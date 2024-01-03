package raccoonman.reterraforged.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPools {
	public static final ExecutorService WORLD_GEN = Executors.newFixedThreadPool(availableProcessors());
	
	public static int availableProcessors() {
		return Math.max(2, Runtime.getRuntime().availableProcessors());
	}
}