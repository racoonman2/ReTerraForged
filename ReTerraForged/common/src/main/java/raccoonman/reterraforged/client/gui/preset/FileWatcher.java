package raccoonman.reterraforged.client.gui.preset;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

public class FileWatcher implements AutoCloseable {
	private WatchService watchService;
	private Path path;
	private Map<WatchEvent.Kind<?>, List<Callback>> callbacks;
	
	public FileWatcher(WatchService watchService, Path path) {
		this.watchService = watchService;
		this.path = path;
		this.callbacks = new HashMap<>();
	}
	
	public void registerCallback(WatchEvent.Kind<?> eventKind, Callback callback) {
		this.callbacks.computeIfAbsent(eventKind, (v) -> new ArrayList<>()).add(callback);
	}

	public boolean tick() {
		try {
			WatchKey key = this.watchService.poll();
	        if(key != null) {
	        	for (WatchEvent<?> event : key.pollEvents()) {
	        		if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
	        			continue;
	        		}

	        		if(this.pollEvents(event) && !key.reset()) {
	        			return false;
	        		}
	        	}
	        }
        } catch(Exception e) {
        	e.printStackTrace();
        }
		return true;
	}
	
	@Override
	public void close() throws IOException {
		this.watchService.close();
	}

	private <T> boolean pollEvents(WatchEvent<T> event) throws IOException {
		WatchEvent.Kind<?> eventKind = event.kind();
		if(eventKind == StandardWatchEventKinds.OVERFLOW) {
			return false;
		}

		@SuppressWarnings("unchecked")
		WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
		Path path = this.path.resolve(pathEvent.context());

		List<FileWatcher.Callback> callbacks = this.callbacks.getOrDefault(eventKind, ImmutableList.of());
		for(FileWatcher.Callback callback : callbacks) {
			callback.accept(pathEvent, path);
		}
		return true;
	}
	
	@Nullable
	public static FileWatcher tick(FileWatcher watcher) throws IOException {
		if(watcher == null || watcher.tick()) {
			return watcher;
		}
		watcher.close();
		return null;
	}
	
	public static FileWatcher register(Path path) throws IOException {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		return new FileWatcher(watchService, path);
	}
	
	public interface Callback {
		void accept(WatchEvent<Path> event, Path path);
	}
}
