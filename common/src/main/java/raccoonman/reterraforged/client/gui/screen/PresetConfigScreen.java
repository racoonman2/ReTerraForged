package raccoonman.reterraforged.client.gui.screen;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.GsonHelper;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.ColumnAlignment;
import raccoonman.reterraforged.client.gui.Toasts;
import raccoonman.reterraforged.client.gui.Tooltips;
import raccoonman.reterraforged.client.gui.UnsizedWidgets;
import raccoonman.reterraforged.client.gui.widget.Label;
import raccoonman.reterraforged.client.gui.widget.WidgetList;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.registries.data.preset.Preset;
import raccoonman.reterraforged.platform.config.ConfigUtil;

// this code is awful lmao
// gui programming is an abysmal experience
public class PresetConfigScreen extends MultiPageScreen {
	private CreateWorldScreen parent;
	@Nullable
	private SelectPresetPage.PresetLabel selectedPreset;
	
	public PresetConfigScreen(CreateWorldScreen parent, WorldCreationContext settings) {
		super(Component.empty());
		
		this.parent = parent;
		
		this.addPage(new PresetConfigScreen.SelectPresetPage());
		this.addPage(new PresetConfigScreen.DummyPage());
		this.addPage(new PresetConfigScreen.DummyPage());
	}
	
	@Override
	protected void init() {
		super.init();
		
		// this doesn't feel like a good way to do this
		this.done.setMessage(Component.translatable(RTFTranslationKeys.PRESET_CONFIG_APPLY_PRESET));
	}
	
	@Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

	@Override
	protected void onDone() {
		this.onClose();
		
		Toasts.tryOrToast(Tooltips.failTranslationKey(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_EXPORT), () -> {
			Pair<Path, PackRepository> settings = this.parent.getDataPackSelectionSettings(this.parent.getUiState().getSettings().dataConfiguration());
			Path datapackPath = settings.getFirst();
			PackRepository repository = settings.getSecond();

			Path path = this.selectedPreset.getExportPath(datapackPath);
			this.selectedPreset.preset.exportAsDatapack(this.parent.getUiState().getSettings().worldgenLoadContext(), path);
			repository.reload();
			if(repository.addPack("file/" + path.getFileName())) {
				this.parent.tryApplyNewDataPacks(repository, false, (c) -> {
				});
			}
		});
	}
	
	@Override
	protected boolean hasNext() {
		return super.hasNext() && this.selectedPreset != null;
	}

	private abstract class BasePage<L extends AbstractWidget, R extends AbstractWidget> implements Page {
		protected WidgetList<L> left;
		protected WidgetList<R> right;
		private Component title;
		
		public BasePage(Component title) {
			this.title = title;
		}
		
		@Override
		public Component title() {
			return this.title;
		}
		
		@Override
		public void init() {
			ColumnAlignment alignment = new ColumnAlignment(PresetConfigScreen.this, 4, 0, 10, 30);
			this.left = alignment.addColumn(0.7F, this::createAndPositionColumn);
			this.right = alignment.addColumn(0.3F, this::createAndPositionColumn);
		}
		
		private <T extends AbstractWidget> WidgetList<T> createAndPositionColumn(int left, int top, int columnWidth, int height, int horizontalPadding, int verticalPadding) {
			final int padding = 30;
			final int slotHeight = 25;
			WidgetList<T> list = new WidgetList<>(PresetConfigScreen.this.minecraft, columnWidth, height, padding, height - padding, slotHeight);
			list.setLeftPos(left);
			return list;
		}
	}
	
