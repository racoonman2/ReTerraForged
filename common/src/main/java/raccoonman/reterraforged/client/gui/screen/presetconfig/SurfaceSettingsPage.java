package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.PresetListPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldPreset;
import raccoonman.reterraforged.data.worldgen.preset.settings.SurfaceSettings;

public class SurfaceSettingsPage extends PresetEditorPage {
	private Slider rockVariance;
	private Slider rockMin;
	private Slider dirtVariance;
	private Slider dirtMin;
	private Slider rockSteepness;
	private Slider dirtSteepness; //TODO ensure is above rockSteepness
	private Slider screeSteepness; //TODO ensure is above dirtSteepness
	private Slider snowSteepness;
	private Slider heightModifier;
	private Slider slopeModifier;
	
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
		
		WorldPreset preset = this.preset.getPreset();
		SurfaceSettings surface = preset.surface();
		SurfaceSettings.Erosion erosion = surface.erosion();
		
		this.rockVariance = PresetWidgets.createIntSlider(erosion.rockVariance, 0, 255, RTFTranslationKeys.GUI_SLIDER_ROCK_VARIANCE, (slider, value) -> {
			erosion.rockVariance = (int) slider.scaleValue(value);
			return value;
		});
		this.rockMin = PresetWidgets.createIntSlider(erosion.rockMin, -1024, 1024, RTFTranslationKeys.GUI_SLIDER_ROCK_MIN, (slider, value) -> {
			erosion.rockMin = (int) slider.scaleValue(value);
			return value;
		});
		this.dirtVariance = PresetWidgets.createIntSlider(erosion.dirtVariance, 0, 255, RTFTranslationKeys.GUI_SLIDER_DIRT_VARIANCE, (slider, value) -> {
			erosion.dirtVariance = (int) slider.scaleValue(value);
			return value;
		});
		this.dirtMin = PresetWidgets.createIntSlider(erosion.dirtMin, -1024, 1024, RTFTranslationKeys.GUI_SLIDER_DIRT_MIN, (slider, value) -> {
			erosion.dirtMin = (int) slider.scaleValue(value);
			return value;
		});
		this.rockSteepness = PresetWidgets.createFloatSlider(erosion.rockSteepness, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_ROCK_STEEPNESS, (slider, value) -> {
			value = Math.max(value, this.dirtSteepness.getValue());
			erosion.rockSteepness = (float) slider.scaleValue(value);
			return value;
		});
		this.dirtSteepness = PresetWidgets.createFloatSlider(erosion.dirtSteepness, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_DIRT_STEEPNESS, (slider, value) -> {
			value = Mth.clamp(value, this.screeSteepness.getValue(), this.rockSteepness.getValue());
			erosion.dirtSteepness = (float) slider.scaleValue(value);
			return value;
		});
		this.screeSteepness = PresetWidgets.createFloatSlider(erosion.screeSteepness, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_SCREE_STEEPNESS, (slider, value) -> {
			value = Math.min(value, this.dirtSteepness.getValue());
			erosion.screeSteepness = (float) slider.scaleValue(value);
			return value;
		});
		this.snowSteepness = PresetWidgets.createFloatSlider(erosion.snowSteepness, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_SNOW_STEEPNESS, (slider, value) -> {
			erosion.snowSteepness = (float) slider.scaleValue(value);
			return value;
		});
		this.heightModifier = PresetWidgets.createFloatSlider(erosion.heightModifier, 0.0F, 255.0F, RTFTranslationKeys.GUI_SLIDER_HEIGHT_MODIFIER, (slider, value) -> {
			erosion.heightModifier = (float) slider.scaleValue(value);
			return value;
		});
		this.slopeModifier = PresetWidgets.createFloatSlider(erosion.slopeModifier, 0.0F, 255.0F, RTFTranslationKeys.GUI_SLIDER_SLOPE_MODIFIER, (slider, value) -> {
			erosion.slopeModifier = (float) slider.scaleValue(value);
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
		this.left.addWidget(this.snowSteepness);
		this.left.addWidget(this.heightModifier);
		this.left.addWidget(this.slopeModifier);
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
