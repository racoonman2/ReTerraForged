package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public class MiscellaneousPage extends PresetEditorPage {
	private CycleButton<Boolean> smoothLayerDecorator;
	private Slider strataRegionSize;
	private CycleButton<Boolean> strataDecorator;
	private CycleButton<Boolean> oreCompatibleStoneOnly;
	private CycleButton<Boolean> erosionDecorator;
	private CycleButton<Boolean> plainStoneErosion;
	private CycleButton<Boolean> naturalSnowDecorator;
	private CycleButton<Boolean> customBiomeFeatures;
	private CycleButton<Boolean> vanillaSprings;
	private CycleButton<Boolean> vanillaLavaLakes;
	private CycleButton<Boolean> vanillaLavaSprings;
	
	public MiscellaneousPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}

	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_MISCELLANEOUS_SETTINGS_TITLE);
	}

	@Override
	public void init() {
		super.init();
		
		Preset preset = this.preset.getPreset();
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		
		this.smoothLayerDecorator = PresetWidgets.createToggle(miscellaneous.smoothLayerDecorator, RTFTranslationKeys.GUI_BUTTON_SMOOTH_LAYER_DECORATOR, (button, value) -> {
			miscellaneous.smoothLayerDecorator = value;
		});
		this.strataRegionSize = PresetWidgets.createIntSlider(miscellaneous.strataRegionSize, 50, 1000, RTFTranslationKeys.GUI_SLIDER_STRATA_REGION_SIZE, (slider, value) -> {
			miscellaneous.strataRegionSize = (int) slider.scaleValue(value);
			return value;
		});
		this.strataDecorator = PresetWidgets.createToggle(miscellaneous.strataDecorator, RTFTranslationKeys.GUI_BUTTON_STRATA_DECORATOR, (button, value) -> {
			miscellaneous.strataDecorator = value;
		});
		this.oreCompatibleStoneOnly = PresetWidgets.createToggle(miscellaneous.oreCompatibleStoneOnly, RTFTranslationKeys.GUI_BUTTON_ORE_COMPATIBLE_STONE_ONLY, (button, value) -> {
			miscellaneous.oreCompatibleStoneOnly = value;
		});
		this.erosionDecorator = PresetWidgets.createToggle(miscellaneous.erosionDecorator, RTFTranslationKeys.GUI_BUTTON_EROSION_DECORATOR, (button, value) -> {
			miscellaneous.erosionDecorator = value;
		});
		this.plainStoneErosion = PresetWidgets.createToggle(miscellaneous.plainStoneErosion, RTFTranslationKeys.GUI_BUTTON_PLAIN_STONE_EROSION, (button, value) -> {
			miscellaneous.plainStoneErosion = value;
		});
		this.naturalSnowDecorator = PresetWidgets.createToggle(miscellaneous.naturalSnowDecorator, RTFTranslationKeys.GUI_BUTTON_NATURAL_SNOW_DECORATOR, (button, value) -> {
			miscellaneous.naturalSnowDecorator = value;
		});
		this.customBiomeFeatures = PresetWidgets.createToggle(miscellaneous.customBiomeFeatures, RTFTranslationKeys.GUI_BUTTON_CUSTOM_BIOME_FEATURES, (button, value) -> {
			miscellaneous.customBiomeFeatures = value;
		});
		this.vanillaSprings = PresetWidgets.createToggle(miscellaneous.vanillaSprings, RTFTranslationKeys.GUI_BUTTON_VANILLA_SPRINGS, (button, value) -> {
			miscellaneous.vanillaSprings = value;
		});
		this.vanillaLavaLakes = PresetWidgets.createToggle(miscellaneous.vanillaLavaLakes, RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_LAKES, (button, value) -> {
			miscellaneous.vanillaLavaLakes = value;
		});
		this.vanillaLavaSprings = PresetWidgets.createToggle(miscellaneous.vanillaLavaSprings, RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_SPRINGS, (button, value) -> {
			miscellaneous.vanillaLavaSprings = value;
		});
		
		this.left.addWidget(this.smoothLayerDecorator);
		this.left.addWidget(this.strataRegionSize);
		this.left.addWidget(this.strataDecorator);
		this.left.addWidget(this.oreCompatibleStoneOnly);
		this.left.addWidget(this.erosionDecorator);
		this.left.addWidget(this.plainStoneErosion);
		this.left.addWidget(this.naturalSnowDecorator);
		this.left.addWidget(this.customBiomeFeatures);
		this.left.addWidget(this.vanillaSprings);
		this.left.addWidget(this.vanillaLavaLakes);
		this.left.addWidget(this.vanillaLavaSprings);
	}
	
	@Override
	public Optional<Page> previous() {
		return Optional.of(new StructureSettingsPage(this.screen, this.preset));
	}

	@Override
	public Optional<Page> next() {
		return Optional.empty();
	}

}