	private class SelectPresetPage extends PresetConfigScreen.BasePage<SelectPresetPage.PresetLabel, AbstractWidget> {
		private static final Predicate<String> IS_VALID = Pattern.compile("^[A-Za-z0-9\\-_ ]+$").asPredicate();
		private static final Path ROOT_CONFIG_PATH = ConfigUtil.getConfigPath();
		private static final Path LEGACY_CONFIG_PATH = ROOT_CONFIG_PATH.resolve("terraforged");
		private static final Path LEGACY_PRESET_PATH = LEGACY_CONFIG_PATH.resolve("presets");
		private static final Path CONFIG_PATH = ROOT_CONFIG_PATH.resolve(ReTerraForged.MOD_ID);
		private static final Path PRESET_PATH = CONFIG_PATH.resolve("presets");
		private static final Path DATAPACK_PATH = CONFIG_PATH.resolve("datapacks");

		private EditBox input;
		private Button create;
		private Button export;
		private Button copy;
		private Button delete;
		private Button importLegacy;
		
		public SelectPresetPage() {
			super(Component.translatable(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_TITLE));
			
			//ew
			try {
				if(!Files.exists(CONFIG_PATH)) Files.createDirectory(CONFIG_PATH);
				if(!Files.exists(PRESET_PATH)) Files.createDirectory(PRESET_PATH);
				if(!Files.exists(DATAPACK_PATH)) Files.createDirectory(DATAPACK_PATH);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void init() {
			super.init();
			
			this.left.setRenderSelection(true);
			// only activate done if a preset is selected
			PresetConfigScreen.this.done.active = false;
			
			this.input = UnsizedWidgets.createEditBox(PresetConfigScreen.this.font);
			this.input.setResponder((text) -> {
				boolean isValid = IS_VALID.test(text) && !this.hasPreset(text);
				final int white = 14737632;
				final int red = 0xFFFF3F30;
				this.create.active = isValid;
				this.input.setTextColor(isValid ? white : red);
			});
			this.create = UnsizedWidgets.createReportingButton(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_CREATE, () -> {
				Path path = PRESET_PATH.resolve(this.input.getValue() + ".json");
				Files.createFile(path);
				writePreset(path, Preset.DEFAULT);
				this.buildPresetList();
				this.input.setValue(StringUtils.EMPTY);
			});
			this.create.active = !this.input.getValue().isBlank();
			this.export = UnsizedWidgets.createReportingButton(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_EXPORT, () -> {
				Path path = PresetConfigScreen.this.selectedPreset.getExportPath(DATAPACK_PATH);
				PresetConfigScreen.this.selectedPreset.preset.exportAsDatapack(PresetConfigScreen.this.parent.getUiState().getSettings().worldgenLoadContext(), path);
				Toasts.notify(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_EXPORT_SUCCESS, Component.literal(path.toString()), SystemToastIds.WORLD_BACKUP);
			});
			this.copy = UnsizedWidgets.createReportingButton(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_COPY, () -> {
				Files.copy(
					PresetConfigScreen.this.selectedPreset.getPath(), 
					getUniquePath(PRESET_PATH, PresetConfigScreen.this.selectedPreset.getPresetName())
				);
				this.buildPresetList();
			});
			this.delete = UnsizedWidgets.createReportingButton(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_DELETE, () -> {
				Files.delete(
					PresetConfigScreen.this.selectedPreset.getPath()
				);
				this.buildPresetList();
			});
			this.importLegacy = UnsizedWidgets.createReportingButton(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_IMPORT_LEGACY, () -> {
				for(Path legacyPresetPath : Files.list(LEGACY_PRESET_PATH).filter(Files::isRegularFile).toList()) {
					Path targetPresetPath = getUniquePath(PRESET_PATH, FilenameUtils.removeExtension(legacyPresetPath.getFileName().toString()) + " (Legacy)");
					Files.copy(legacyPresetPath, targetPresetPath);
				}
				this.buildPresetList();
			});
			
			if(Files.notExists(LEGACY_PRESET_PATH)) {
				this.importLegacy.active = false;
				this.importLegacy.setTooltip(Tooltips.create(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_IMPORT_LEGACY_DISABLED));
			}

			this.right.addWidget(this.input);
			this.right.addWidget(this.create);
			this.right.addWidget(this.export);
			this.right.addWidget(this.copy);
			this.right.addWidget(this.delete);
			this.right.addWidget(this.importLegacy);

			this.buildPresetList();
		}
		
		public void selectPreset(PresetLabel preset) {
			PresetConfigScreen.this.selectedPreset = preset;
			this.left.select(preset);
			boolean active = PresetConfigScreen.this.selectedPreset != null;
			this.export.active = active;
			this.copy.active = active;
			this.delete.active = active;
			PresetConfigScreen.this.next.active = PresetConfigScreen.this.hasNext();
			PresetConfigScreen.this.done.active = active;
		}
		
		private boolean hasPreset(String presetName) {
			return this.left.children().stream().filter((widget) -> {
				return widget.getWidget().getPresetName().equals(presetName);
			}).findAny().isPresent();
		}
		
		private void buildPresetList() {
			// reset the selected preset if we rebuild the list
			this.selectPreset(null);
			
			List<WidgetList.Entry<PresetLabel>> widgets = new ArrayList<>();
			
			Toasts.tryOrToast(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_LOAD_FAIL, () -> {
				for(Path presetPath : Files.list(PRESET_PATH).filter(Files::isRegularFile).filter((path) -> path.getFileName().toString().endsWith(".json")).toList()) {
					String presetName = FilenameUtils.removeExtension(presetPath.getFileName().toString());
					Preset preset = readPreset(presetPath);
					
					widgets.add(new WidgetList.Entry<>(new SelectPresetPage.PresetLabel(presetName, preset)));
				}
			});
			
			this.left.replaceEntries(widgets);
		}
		
		private static Path getUniquePath(Path presetListPath, String name) {
			int counter = 0;
			Path target;
			while(Files.exists(target = presetListPath.resolve((counter == 0 ? name : name + " (" + counter + ")") + ".json"))) { 
				counter++;
			}
			return presetListPath.resolve(target);
		}
		
		private static Preset readPreset(Path path) throws IOException {
			try(Reader reader = Files.newBufferedReader(path)) {
				DataResult<Pair<Preset, JsonElement>> result = Preset.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseReader(reader));
				Optional<PartialResult<Pair<Preset, JsonElement>>> error = result.error();
				
				if(error.isPresent()) {
					throw new JsonIOException(error.get().message());
				}
				
				return result.result().get().getFirst();
			}
		}
		
		private static void writePreset(Path path, Preset preset) throws IOException {
			try(Writer writer = Files.newBufferedWriter(path)) {
				DataResult<JsonElement> result = Preset.CODEC.encodeStart(JsonOps.INSTANCE, preset);
				Optional<PartialResult<JsonElement>> error = result.error();
				if(error.isPresent()) {
					throw new JsonIOException(error.get().message());
				}
                try (JsonWriter jsonWriter = new JsonWriter(writer)) {
                    jsonWriter.setSerializeNulls(false);
                    jsonWriter.setIndent("  ");
                    GsonHelper.writeValue(jsonWriter, result.result().get(), null);
                }
			}
		}
		
		public class PresetLabel extends Label {
			private Preset preset;

			public PresetLabel(String presetName, Preset preset) {
		    	super(0, 0, 20, 20, (self) -> {
		    		if(self instanceof PresetLabel label) {
			    		PresetConfigScreen.SelectPresetPage.this.selectPreset(label);
		    		}
		    	}, Component.literal(presetName));
		    	this.preset = preset;
			}
			
			public String getPresetName() {
				return this.getMessage().getString();
			}
			
			public Path getPath() {
				return PRESET_PATH.resolve(this.getPresetName() + ".json");
			}
			
			public Path getExportPath(Path root) {
				return root.resolve(this.getPresetName() + ".zip");
			}
		}
	}
	
	private class DummyPage extends BasePage<AbstractWidget, AbstractWidget> {

		public DummyPage() {
			super(Component.literal("DUMMY"));
		}
	}
}
