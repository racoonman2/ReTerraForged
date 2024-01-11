package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.PresetListPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.data.worldgen.preset.settings.SurfaceSettings;

public class SurfaceSettingsPage extends PresetEditorPage {
	private Slider rockVariance;
	private Slider rockMin;
	private Slider dirtVariance;
	private Slider dirtMin;
	private Slider rockSteepness;
	private Slider dirtSteepness; //TODO ensure is above rockSteepness
	private Slider screeSteepness; //TODO ensure is above dirtSteepness
	
	public SurfaceSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}
	
	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_SURFACE_SETTINGS_TITLE);
	}

	@Override
	public void init() {
		super.init();
		
		Preset preset = this.preset.getPreset();
		SurfaceSettings surface = preset.surface();
		SurfaceSettings.Erosion erosion = surface.erosion();
		
		this.rockVariance = PresetWidgets.createIntSlider(erosion.rockVariance, 0, 256, RTFTranslationKeys.GUI_SLIDER_ROCK_VARIANCE, (slider, value) -> {
			erosion.rockVariance = (int) slider.scaleValue(value);
			return value;
		});
		this.rockMin = PresetWidgets.createIntSlider(erosion.rockMin, -1024, 1024, RTFTranslationKeys.GUI_SLIDER_ROCK_MIN, (slider, value) -> {
			erosion.rockMin = (int) slider.scaleValue(value);
			return value;
		});
		this.dirtVariance = PresetWidgets.createIntSlider(erosion.dirtVariance, 0, 256, RTFTranslationKeys.GUI_SLIDER_DIRT_VARIANCE, (slider, value) -> {
			erosion.dirtVariance = (int) slider.scaleValue(value);
			return value;
		});
		this.dirtMin = PresetWidgets.createIntSlider(erosion.dirtMin, -1024, 1024, RTFTranslationKeys.GUI_SLIDER_DIRT_MIN, (slider, value) -> {
			erosion.dirtMin = (int) slider.scaleValue(value);
			return value;
		});
		this.rockSteepness = PresetWidgets.createFloatSlider(erosion.rockSteepness, 0.0F, 3.0F, RTFTranslationKeys.GUI_SLIDER_ROCK_STEEPNESS, (slider, value) -> {
			erosion.rockSteepness = (float) slider.scaleValue(value);
			return value;
		});
		this.dirtSteepness = PresetWidgets.createFloatSlider(erosion.dirtSteepness, 0.0F, 3.0F, RTFTranslationKeys.GUI_SLIDER_DIRT_STEEPNESS, (slider, value) -> {
			erosion.dirtSteepness = (float) slider.scaleValue(value);
			return value;
		});
		this.screeSteepness = PresetWidgets.createFloatSlider(erosion.screeSteepness, 0.0F, 3.0F, RTFTranslationKeys.GUI_SLIDER_SCREE_STEEPNESS, (slider, value) -> {
			erosion.screeSteepness = (float) slider.scaleValue(value);
			return value;
		});
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_SURFACE_EROSION));
		this.left.addWidget(this.rockVariance);
		this.left.addWidget(this.rockMin);
		this.left.addWidget(this.dirtVariance);
		this.left.addWidget(this.dirtMin);
		this.left.addWidget(this.rockSteepness);
		this.left.addWidget(this.dirtSteepness);
		this.left.addWidget(this.screeSteepness);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.of(new WorldSettingsPage(this.screen, this.preset));
	}

	@Override
	public Optional<Page> next() {
		return Optional.of(new CaveSettingsPage(this.screen, this.preset));
	}
}
