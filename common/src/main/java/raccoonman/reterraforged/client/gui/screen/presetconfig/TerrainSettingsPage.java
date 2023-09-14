package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.presetconfig.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.ValueButton;
import raccoonman.reterraforged.common.data.preset.Preset;
import raccoonman.reterraforged.common.data.preset.settings.TerrainSettings;
import raccoonman.reterraforged.common.data.preset.settings.TerrainSettings.General;
import raccoonman.reterraforged.common.data.preset.settings.TerrainSettings.Terrain;

public class TerrainSettingsPage extends PresetEditorPage {
	private ValueButton<Integer> terrainSeedOffset;
	private Slider terrainRegionSize;
	private Slider globalVerticalScale;
	private Slider globalHorizontalScale;
	private CycleButton<Boolean> fancyMountains;
	
	private Slider steppeWeight;
	private Slider steppeBaseScale;
	private Slider steppeVerticalScale;
	private Slider steppeHorizontalScale;
	
	private Slider plainsWeight;
	private Slider plainsBaseScale;
	private Slider plainsVerticalScale;
	private Slider plainsHorizontalScale;
	
	private Slider hillsWeight;
	private Slider hillsBaseScale;
	private Slider hillsVerticalScale;
	private Slider hillsHorizontalScale;
	
	private Slider dalesWeight;
	private Slider dalesBaseScale;
	private Slider dalesVerticalScale;
	private Slider dalesHorizontalScale;
	
	private Slider plateauWeight;
	private Slider plateauBaseScale;
	private Slider plateauVerticalScale;
	private Slider plateauHorizontalScale;
	
	private Slider badlandsWeight;
	private Slider badlandsBaseScale;
	private Slider badlandsVerticalScale;
	private Slider badlandsHorizontalScale;
	
	private Slider torridonianWeight;
	private Slider torridonianBaseScale;
	private Slider torridonianVerticalScale;
	private Slider torridonianHorizontalScale;
	
	private Slider mountainsWeight;
	private Slider mountainsBaseScale;
	private Slider mountainsVerticalScale;
	private Slider mountainsHorizontalScale;
	
	private Slider volcanoWeight;
	private Slider volcanoBaseScale;
	private Slider volcanoVerticalScale;
	private Slider volcanoHorizontalScale;
	
