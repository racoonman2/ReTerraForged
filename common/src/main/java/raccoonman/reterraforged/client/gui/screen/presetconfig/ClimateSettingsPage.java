package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.ValueButton;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.worldgen.data.preset.ClimateSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

class ClimateSettingsPage extends PresetEditorPage {
	private ValueButton<Integer> temperatureSeedOffset;
	private Slider temperatureScale;
	private Slider temperatureFalloff;
	private Slider temperatureMin;
	private Slider temperatureMax;
	private Slider temperatureBias;
	
	private ValueButton<Integer>  moistureSeedOffset;
	private Slider moistureScale;
	private Slider moistureFalloff;
	private Slider moistureMin;
	private Slider moistureMax;
	private Slider moistureBias;
	
	private Slider biomeSize;
	private Slider macroNoiseSize;
	private Slider biomeWarpScale;
	private Slider biomeWarpStrength;
	
	private CycleButton<Source> biomeEdgeType;
	private Slider biomeEdgeScale;
	private Slider biomeEdgeOcaves;
	private Slider biomeEdgeGain;
	private Slider biomeEdgeLacunarity;
	private Slider biomeEdgeStrength;

	public ClimateSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}

	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_CLIMATE_SETTINGS_TITLE);
	}
	
	@Override
	public void init() {
		super.init();
		
		Preset preset = this.preset.getPreset();
		ClimateSettings climate = preset.climate();
		ClimateSettings.RangeValue temperature = climate.temperature;
		
		this.temperatureSeedOffset = PresetWidgets.createRandomButton(RTFTranslationKeys.GUI_BUTTON_CLIMATE_SEED_OFFSET, temperature.seedOffset, (value) -> {
			temperature.seedOffset = value;
			this.regenerate();
		});
		this.temperatureScale = PresetWidgets.createIntSlider(temperature.scale, 1, 20, RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_SCALE, (slider, value) -> {
			temperature.scale = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.temperatureFalloff = PresetWidgets.createIntSlider(temperature.falloff, 1, 10, RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_FALLOFF, (slider, value) -> {
			temperature.falloff = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.temperatureMin = PresetWidgets.createFloatSlider(temperature.min, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MIN, (slider, value) -> {
			temperature.min = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.temperatureMax = PresetWidgets.createFloatSlider(temperature.max, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MAX, (slider, value) -> {
			temperature.max = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.temperatureBias = PresetWidgets.createFloatSlider(temperature.bias, -1.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_BIAS, (slider, value) -> {
			temperature.bias = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});

		ClimateSettings.RangeValue moisture = climate.moisture;
		this.moistureSeedOffset = PresetWidgets.createRandomButton(RTFTranslationKeys.GUI_BUTTON_CLIMATE_SEED_OFFSET, moisture.seedOffset, (value) -> {
			moisture.seedOffset = value;
			this.regenerate();
		});
		this.moistureScale = PresetWidgets.createIntSlider(moisture.scale, 1, 20, RTFTranslationKeys.GUI_SLIDER_MOISTURE_SCALE, (slider, value) -> {
			moisture.scale = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.moistureFalloff = PresetWidgets.createIntSlider(moisture.falloff, 1, 10, RTFTranslationKeys.GUI_SLIDER_MOISTURE_FALLOFF, (slider, value) -> {
			moisture.falloff = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.moistureMin = PresetWidgets.createFloatSlider(moisture.min, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_MOISTURE_MIN, (slider, value) -> {
			moisture.min = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.moistureMax = PresetWidgets.createFloatSlider(moisture.max, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_MOISTURE_MAX, (slider, value) -> {
			moisture.max = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.moistureBias = PresetWidgets.createFloatSlider(moisture.bias, -1.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_MOISTURE_BIAS, (slider, value) -> {
			moisture.bias = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});

		ClimateSettings.BiomeShape biomeShape = climate.biomeShape;
		this.biomeSize = PresetWidgets.createIntSlider(biomeShape.biomeSize, 50, 2000, RTFTranslationKeys.GUI_SLIDER_BIOME_SIZE, (slider, value) -> {
			biomeShape.biomeSize = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.macroNoiseSize = PresetWidgets.createIntSlider(biomeShape.macroNoiseSize, 1, 20, RTFTranslationKeys.GUI_SLIDER_MACRO_NOISE_SIZE, (slider, value) -> {
			biomeShape.macroNoiseSize = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.biomeWarpScale = PresetWidgets.createIntSlider(biomeShape.biomeWarpScale, 1, 500, RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_SCALE, (slider, value) -> {
			biomeShape.biomeWarpScale = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.biomeWarpStrength = PresetWidgets.createIntSlider(biomeShape.biomeWarpStrength, 1, 500, RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_STRENGTH, (slider, value) -> {
			biomeShape.biomeWarpStrength = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		
		ClimateSettings.BiomeNoise biomeEdgeShape = climate.biomeEdgeShape;
		this.biomeEdgeType = PresetWidgets.createCycle(Source.values(), biomeEdgeShape.type, RTFTranslationKeys.GUI_BUTTON_BIOME_EDGE_TYPE, (button, value) -> {
			biomeEdgeShape.type = value;
			this.regenerate();
		});
		this.biomeEdgeScale = PresetWidgets.createIntSlider(biomeEdgeShape.scale, 1, 500, RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_SCALE, (slider, value) -> {
			biomeEdgeShape.scale = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.biomeEdgeOcaves = PresetWidgets.createIntSlider(biomeEdgeShape.octaves, 1, 5, RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_OCTAVES, (slider, value) -> {
			biomeEdgeShape.octaves = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.biomeEdgeGain = PresetWidgets.createFloatSlider(biomeEdgeShape.gain, 0.0F, 5.5F, RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_GAIN, (slider, value) -> {
			biomeEdgeShape.gain = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.biomeEdgeLacunarity = PresetWidgets.createFloatSlider(biomeEdgeShape.lacunarity, 0.0F, 10.5F, RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_LACUNARITY, (slider, value) -> {
			biomeEdgeShape.lacunarity = (float) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		this.biomeEdgeStrength = PresetWidgets.createIntSlider(biomeEdgeShape.strength, 1, 500, RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_STRENGTH, (slider, value) -> {
			biomeEdgeShape.strength = (int) slider.scaleValue(value);
			this.regenerate();
			return value;
		});
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_TEMPERATURE));
		this.left.addWidget(this.temperatureSeedOffset);
		this.left.addWidget(this.temperatureScale);
		this.left.addWidget(this.temperatureFalloff);
		this.left.addWidget(this.temperatureMin);
		this.left.addWidget(this.temperatureMax);
		this.left.addWidget(this.temperatureBias);

		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_MOISTURE));
		this.left.addWidget(this.moistureSeedOffset);
		this.left.addWidget(this.moistureScale);
		this.left.addWidget(this.moistureFalloff);
		this.left.addWidget(this.moistureMin);
		this.left.addWidget(this.moistureMax);
		this.left.addWidget(this.moistureBias);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_BIOME_SHAPE));
		this.left.addWidget(this.biomeSize);
		this.left.addWidget(this.macroNoiseSize);
		this.left.addWidget(this.biomeWarpScale);
		this.left.addWidget(this.biomeWarpStrength);

		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_BIOME_EDGE_SHAPE));
		this.left.addWidget(this.biomeEdgeType);
		this.left.addWidget(this.biomeEdgeScale);
		this.left.addWidget(this.biomeEdgeOcaves);
		this.left.addWidget(this.biomeEdgeGain);
		this.left.addWidget(this.biomeEdgeLacunarity);
		this.left.addWidget(this.biomeEdgeStrength);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.of(new WorldSettingsPage(this.screen, this.preset));
	}

	@Override
	public Optional<Page> next() {
		return Optional.of(new TerrainSettingsPage(this.screen, this.preset));
	}
}
