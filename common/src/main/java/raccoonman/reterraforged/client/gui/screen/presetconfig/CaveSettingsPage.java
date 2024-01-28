package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.PresetListPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.data.preset.settings.CaveSettings;
import raccoonman.reterraforged.data.preset.settings.Preset;

public class CaveSettingsPage extends PresetEditorPage {
	private Slider surfaceDensityThreshold;
	private Slider entranceCaveChance;
	private Slider cheeseCaveChance;
	private Slider spaghettiChance;
	private Slider noodleCaveChance;
	private Slider carverCaveChance;
	private Slider deepCarverCaveChance;
	private Slider ravineChance;
	private CycleButton<Boolean> largeOreVeins;
	private CycleButton<Boolean> legacyCarverDistribution;
	
	public CaveSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}

	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_CAVE_SETTINGS_TITLE);
	}

	@Override
	public void init() {
		super.init();

		Preset preset = this.preset.getPreset();
		CaveSettings caves = preset.caves();

		this.surfaceDensityThreshold = PresetWidgets.createFloatSlider(caves.surfaceDensityThreshold, 1.5625F, 10.0F, RTFTranslationKeys.GUI_SLIDER_SURFACE_DENSITY_THRESHOLD, (slider, value) -> {
			caves.surfaceDensityThreshold = (float) slider.scaleValue(value);
			return value;
		});
		this.entranceCaveChance = PresetWidgets.createFloatSlider(caves.entranceCaveChance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_ENTRANCE_CAVE_CHANCE, (slider, value) -> {
			caves.entranceCaveChance = (float) slider.scaleValue(value);
			return value;
		});
		this.cheeseCaveChance = PresetWidgets.createFloatSlider(caves.cheeseCaveChance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_CHEESE_CAVE_CHANCE, (slider, value) -> {
			caves.cheeseCaveChance = (float) slider.scaleValue(value);
			return value;
		});
		this.spaghettiChance = PresetWidgets.createFloatSlider(caves.spaghettiCaveChance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_SPAGHETTI_CAVE_CHANCE, (slider, value) -> {
			caves.spaghettiCaveChance = (float) slider.scaleValue(value);
			return value;
		});
		this.noodleCaveChance = PresetWidgets.createFloatSlider(caves.noodleCaveChance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_NOODLE_CAVE_CHANCE, (slider, value) -> {
			caves.noodleCaveChance = (float) slider.scaleValue(value);
			return value;
		});
		this.carverCaveChance = PresetWidgets.createFloatSlider(caves.caveCarverChance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_CAVE_CARVER_CHANCE, (slider, value) -> {
			caves.caveCarverChance = (float) slider.scaleValue(value);
			return value;
		});
		this.deepCarverCaveChance = PresetWidgets.createFloatSlider(caves.deepCaveCarverChance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_DEEP_CAVE_CARVER_CHANCE, (slider, value) -> {
			caves.deepCaveCarverChance = (float) slider.scaleValue(value);
			return value;
		});
		this.ravineChance = PresetWidgets.createFloatSlider(caves.ravineCarverChance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_RAVINE_CARVER_CHANCE, (slider, value) -> {
			caves.ravineCarverChance = (float) slider.scaleValue(value);
			return value;
		});
		this.largeOreVeins = PresetWidgets.createToggle(caves.largeOreVeins, RTFTranslationKeys.GUI_BUTTON_LARGE_ORE_VEINS, (button, value) -> {
			caves.largeOreVeins = value;
		});
		this.legacyCarverDistribution = PresetWidgets.createToggle(caves.legacyCarverDistribution, RTFTranslationKeys.GUI_BUTTON_LEGACY_CARVER_DISTRIBUTION, (button, value) -> {
			caves.legacyCarverDistribution = value;
		});

		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_NOISE_CAVES));
		this.left.addWidget(this.surfaceDensityThreshold);
		this.left.addWidget(this.entranceCaveChance);
		this.left.addWidget(this.cheeseCaveChance);
		this.left.addWidget(this.spaghettiChance);
		this.left.addWidget(this.noodleCaveChance);

		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_CARVERS));
		this.left.addWidget(this.carverCaveChance);
		this.left.addWidget(this.deepCarverCaveChance);
		this.left.addWidget(this.ravineChance);
		this.left.addWidget(this.largeOreVeins);
		this.left.addWidget(this.legacyCarverDistribution);
	}
	
	@Override
	public Optional<Page> previous() {
		return Optional.of(new SurfaceSettingsPage(this.screen, this.preset));
	}

	@Override
	public Optional<Page> next() {
		return Optional.of(new ClimateSettingsPage(this.screen, this.preset));
	}
}