	public TerrainSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}

	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_TERRAIN_SETTINGS_TITLE);
	}
	
	@Override
	public void init() {
		super.init();

		Preset preset = this.preset.getPreset();
		TerrainSettings terrain = preset.terrain();
		General general = terrain.general;
		
		this.terrainSeedOffset = PresetWidgets.createRandomButton(RTFTranslationKeys.GUI_BUTTON_TERRAIN_SEED_OFFSET, general.terrainSeedOffset, (value) -> {
			general.terrainSeedOffset = value;
		});
		this.terrainRegionSize = PresetWidgets.createIntSlider(general.terrainRegionSize, 125, 5000, RTFTranslationKeys.GUI_SLIDER_TERRAIN_REGION_SIZE, (value) -> {
			general.terrainRegionSize = (int) this.terrainRegionSize.scaleValue((float) value);
			return value;
		});
		this.globalVerticalScale = PresetWidgets.createFloatSlider(general.globalVerticalScale, 0.01F, 1.0F, RTFTranslationKeys.GUI_SLIDER_GLOBAL_VERTICAL_SCALE, (value) -> {
			general.globalVerticalScale = (int) this.globalVerticalScale.scaleValue((float) value);
			return value;
		});
		this.globalHorizontalScale = PresetWidgets.createFloatSlider(general.globalHorizontalScale, 0.01F, 5.0F, RTFTranslationKeys.GUI_SLIDER_GLOBAL_HORIZONTAL_SCALE, (value) -> {
			general.globalHorizontalScale = (int) this.globalHorizontalScale.scaleValue((float) value);
			return value;
		});
		this.fancyMountains = PresetWidgets.createToggle(general.fancyMountains, RTFTranslationKeys.GUI_BUTTON_FANCY_MOUNTAINS, (button, value) -> {
			general.fancyMountains = value;
		});
		
		Terrain steppe = terrain.steppe;
		this.steppeWeight = PresetWidgets.createFloatSlider(steppe.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			steppe.weight = (float) this.steppeWeight.scaleValue((float) value);
			return value;
		});
		this.steppeBaseScale = PresetWidgets.createFloatSlider(steppe.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			steppe.baseScale = (float) this.steppeBaseScale.scaleValue((float) value);
			return value;
		});
		this.steppeVerticalScale = PresetWidgets.createFloatSlider(steppe.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			steppe.verticalScale = (float) this.steppeVerticalScale.scaleValue((float) value);
			return value;
		});
		this.steppeHorizontalScale = PresetWidgets.createFloatSlider(steppe.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			steppe.horizontalScale = (float) this.steppeHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		Terrain plains = terrain.plains;
		this.plainsWeight = PresetWidgets.createFloatSlider(plains.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			plains.weight = (float) this.plainsWeight.scaleValue((float) value);
			return value;
		});
		this.plainsBaseScale = PresetWidgets.createFloatSlider(plains.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			plains.baseScale = (float) this.plainsBaseScale.scaleValue((float) value);
			return value;
		});
		this.plainsVerticalScale = PresetWidgets.createFloatSlider(plains.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			plains.verticalScale = (float) this.plainsVerticalScale.scaleValue((float) value);
			return value;
		});
		this.plainsHorizontalScale = PresetWidgets.createFloatSlider(plains.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			plains.horizontalScale = (float) this.plainsHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		Terrain hills = terrain.hills;
		this.hillsWeight = PresetWidgets.createFloatSlider(hills.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			hills.weight = (float) this.hillsWeight.scaleValue((float) value);
			return value;
		});
		this.hillsBaseScale = PresetWidgets.createFloatSlider(hills.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			hills.baseScale = (float) this.hillsBaseScale.scaleValue((float) value);
			return value;
		});
		this.hillsVerticalScale = PresetWidgets.createFloatSlider(hills.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			hills.verticalScale = (float) this.hillsVerticalScale.scaleValue((float) value);
			return value;
		});
		this.hillsHorizontalScale = PresetWidgets.createFloatSlider(hills.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			hills.horizontalScale = (float) this.hillsHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		Terrain dales = terrain.dales;
		this.dalesWeight = PresetWidgets.createFloatSlider(dales.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			dales.weight = (float) this.dalesWeight.scaleValue((float) value);
			return value;
		});
		this.dalesBaseScale = PresetWidgets.createFloatSlider(dales.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			dales.baseScale = (float) this.dalesBaseScale.scaleValue((float) value);
			return value;
		});
		this.dalesVerticalScale = PresetWidgets.createFloatSlider(dales.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			dales.verticalScale = (float) this.dalesVerticalScale.scaleValue((float) value);
			return value;
		});
		this.dalesHorizontalScale = PresetWidgets.createFloatSlider(dales.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			dales.horizontalScale = (float) this.dalesHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		Terrain plateau = terrain.plateau;
		this.plateauWeight = PresetWidgets.createFloatSlider(plateau.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			plateau.weight = (float) this.plateauWeight.scaleValue((float) value);
			return value;
		});
		this.plateauBaseScale = PresetWidgets.createFloatSlider(plateau.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			plateau.baseScale = (float) this.plateauBaseScale.scaleValue((float) value);
			return value;
		});
		this.plateauVerticalScale = PresetWidgets.createFloatSlider(plateau.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			plateau.verticalScale = (float) this.plateauVerticalScale.scaleValue((float) value);
			return value;
		});
		this.plateauHorizontalScale = PresetWidgets.createFloatSlider(plateau.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			plateau.horizontalScale = (float) this.plateauHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		Terrain badlands = terrain.badlands;
		this.badlandsWeight = PresetWidgets.createFloatSlider(badlands.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			badlands.weight = (float) this.badlandsWeight.scaleValue((float) value);
			return value;
		});
		this.badlandsBaseScale = PresetWidgets.createFloatSlider(badlands.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			badlands.baseScale = (float) this.badlandsBaseScale.scaleValue((float) value);
			return value;
		});
		this.badlandsVerticalScale = PresetWidgets.createFloatSlider(badlands.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			badlands.verticalScale = (float) this.badlandsVerticalScale.scaleValue((float) value);
			return value;
		});
		this.badlandsHorizontalScale = PresetWidgets.createFloatSlider(badlands.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			badlands.horizontalScale = (float) this.badlandsHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		Terrain torridonian = terrain.torridonian;
		this.torridonianWeight = PresetWidgets.createFloatSlider(torridonian.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			torridonian.weight = (float) this.torridonianWeight.scaleValue((float) value);
			return value;
		});
		this.torridonianBaseScale = PresetWidgets.createFloatSlider(torridonian.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			torridonian.baseScale = (float) this.torridonianBaseScale.scaleValue((float) value);
			return value;
		});
		this.torridonianVerticalScale = PresetWidgets.createFloatSlider(torridonian.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			torridonian.verticalScale = (float) this.torridonianVerticalScale.scaleValue((float) value);
			return value;
		});
		this.torridonianHorizontalScale = PresetWidgets.createFloatSlider(torridonian.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			torridonian.horizontalScale = (float) this.torridonianHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		Terrain mountains = terrain.mountains;
		this.mountainsWeight = PresetWidgets.createFloatSlider(mountains.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			mountains.weight = (float) this.mountainsWeight.scaleValue((float) value);
			return value;
		});
		this.mountainsBaseScale = PresetWidgets.createFloatSlider(mountains.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			mountains.baseScale = (float) this.mountainsBaseScale.scaleValue((float) value);
			return value;
		});
		this.mountainsVerticalScale = PresetWidgets.createFloatSlider(mountains.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			mountains.verticalScale = (float) this.mountainsVerticalScale.scaleValue((float) value);
			return value;
		});
		this.mountainsHorizontalScale = PresetWidgets.createFloatSlider(mountains.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			mountains.horizontalScale = (float) this.mountainsHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		Terrain volcano = terrain.volcano;
		this.volcanoWeight = PresetWidgets.createFloatSlider(volcano.weight, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, (value) -> {
			volcano.weight = (float) this.volcanoWeight.scaleValue((float) value);
			return value;
		});
		this.volcanoBaseScale = PresetWidgets.createFloatSlider(volcano.baseScale, 0.0F, 2.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, (value) -> {
			volcano.baseScale = (float) this.volcanoBaseScale.scaleValue((float) value);
			return value;
		});
		this.volcanoVerticalScale = PresetWidgets.createFloatSlider(volcano.verticalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, (value) -> {
			volcano.verticalScale = (float) this.volcanoVerticalScale.scaleValue((float) value);
			return value;
		});
		this.volcanoHorizontalScale = PresetWidgets.createFloatSlider(volcano.horizontalScale, 0.0F, 10.0F, RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, (value) -> {
			volcano.horizontalScale = (float) this.volcanoHorizontalScale.scaleValue((float) value);
			return value;
		});
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_GENERAL));
		this.left.addWidget(this.terrainSeedOffset);
		this.left.addWidget(this.terrainRegionSize);
		this.left.addWidget(this.globalVerticalScale);
		this.left.addWidget(this.globalHorizontalScale);
		this.left.addWidget(this.fancyMountains);

		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_STEPPE));
		this.left.addWidget(this.steppeWeight);
		this.left.addWidget(this.steppeBaseScale);
		this.left.addWidget(this.steppeVerticalScale);
		this.left.addWidget(this.steppeHorizontalScale);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_PLAINS));
		this.left.addWidget(this.plainsWeight);
		this.left.addWidget(this.plainsBaseScale);
		this.left.addWidget(this.plainsVerticalScale);
		this.left.addWidget(this.plainsHorizontalScale);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_HILLS));
		this.left.addWidget(this.hillsWeight);
		this.left.addWidget(this.hillsBaseScale);
		this.left.addWidget(this.hillsVerticalScale);
		this.left.addWidget(this.hillsHorizontalScale);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_DALES));
		this.left.addWidget(this.dalesWeight);
		this.left.addWidget(this.dalesBaseScale);
		this.left.addWidget(this.dalesVerticalScale);
		this.left.addWidget(this.dalesHorizontalScale);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_PLATEAU));
		this.left.addWidget(this.plateauWeight);
		this.left.addWidget(this.plateauBaseScale);
		this.left.addWidget(this.plateauVerticalScale);
		this.left.addWidget(this.plateauHorizontalScale);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_BADLANDS));
		this.left.addWidget(this.badlandsWeight);
		this.left.addWidget(this.badlandsBaseScale);
		this.left.addWidget(this.badlandsVerticalScale);
		this.left.addWidget(this.badlandsHorizontalScale);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_TORRIDONIAN));
		this.left.addWidget(this.torridonianWeight);
		this.left.addWidget(this.torridonianBaseScale);
		this.left.addWidget(this.torridonianVerticalScale);
		this.left.addWidget(this.torridonianHorizontalScale);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_MOUNTAINS));
		this.left.addWidget(this.mountainsWeight);
		this.left.addWidget(this.mountainsBaseScale);
		this.left.addWidget(this.mountainsVerticalScale);
		this.left.addWidget(this.mountainsHorizontalScale);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_VOLCANO));
		this.left.addWidget(this.volcanoWeight);
		this.left.addWidget(this.volcanoBaseScale);
		this.left.addWidget(this.volcanoVerticalScale);
		this.left.addWidget(this.volcanoHorizontalScale);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.of(new ClimateSettingsPage(this.screen, this.preset));
	}

	@Override
	public Optional<Page> next() {
		return Optional.of(new RiverSettingsPage(this.screen, this.preset));
	}
}
