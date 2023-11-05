package raccoonman.reterraforged.client.gui.screen.presetconfig;

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

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.JsonOps;

import io.netty.util.internal.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.Toasts;
import raccoonman.reterraforged.client.gui.Tooltips;
import raccoonman.reterraforged.client.gui.screen.page.BisectedPage;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Label;
import raccoonman.reterraforged.client.gui.widget.WidgetList;
import raccoonman.reterraforged.client.gui.widget.WidgetList.Entry;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.platform.ConfigUtil;

class SelectPresetPage extends BisectedPage<PresetConfigScreen, PresetEntry, AbstractWidget> {
	private static final Path CONFIG_PATH = ConfigUtil.getConfigPath();

	private static final Path RTF_CONFIG_PATH = CONFIG_PATH.resolve(ReTerraForged.MOD_ID);
	private static final Path LEGACY_CONFIG_PATH = CONFIG_PATH.resolve(ReTerraForged.LEGACY_MOD_ID);
	
	private static final Path RTF_PRESET_PATH = RTF_CONFIG_PATH.resolve("presets");
	private static final Path LEGACY_PRESET_PATH = LEGACY_CONFIG_PATH.resolve("presets");
	
	private static final Path RTF_DATAPACK_PATH = RTF_CONFIG_PATH.resolve("datapacks");
	
	private static final Predicate<String> IS_VALID = Pattern.compile("^[A-Za-z0-9\\-_ ]+$").asPredicate();

	private EditBox input;
	private Button createPreset;
	private Button deletePreset;
	private Button exportPreset;
	private Button copyPreset;
	private Button importLegacyPresets;
	private Button openPresetFolder;
	
	public SelectPresetPage(PresetConfigScreen screen) {
		super(screen);
		
		try {
			if(!Files.exists(RTF_CONFIG_PATH)) Files.createDirectory(RTF_CONFIG_PATH);
			if(!Files.exists(RTF_PRESET_PATH)) Files.createDirectory(RTF_PRESET_PATH);
			if(!Files.exists(RTF_DATAPACK_PATH)) Files.createDirectory(RTF_DATAPACK_PATH);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_SELECT_PRESET_TITLE);
	}

