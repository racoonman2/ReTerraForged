package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.presetconfig.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.UnsizedWidgets;
import raccoonman.reterraforged.common.data.preset.Preset;
import raccoonman.reterraforged.common.data.preset.settings.ContinentType;
import raccoonman.reterraforged.common.data.preset.settings.SpawnType;
import raccoonman.reterraforged.common.data.preset.settings.WorldSettings;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;

public class WorldSettingsPage extends BisectedPage<PresetConfigScreen, AbstractWidget, AbstractWidget> {
	private PresetEntry preset;
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
	private Slider minY;
	private Slider seaLevel;
	
	public WorldSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen);
		
		this.preset = preset;
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
		
		this.continentType = UnsizedWidgets.createCycle(ContinentType.values(), continent.continentType, RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE, (button, value) -> {
			this.applyContinentValue(value);
		});
		this.continentShape = UnsizedWidgets.createCycle(DistanceFunction.values(), continent.continentShape, RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE, (button, value) -> {
			
		});
		this.continentScale = UnsizedWidgets.createIntSlider(continent.continentScale, 100, 10000, RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE, (value) -> {
			return value;
		});
		this.continentJitter = UnsizedWidgets.createFloatSlider(continent.continentJitter, 0.5F, 1.0F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER, (value) -> {
			return value;
		});
		this.continentSkipping = UnsizedWidgets.createFloatSlider(continent.continentSkipping, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING, (value) -> {
			return value;
		});
		this.continentSizeVariance = UnsizedWidgets.createFloatSlider(continent.continentSizeVariance, 0.0F, 0.75F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE, (value) -> {
			return value;
		});
		this.continentNoiseOctaves = UnsizedWidgets.createIntSlider(continent.continentNoiseOctaves, 1, 5, RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES, (value) -> {
			return value;
		});
		this.continentNoiseGain = UnsizedWidgets.createFloatSlider(continent.continentNoiseGain, 0.0F, 0.5F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN, (value) -> {
			return value;
		});
		this.continentNoiseLacunarity = UnsizedWidgets.createFloatSlider(continent.continentNoiseLacunarity, 1.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY, (value) -> {
			return value;
		});
		
		this.applyContinentValue(this.continentType.getValue());

		WorldSettings.ControlPoints controlPoints = world.controlPoints;
		
		this.deepOcean = UnsizedWidgets.createFloatSlider(controlPoints.deepOcean, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN, (value) -> {
			return Math.min(value, this.shallowOcean.getLerpedValue());
		});
		this.shallowOcean = UnsizedWidgets.createFloatSlider(controlPoints.shallowOcean, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN, (value) -> {
			return Mth.clamp(value, this.deepOcean.getLerpedValue(), this.beach.getLerpedValue());
		});
		this.beach = UnsizedWidgets.createFloatSlider(controlPoints.beach, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_BEACH, (value) -> {
			return Mth.clamp(value, this.shallowOcean.getLerpedValue(), this.coast.getLerpedValue());
		});
		this.coast = UnsizedWidgets.createFloatSlider(controlPoints.coast, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_COAST, (value) -> {
			return Mth.clamp(value, this.beach.getLerpedValue(), this.inland.getLerpedValue());
		});
		this.inland = UnsizedWidgets.createFloatSlider(controlPoints.inland, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_INLAND, (value) -> {
			return Math.max(value, this.coast.getLerpedValue());
		});
		
		WorldSettings.Properties properties = world.properties;
		
		this.spawnType = UnsizedWidgets.createCycle(SpawnType.values(), properties.spawnType, RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE, (button, value) -> {

		});
		this.worldHeight = UnsizedWidgets.createIntSlider(properties.worldHeight, 0, 1024, RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT, (value) -> {
			return value;
		});
		this.minY = UnsizedWidgets.createIntSlider(properties.minY, -64, 0, RTFTranslationKeys.GUI_SLIDER_MIN_Y, (value) -> {
			return value;
		});
		this.seaLevel = UnsizedWidgets.createIntSlider(properties.seaLevel, 0, 255, RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL, (value) -> {
			return value;
		});
		
		this.left.addWidget(UnsizedWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_CONTINENT));
		this.left.addWidget(this.continentType);
		this.left.addWidget(this.continentShape);
		this.left.addWidget(this.continentScale);
		this.left.addWidget(this.continentJitter);
		this.left.addWidget(this.continentSkipping);
		this.left.addWidget(this.continentSizeVariance);
		this.left.addWidget(this.continentNoiseOctaves);
		this.left.addWidget(this.continentNoiseGain);
		this.left.addWidget(this.continentNoiseLacunarity);

		this.left.addWidget(UnsizedWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_CONTROL_POINTS));
		this.left.addWidget(this.deepOcean);
		this.left.addWidget(this.shallowOcean);
		this.left.addWidget(this.beach);
		this.left.addWidget(this.coast);
		this.left.addWidget(this.inland);
		
		this.left.addWidget(UnsizedWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_PROPERTIES));
		this.left.addWidget(this.spawnType);
		this.left.addWidget(this.worldHeight);
		this.left.addWidget(this.minY);
		this.left.addWidget(this.seaLevel);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.of(new SelectPresetPage(this.screen));
	}

	@Override
	public Optional<Page> next() {
		return Optional.empty();
	}
	
	private void applyContinentValue(ContinentType value) {
		this.continentShape.active = value == ContinentType.MULTI || value == ContinentType.SINGLE;

		boolean isMultiImproved = value == ContinentType.MULTI_IMPROVED;
		this.continentSkipping.active = isMultiImproved;
		this.continentSizeVariance.active = isMultiImproved;
		this.continentNoiseOctaves.active = isMultiImproved;
		this.continentNoiseGain.active = isMultiImproved;
		this.continentNoiseLacunarity.active = isMultiImproved;
	}
}
