package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.PresetListPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.data.worldgen.preset.ContinentType;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.data.worldgen.preset.SpawnType;
import raccoonman.reterraforged.data.worldgen.preset.WorldSettings;
import raccoonman.reterraforged.world.worldgen.noise.function.DistanceFunction;

public class WorldSettingsPage extends PresetEditorPage {
	private CycleButton<ContinentType> continentType;
	private CycleButton<DistanceFunction> continentShape;
	private Slider continentScale;
	private Slider continentJitter;
	private Slider continentSkipping;
	private Slider continentSizeVariance;
	private Slider continentNoiseOctaves;
	private Slider continentNoiseGain;
	private Slider continentNoiseLacunarity;
	
	private Slider deepOcean;
	private Slider shallowOcean;
	private Slider beach;
	private Slider coast;
	private Slider inland;
	
	private CycleButton<SpawnType> spawnType;
	private Slider worldHeight;
	private Slider worldDepth;
	private Slider seaLevel;
	
	public WorldSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}
	
	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_WORLD_SETTINGS_TITLE);
	}

	@Override
	public void init() {
		super.init();
		
		Preset preset = this.preset.getPreset();
		WorldSettings world = preset.world();
		WorldSettings.Continent continent = world.continent;
		WorldSettings.ControlPoints controlPoints = world.controlPoints;
		WorldSettings.Properties properties = world.properties;
		
		this.continentType = PresetWidgets.createCycle(
			ImmutableList.of(
				ContinentType.MULTI,
				ContinentType.SINGLE,
				ContinentType.MULTI_IMPROVED
			),
			continent.continentType, RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE, 
			(button, value) -> {
				continent.continentType = value;
				this.applyContinentType(value);
				this.regenerate();
			}, 
			ContinentType::name
		);
		this.continentShape = PresetWidgets.createCycle(DistanceFunction.values(), continent.continentShape, RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE, (button, value) -> {
			continent.continentShape = value;
			this.regenerate();
		});
		this.continentScale = PresetWidgets.createIntSlider(continent.continentScale, 100, 10000, RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE, (slider, value) -> {
			continent.continentScale = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.continentJitter = PresetWidgets.createFloatSlider(continent.continentJitter, 0.5F, 1.0F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER, (slider, value) -> {
			continent.continentJitter = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.continentSkipping = PresetWidgets.createFloatSlider(continent.continentSkipping, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING, (slider, value) -> {
			continent.continentSkipping = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.continentSizeVariance = PresetWidgets.createFloatSlider(continent.continentSizeVariance, 0.0F, 0.75F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE, (slider, value) -> {
			continent.continentSizeVariance = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.continentNoiseOctaves = PresetWidgets.createIntSlider(continent.continentNoiseOctaves, 1, 5, RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES, (slider, value) -> {
			continent.continentNoiseOctaves = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.continentNoiseGain = PresetWidgets.createFloatSlider(continent.continentNoiseGain, 0.0F, 0.5F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN, (slider, value) -> {
			continent.continentNoiseGain = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.continentNoiseLacunarity = PresetWidgets.createFloatSlider(continent.continentNoiseLacunarity, 1.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY, (slider, value) -> {
			continent.continentNoiseLacunarity = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		
		this.applyContinentType(this.continentType.getValue());

		this.deepOcean = PresetWidgets.createFloatSlider(controlPoints.deepOcean, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN, (slider, value) -> {
			value = Math.min(value, this.shallowOcean.getValue());
			controlPoints.deepOcean = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.shallowOcean = PresetWidgets.createFloatSlider(controlPoints.shallowOcean, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN, (slider, value) -> {
			value = Mth.clamp(value, this.deepOcean.getValue(), this.beach.getValue());
			controlPoints.shallowOcean = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.beach = PresetWidgets.createFloatSlider(controlPoints.beach, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_BEACH, (slider, value) -> {
			value = Mth.clamp(value, this.shallowOcean.getValue(), this.coast.getValue());
			controlPoints.beach = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.coast = PresetWidgets.createFloatSlider(controlPoints.coast, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_COAST, (slider, value) -> {
			value = Mth.clamp(value, this.beach.getValue(), this.inland.getValue());
			controlPoints.coast = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.inland = PresetWidgets.createFloatSlider(controlPoints.inland, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_INLAND, (slider, value) -> {
			value = Math.max(value, this.coast.getValue());
			controlPoints.inland = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.spawnType = PresetWidgets.createCycle(SpawnType.values(), properties.spawnType, RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE, (button, value) -> {
			properties.spawnType = value;
		});
		this.worldHeight = PresetWidgets.createIntSlider(properties.worldHeight, 0, 1024, RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT, (slider, value) -> {
			int nearestMultiple = Math.max(getNearestMultiple(slider, (float) value, 16), 16);
			properties.worldHeight = nearestMultiple;
			return slider.getSliderValue(nearestMultiple);
		});
		this.worldDepth = PresetWidgets.createIntSlider(properties.worldDepth, 0, 256, RTFTranslationKeys.GUI_SLIDER_WORLD_DEPTH, (slider, value) -> {
			int nearestMultiple = getNearestMultiple(slider, (float) value, 16);
			properties.worldDepth = nearestMultiple;
			return slider.getSliderValue(nearestMultiple);
		});
		this.seaLevel = PresetWidgets.createIntSlider(properties.seaLevel, 0, 255, RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL, (slider, value) -> {
			properties.seaLevel = (int) slider.scaleValue(value);
			return value;
		});
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_CONTINENT));
		this.left.addWidget(this.continentType);
		this.left.addWidget(this.continentShape);
		this.left.addWidget(this.continentScale);
		this.left.addWidget(this.continentJitter);
		this.left.addWidget(this.continentSkipping);
		this.left.addWidget(this.continentSizeVariance);
		this.left.addWidget(this.continentNoiseOctaves);
		this.left.addWidget(this.continentNoiseGain);
		this.left.addWidget(this.continentNoiseLacunarity);

		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_CONTROL_POINTS));
		this.left.addWidget(this.deepOcean);
		this.left.addWidget(this.shallowOcean);
		this.left.addWidget(this.beach);
		this.left.addWidget(this.coast);
		this.left.addWidget(this.inland);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_PROPERTIES));
		this.left.addWidget(this.spawnType);
		this.left.addWidget(this.worldHeight);
		this.left.addWidget(this.worldDepth);
		this.left.addWidget(this.seaLevel);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.of(new PresetListPage(this.screen));
	}

	@Override
	public Optional<Page> next() {
		return Optional.of(new CaveSettingsPage(this.screen, this.preset));
	}
	
	private void applyContinentType(ContinentType type) {
		this.continentShape.active = type == ContinentType.MULTI || type == ContinentType.SINGLE;

		boolean isMultiImproved = type == ContinentType.MULTI_IMPROVED;
		this.continentSkipping.active = isMultiImproved;
		this.continentSizeVariance.active = isMultiImproved;
		this.continentNoiseOctaves.active = isMultiImproved;
		this.continentNoiseGain.active = isMultiImproved;
		this.continentNoiseLacunarity.active = isMultiImproved;
	}
	
	private static int getNearestMultiple(Slider slider, float value, int multiple)  {
		int lerpedValue = (int) slider.lerpValue(value);
		int lerpedMultiple = (int) slider.lerpValue((float) slider.getSliderValue(multiple));
		return lerpedValue / lerpedMultiple * lerpedMultiple;
	}
}
