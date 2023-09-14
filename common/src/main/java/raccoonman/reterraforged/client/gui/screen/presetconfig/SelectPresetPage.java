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
import com.mojang.serialization.JsonOps;

import io.netty.util.internal.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.Tooltips;
import raccoonman.reterraforged.client.gui.screen.presetconfig.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Label;
import raccoonman.reterraforged.client.gui.widget.WidgetList;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.data.preset.Preset;
import raccoonman.reterraforged.platform.config.ConfigUtil;

public class SelectPresetPage extends BisectedPage<PresetConfigScreen, PresetEntry, AbstractWidget> {
	private static final Predicate<String> IS_VALID = Pattern.compile("^[A-Za-z0-9\\-_ ]+$").asPredicate();

	private static final Path CONFIG_PATH = ConfigUtil.getConfigPath();

	private static final Path RTF_CONFIG_PATH = CONFIG_PATH.resolve(ReTerraForged.MOD_ID);
	private static final Path LEGACY_CONFIG_PATH = CONFIG_PATH.resolve(ReTerraForged.LEGACY_MOD_ID);
	
	private static final Path RTF_PRESET_PATH = RTF_CONFIG_PATH.resolve("presets");
	private static final Path LEGACY_PRESET_PATH = LEGACY_CONFIG_PATH.resolve("presets");
	
	private EditBox inputBox;
	private Button createPresetButton,
				   deletePresetButton,
				   copyPresetButton,
				   importLegacyPresetsButton;
	
	public SelectPresetPage(PresetConfigScreen screen) {
		super(screen);
		
		try {
			if(!Files.exists(RTF_CONFIG_PATH)) Files.createFile(RTF_CONFIG_PATH);
			if(!Files.exists(RTF_PRESET_PATH)) Files.createFile(RTF_PRESET_PATH);
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
		
		this.inputBox = PresetWidgets.createEditBox(this.screen.font, (text) -> {
			boolean isValid = this.isValidPresetName(text);
			final int white = 14737632;
			final int red = 0xFFFF3F30;
			this.createPresetButton.active = isValid;
			this.inputBox.setTextColor(isValid ? white : red);
		});
		this.createPresetButton = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_CREATE, () -> {
			this.createPreset(this.inputBox.getValue());
			this.inputBox.setValue(StringUtil.EMPTY_STRING);
		});
		this.createPresetButton.active = this.isValidPresetName(this.inputBox.getValue());
		this.copyPresetButton = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_COPY, () -> this.copyPreset(this.left.getSelected().getWidget()));
		this.deletePresetButton = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_DELETE, () -> this.deletePreset(this.left.getSelected().getWidget()));
		this.importLegacyPresetsButton = PresetWidgets.createThrowingButton(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY, this::importLegacyPresets);
		
		try {
			// this probably shouldn't go here
			if(!Files.exists(LEGACY_PRESET_PATH) || PathUtils.isEmptyDirectory(LEGACY_PRESET_PATH)) {
				this.importLegacyPresetsButton.active = false;
				this.importLegacyPresetsButton.setTooltip(Tooltips.create(RTFTranslationKeys.GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS));
			}
			
			this.rebuildPresets();
		} catch (IOException e) {
			//TODO: toast here
			e.printStackTrace();
		}
		
		this.left.setRenderSelection(true);
		
		this.right.addWidget(this.inputBox);
		this.right.addWidget(this.createPresetButton);
		this.right.addWidget(this.copyPresetButton);
		this.right.addWidget(this.deletePresetButton);
		this.right.addWidget(this.importLegacyPresetsButton);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.empty();
	}

	@Override
	public Optional<Page> next() {
		// note: its possible for left to be null when this is called
		return Optional.ofNullable(this.left).map(WidgetList::getSelected).map((e) -> {
			PresetEntry entry = e.getWidget();
			return !entry.isBuiltin() ? new WorldSettingsPage(this.screen, entry) : null;
		});
	}

	private void createPreset(String name) throws IOException {
		new PresetEntry(Component.literal(name), Preset.makeDefault(), false).save();
		this.rebuildPresets();
	}
	
	private void deletePreset(PresetEntry entry) throws IOException {
		Files.delete(entry.getPath());
		this.rebuildPresets();
	}
	
	private void copyPreset(PresetEntry entry) throws IOException {
		String name = entry.getName().getString();
		int counter = 1;
		String uniqueName;
		while(Files.exists(RTF_PRESET_PATH.resolve((uniqueName = name + " (" + counter + ")") + ".json"))) { 
			counter++;
		}
		new PresetEntry(Component.literal(uniqueName), entry.getPreset().copy(), false).save();
		this.rebuildPresets();
	}
	
	private void importLegacyPresets() throws IOException {
		for(Path from : Files.list(LEGACY_PRESET_PATH).toList()) {
			String fromStr = from.toString();
			Path target = RTF_PRESET_PATH.resolve(FileNameUtils.getBaseName(fromStr) + " (Legacy)." + FilenameUtils.getExtension(fromStr));
			Files.copy(from, target);
		}
		this.rebuildPresets();
	}
	
	private void onSelectPreset(@Nullable PresetEntry entry) {
		boolean active = entry != null;
		boolean builtin = active && entry.isBuiltin();
		this.screen.doneButton.active = active;
		this.copyPresetButton.active = active;
		this.deletePresetButton.active = active && !builtin;
		this.screen.nextButton.active = active && !builtin;
	}
	
	//FIXME: don't terminate the loop if parse fails
	private void rebuildPresets() throws IOException {
		this.onSelectPreset(null);
		
		List<PresetEntry> entries = new ArrayList<>();
		entries.add(new PresetEntry(Component.translatable(RTFTranslationKeys.GUI_BEAUTIFUL_PRESET_NAME).withStyle(ChatFormatting.GRAY), Preset.makeDefault(), true));
		for(Path presetPath : Files.list(RTF_PRESET_PATH)
			.filter(Files::isRegularFile)
			.toList()
		) {
			try(Reader reader = Files.newBufferedReader(presetPath)) {
				String base = FileNameUtils.getBaseName(presetPath.toString());
				Preset result = Preset.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).result().orElseThrow();

				entries.add(new PresetEntry(Component.literal(base), result, false));
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
					SelectPresetPage.this.onSelectPreset(entry);
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
		
		public void save() throws IOException {
			if(!this.builtin) {
				try(
					Writer writer = Files.newBufferedWriter(this.getPath());
					JsonWriter jsonWriter = new JsonWriter(writer);
				) {
					JsonElement element = Preset.CODEC.encodeStart(JsonOps.INSTANCE, preset).result().orElseThrow();
					jsonWriter.setSerializeNulls(false);
					jsonWriter.setIndent("  ");
					GsonHelper.writeValue(jsonWriter, element, null);
				}
			}
		}
	}
}