	@Override
	public void init() {
		super.init();
		
		this.input = PresetWidgets.createEditBox(this.screen.font, (text) -> {
			boolean isValid = this.isValidPresetName(text);
			final int white = 14737632;
			final int red = 0xFFFF3F30;
			this.createPreset.active = isValid;
			this.input.setTextColor(isValid ? white : red);
		});
		this.createPreset = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_CREATE, () -> {
			new PresetEntry(Component.literal(this.input.getValue()), Preset.makeDefault(), false).save();
			this.rebuildPresets();
			this.input.setValue(StringUtil.EMPTY_STRING);
		});
		this.createPreset.active = this.isValidPresetName(this.input.getValue());
		this.copyPreset = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_COPY, () -> {
			PresetEntry preset = this.left.getSelected().getWidget();
			String name = preset.getName().getString();
			int counter = 1;
			String uniqueName;
			while(Files.exists(RTF_PRESET_PATH.resolve((uniqueName = name + " (" + counter + ")") + ".json"))) { 
				counter++;
			}
			new PresetEntry(Component.literal(uniqueName), preset.getPreset().copy(), false).save();
			this.rebuildPresets();
		});
		this.deletePreset = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_DELETE, () -> {
			PresetEntry preset = this.left.getSelected().getWidget();
			Files.delete(preset.getPath());
			this.rebuildPresets();
		});
		this.exportPreset = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_EXPORT, () -> {
			PresetEntry preset = this.left.getSelected().getWidget();
			Path path = RTF_DATAPACK_PATH.resolve(preset.getName().getString() + ".zip");
			this.screen.exportAsDatapack(path, preset);
			this.rebuildPresets();
			
			Toasts.notify(RTFTranslationKeys.GUI_BUTTON_EXPORT_SUCCESS, Component.literal(path.toString()), SystemToastIds.WORLD_BACKUP);
		});
		this.importLegacyPresets = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY, () -> {
			for(Path from : Files.list(LEGACY_PRESET_PATH).toList()) {
				String fromStr = from.toString();
				Path target = RTF_PRESET_PATH.resolve(FileNameUtils.getBaseName(fromStr) + " (Legacy)." + FilenameUtils.getExtension(fromStr));
				Files.copy(from, target);
			}
			this.rebuildPresets();
		});
		this.openPresetFolder = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_OPEN_PRESET_FOLDER, () -> {
			Util.getPlatform().openUri(RTF_PRESET_PATH.toUri());
			this.rebuildPresets();
		});
		
		try {
			// this probably shouldn't go here
			if(!Files.exists(LEGACY_PRESET_PATH) || PathUtils.isEmptyDirectory(LEGACY_PRESET_PATH)) {
				this.importLegacyPresets.active = false;
				this.importLegacyPresets.setTooltip(Tooltips.create(RTFTranslationKeys.GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS));
			}
			
			this.rebuildPresets();
		} catch (IOException e) {
			//TODO: toast here
			e.printStackTrace();
		}
		
		this.right.addWidget(this.input);
		this.right.addWidget(this.createPreset);
		this.right.addWidget(this.copyPreset);
		this.right.addWidget(this.deletePreset);
		this.right.addWidget(this.exportPreset);
		this.right.addWidget(this.importLegacyPresets);
		this.right.addWidget(this.openPresetFolder);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.empty();
	}

	@Override
	public Optional<Page> next() {
		return Optional.ofNullable(this.left).map(WidgetList::getSelected).map((e) -> {
			PresetEntry entry = e.getWidget();
			return !entry.isBuiltin() ? new WorldSettingsPage(this.screen, entry) : null;
		});
	}
	
	@Override
	public void onDone() {
		super.onDone();
		
		Entry<PresetEntry> selected = this.left.getSelected();
		if(selected != null) {
			try {
				this.screen.applyPreset(selected.getWidget());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void selectPreset(@Nullable PresetEntry entry) {
		boolean active = entry != null;
		boolean modifiable = active && !entry.isBuiltin();
		
		this.screen.doneButton.active = active;
		this.copyPreset.active = active;
		this.deletePreset.active = modifiable;
		this.exportPreset.active = active;
		this.screen.nextButton.active = modifiable;
	}
	
	private void rebuildPresets() throws IOException {
		this.selectPreset(null);
		
		List<PresetEntry> entries = new ArrayList<>();
		entries.add(new PresetEntry(Component.translatable(RTFTranslationKeys.GUI_DEFAULT_PRESET_NAME).withStyle(ChatFormatting.GRAY), Preset.makeDefault(), true));
		for(Path presetPath : Files.list(RTF_PRESET_PATH)
			.filter(Files::isRegularFile)
			.toList()
		) {
			try(Reader reader = Files.newBufferedReader(presetPath)) {
				String base = FileNameUtils.getBaseName(presetPath.toString());
				DataResult<Preset> result = Preset.DIRECT_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader));
				Optional<PartialResult<Preset>> error = result.error();
				if(error.isPresent()) {
					ReTerraForged.LOGGER.error(error.get().message());
					continue;
				}
				Preset preset = result.result().get();
				entries.add(new PresetEntry(Component.literal(base), preset, false));
			}
		}
		this.left.replaceEntries(entries.stream().map(WidgetList.Entry::new).toList());
	}
	
	private boolean isValidPresetName(String text) {
		return IS_VALID.test(text) && !this.hasPresetWithName(text);
	}
	
	private boolean hasPresetWithName(String name) {
		return this.left.children().stream().filter((entry) -> {
			return entry.getWidget().getName().getString().equals(name);
		}).findAny().isPresent();
	}
	
	class PresetEntry extends Label {
		private Component name;
		private Preset preset;
		private boolean builtin;
		
		public PresetEntry(Component name, Preset preset, boolean builtin) {
			super(-1, -1, -1, -1, (b) -> {
				if(b instanceof PresetEntry entry) {
					SelectPresetPage.this.selectPreset(entry);
				}
			}, name);
			
			this.name = name;
			this.preset = preset;
			this.builtin = builtin;
		}

		public Component getName() {
			return this.name;
		}
		
		public Preset getPreset() {
			return this.preset;
		}
		
		public boolean isBuiltin() {
			return this.builtin;
		}
		
		public Path getPath() {
			return RTF_PRESET_PATH.resolve(this.name.getString() + ".json");
		}
		
		//FIXME delete old pack before save
		public void save() throws IOException {
			if(!this.builtin) {
				try(
					Writer writer = Files.newBufferedWriter(this.getPath());
					JsonWriter jsonWriter = new JsonWriter(writer);
				) {
					JsonElement element = Preset.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, this.preset).result().orElseThrow();
					jsonWriter.setSerializeNulls(false);
					jsonWriter.setIndent("  ");
					GsonHelper.writeValue(jsonWriter, element, null);
				}
			}
		}
	}
}
