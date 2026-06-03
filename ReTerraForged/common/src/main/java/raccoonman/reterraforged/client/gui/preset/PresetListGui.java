package raccoonman.reterraforged.client.gui.preset;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.RegistryEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.GsonHelper;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.client.gui.SetGuiCallback;
import raccoonman.reterraforged.client.gui.widget.Label;
import raccoonman.reterraforged.client.gui.widget.Toast;
import raccoonman.reterraforged.client.gui.widget.WidgetList;
import raccoonman.reterraforged.data.RTFDataGen;
import raccoonman.reterraforged.data.RTFTranslationKeys;
import raccoonman.reterraforged.platform.ConfigUtil;
import raccoonman.reterraforged.preset.BuiltinPresets;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.PresetCodec;
import raccoonman.reterraforged.preset.option.Page;
import raccoonman.reterraforged.preset.pages.BiomeOptions;
import raccoonman.reterraforged.preset.pages.CaveOptions;
import raccoonman.reterraforged.preset.pages.ClimateOptions;
import raccoonman.reterraforged.preset.pages.StructureOptions;
import raccoonman.reterraforged.preset.pages.TerrainOptions;
import raccoonman.reterraforged.preset.pages.WorldOptions;

public class PresetListGui extends PresetGui<PresetListGui.Entry, AbstractWidget> implements PackChangeListener {
	private static final Path PRESET_PATH = ConfigUtil.PRESET_PATH;
	private static final Path EXPORT_PATH = ConfigUtil.EXPORT_PATH;
	private static final Predicate<String> PRESET_NAME_VALIDATOR = Pattern.compile("^[A-Za-z0-9\\-_ ]+$").asPredicate();
	private static final int WHITE = -2039584;
	private static final int RED = 0xFFFF3F30;
	
	private SetGuiCallback setGuiCallback;
	private List<Page> pages;
	private Codec<Preset> presetCodec;

	private EditBox inputBox;
	private Button createButton;
	private Button copyButton;
	private Button renameButton;
	private Button deleteButton; 
	private Button exportButton;
	
	@Nullable
	private FileWatcher fileWatcher;
	
	private StructureOptions.Placements structurePlacements;
	
	public PresetListGui(CreateWorldScreen screen, SetGuiCallback setGuiCallback) {
		super(screen);

		this.setGuiCallback = setGuiCallback;
		this.onPackUpdated();
	}

	@Override
	public void init(ScreenRectangle area) {
		super.init(area);
		
		this.inputBox = this.right.add((x, y, width, height) -> {
			EditBox editBox = new EditBox(this.screen.font, x, y, width, height, this.inputBox, CommonComponents.EMPTY);
			editBox.setResponder(this::validateInputBox);
			editBox.setHint(Component.translatable(RTFTranslationKeys.CREATE_PRESET_PROMPT).withStyle(ChatFormatting.DARK_GRAY));
			return editBox;
		});
		this.createButton = this.addButton(RTFTranslationKeys.CREATE_PRESET, (v) -> this.onSave());
		this.createButton.active = this.isValidName(this.inputBox.getValue());
		this.copyButton = this.addButton(RTFTranslationKeys.COPY_PRESET, (v) -> this.onCopy());
		this.renameButton = this.addButton(RTFTranslationKeys.RENAME_PRESET, (v) -> this.onRename());
		this.deleteButton = this.addButton(RTFTranslationKeys.DELETE_PRESET, (v) -> this.onDelete());
		this.exportButton = this.addButton(RTFTranslationKeys.EXPORT_PRESET, (v) -> this.onExport());
		this.addButton(RTFTranslationKeys.OPEN_PRESET_FOLDER, (v) -> Util.getPlatform().openPath(PRESET_PATH));
		this.addButton(RTFTranslationKeys.OPEN_EXPORT_FOLDER, (v) -> Util.getPlatform().openPath(EXPORT_PATH));
		
		this.lastButton.active = false;
		this.left.setSelectionCallback((entry) -> this.selectPreset(entry != null ? entry.getWidget() : null));
		this.left.setRenderSelected(true);
		this.left.setSelected(null);
		
		try {
			if(this.fileWatcher == null) {
				this.fileWatcher = this.makeFileWatcher();
			}

			this.loadPresets();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void tick() {
		try {
			this.fileWatcher = FileWatcher.tick(this.fileWatcher);
		} catch (IOException e) {
			e.printStackTrace();
			this.fileWatcher = null;
		}
	}

	@Override
	public void close() {
		if(this.fileWatcher != null) {
			try {
				this.fileWatcher.close();
				this.fileWatcher = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onNext() {
		PresetListGui.Entry selectedPreset = this.getSelectedPreset();
		this.setGuiCallback.accept(new PresetEditorGui(this, selectedPreset));
	}
	
	@Override
	public void onApply() {
		PresetListGui.Entry selectedPreset = this.getSelectedPreset();
		if(selectedPreset != null) {
			try {
				selectedPreset.apply();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onPackUpdated() {
		RegistryAccess registryAccess = this.screen.getUiState().getSettings().worldgenLoadContext();

		this.structurePlacements = StructureOptions.Placements.make(registryAccess);
		this.pages = ImmutableList.of(
			WorldOptions.PAGE,
			TerrainOptions.PAGE,
			ClimateOptions.PAGE,
			BiomeOptions.PAGE,
			CaveOptions.PAGE,
			StructureOptions.makePage(this.structurePlacements)
		);
		
		Codec<Preset> newCodec = new PresetCodec(this.pages);
		PresetListGui.Entry selectedPreset;
		if(this.presetCodec != null && (selectedPreset = this.getSelectedPreset()) != null) {
			selectedPreset.getPreset()
				.updateOptionReferences(this.presetCodec, newCodec, JavaOps.INSTANCE)
				.resultOrPartial(RTFCommon.LOGGER::error)
				.ifPresent(selectedPreset::setPreset);
		}
		this.presetCodec = newCodec;
	}

	@Override
	public Component getTitle() {
		return Component.translatable(RTFTranslationKeys.SELECT_PRESET_TITLE);
	}
	
	public SetGuiCallback setGuiCallback() {
		return this.setGuiCallback;
	}
	
	public List<Page> pages() {
		return this.pages;
	}
	
	public StructureOptions.Placements structurePlacements() {
		return this.structurePlacements;
	}

	public RegistryAccess dimensionRegistries() {
		WorldCreationContext settings = this.screen.getUiState().getSettings();
		return new RegistryAccess.ImmutableRegistryAccess(
			Stream.concat(
				settings.worldgenLoadContext().registries().map(RegistryEntry::value),
				Stream.of(settings.datapackDimensions())
			).toList()
		);
	}
	
	private void onSave() throws IOException {
		this.savePreset(Preset.make(), presetPath(this.inputBox.getValue()));
		this.inputBox.setValue(StringUtils.EMPTY);
	}
	
	private void onCopy() throws IOException {
		PresetListGui.Entry selectedPreset = this.getSelectedPreset();
		selectedPreset.copy();
	}
	
	private void onRename() throws IOException {
		PresetListGui.Entry selectedPreset = this.getSelectedPreset();
		selectedPreset.rename(this.inputBox.getValue());
		this.inputBox.setValue(StringUtils.EMPTY);
		this.selectPreset(null);
	}

	private void onDelete() throws IOException {
		PresetListGui.Entry selectedPreset = this.getSelectedPreset();
		selectedPreset.delete();
		this.selectPreset(null);
	}
	
	private void onExport() throws IOException {
		PresetListGui.Entry selectedPreset = this.getSelectedPreset();
		Path path = EXPORT_PATH.resolve(selectedPreset.getDatapackName());
		selectedPreset.export(path);
		Toast.addToast(RTFTranslationKeys.GUI_EXPORT_PRESET_SUCCESS, Component.literal(path.toString()), SystemToastId.WORLD_BACKUP);
	}

	private PresetListGui.Entry getSelectedPreset() {
		WidgetList.Entry<PresetListGui.Entry> entry = this.left.getSelected();
		return entry != null ? entry.getWidget() : null;
	}
	
	private void loadPresets() throws IOException {
		this.left.clearEntries();
		
		BuiltinPresets.REGISTRY.forEach((pair) -> {
			Component title = pair.getFirst();
			Preset second = pair.getSecond();
			this.addPresetEntry(title, second, Optional.empty());
		});

		for(Path path : Files.list(PRESET_PATH).filter(Files::isRegularFile).toList()) {
			this.loadPreset(path).resultOrPartial(RTFCommon.LOGGER::error);
		}
	}
	
	private DataResult<PresetListGui.Entry> loadPreset(Path path) throws IOException {
		try(Reader reader = Files.newBufferedReader(path)) {
			JsonElement element = JsonParser.parseReader(reader);
			return this.presetCodec.parse(JsonOps.INSTANCE, element).map((preset) -> {
				String base = FileNameUtils.getBaseName(path);
				Component title = Component.literal(base);
				return this.addPresetEntry(title, preset, Optional.of(path));
			});
		}
	}

	private void savePreset(Preset preset, Path path) throws IOException {
		try(
			Writer writer = Files.newBufferedWriter(path);
			JsonWriter jsonWriter = new JsonWriter(writer);
		) {
			JsonElement element = PresetListGui.this.presetCodec.encodeStart(JsonOps.INSTANCE, preset).result().orElseThrow();
			jsonWriter.setSerializeNulls(false);
			jsonWriter.setIndent("  ");
			GsonHelper.writeValue(jsonWriter, element, null);
		}
	}
	
	private boolean isValidName(String text) {
		return PRESET_NAME_VALIDATOR.test(text) && this.left.children().stream().filter((entry) -> {
			return entry.getWidget().getMessage().getString().equals(text);
		}).findAny().isEmpty();
	}
	
	private void validateInputBox(String text) {
		boolean isValid = this.isValidName(text);
		this.createButton.active = isValid;
		
		PresetListGui.Entry selectedPreset = this.getSelectedPreset();
		this.renameButton.active = selectedPreset != null && !selectedPreset.isBuiltIn() && isValid;
		this.inputBox.setTextColor(isValid ? WHITE : RED);
	}
	
	private void selectPreset(PresetListGui.Entry preset) {
		boolean isSelected = preset != null;
		boolean isUserPreset = isSelected && !preset.isBuiltIn();
		
		this.applyButton.active = isSelected;
		this.copyButton.active = isSelected;
		this.renameButton.active = isUserPreset;
		this.deleteButton.active = isUserPreset;
		this.exportButton.active = isSelected;
		this.nextButton.active = isUserPreset;
		
		this.validateInputBox(this.inputBox.getValue());
	}

	private interface OnPress {
		void onPress(Button button) throws Exception;
	}
	
	private Button addButton(String translationKey, PresetListGui.OnPress onPress) {
		return this.right.add((x, y, width, height) -> {
			return Button.builder(Component.translatable(translationKey), (button) -> {
				Toast.tryOrToast(RTFTranslationKeys.tooltipFailKey(translationKey), () -> onPress.onPress(button));
			}).bounds(x, y, width, height).build();
		});
	}

	private PresetListGui.Entry addPresetEntry(Component title, Preset preset, Optional<Path> path) {
		return this.left.add((x, y, width, height) -> {
			return new PresetListGui.Entry(x, y, width, height, title, preset, path);
		});
	}
	
	private FileWatcher makeFileWatcher() throws IOException {
		ConfigUtil.makeChildDirectories(PRESET_PATH, EXPORT_PATH);

		FileWatcher fileWatcher = FileWatcher.register(PRESET_PATH);
		fileWatcher.registerCallback(StandardWatchEventKinds.ENTRY_CREATE, (v, path) -> {
			try {
				this.loadPreset(path).resultOrPartial(RTFCommon.LOGGER::error).ifPresent((e) -> {
					RTFCommon.LOGGER.info("Created file: {}", path);
				});
			} catch (IOException e) {
				RTFCommon.LOGGER.error(e);
			}
		});
		
		fileWatcher.registerCallback(StandardWatchEventKinds.ENTRY_DELETE, (v, path) -> {
			RTFCommon.LOGGER.info("Deleted file: {}", path);
			
			WidgetList.Entry<PresetListGui.Entry> deletedEntry = null;
			for(WidgetList.Entry<PresetListGui.Entry> widgetEntry : this.left.children()) {
				PresetListGui.Entry entry = widgetEntry.getWidget();
				Optional<Path> presetPath = entry.getPath();
				if(presetPath.isEmpty() || !presetPath.get().equals(path)) {
					continue;
				}
				deletedEntry = widgetEntry;
			}
				
			if(deletedEntry != null) {
				this.left.removeEntry(deletedEntry);
			}
		});
		return fileWatcher;
	}
	
	private static Path presetPath(String name) {
		return PRESET_PATH.resolve(name + ".json");
	}
	
	public class Entry extends Label {
		private Preset preset;
		private Optional<Path> path;
		
		public Entry(int x, int y, int width, int height, Component title, Preset preset, Optional<Path> path) {
			super(x, y, width, height, title, true);
			
			this.preset = preset;
			this.path = path;
			
			preset.getDescription()
				.map(Tooltip::create)
				.ifPresent(this::setTooltip);
		}
		
		public void setPreset(Preset preset) {
			this.preset = preset;
		}
		
		public Preset getPreset() {
			return this.preset;
		}

		public boolean isBuiltIn() {
			return this.path.isEmpty();
		}
		
		public Optional<Path> getPath() {
			return this.path;
		}
		
		public String getDatapackName() {
			return this.getMessage().getString() + ".zip";
		}
		
		public void save() throws IOException {
			if(this.path.isPresent()) {
				PresetListGui.this.savePreset(this.preset, this.path.get());
			}
		}

		public void copy() throws IOException {
			String name = this.getMessage().getString();
			Path path = presetPath(name + " (Copy)");
			PresetListGui.this.savePreset(this.preset, path);
		}
		
		public void rename(String name) throws IOException {
			Path oldPath = this.getPath().get();
			Path newPath = presetPath(name);
			Files.move(oldPath, newPath);
		}
		
		public void delete() throws IOException {
			if(this.path.isPresent()) {
				Files.delete(this.path.get());
			}
		}
		
		public void export(Path path) throws IOException {
			String presetName = this.getMessage().getString();
			RTFDataGen.generateDataPack(path, presetName, this.preset, PresetListGui.this.presetCodec,  PresetListGui.this.structurePlacements, CompletableFuture.completedFuture(PresetListGui.this.dimensionRegistries()));
		}
		
		public void apply() throws IOException {
			WorldCreationUiState uiState = PresetListGui.this.screen.getUiState();
			WorldCreationContext settings = uiState.getSettings();
			Pair<Path, PackRepository> path = PresetListGui.this.screen.getDataPackSelectionSettings(settings.dataConfiguration());
			PackRepository repository = path.getSecond();
			Path exportPath = path.getFirst().resolve(this.getDatapackName());
			this.export(exportPath);
			repository.reload();
			String pack = "file/" + exportPath.getFileName();
			repository.removePack(pack);
			if(repository.addPack(pack)) {
				DataPackLoader.tryApplyDataPacks(uiState, repository);
			}
		}
	}
}
