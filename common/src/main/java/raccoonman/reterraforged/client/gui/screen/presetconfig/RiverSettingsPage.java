package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.presetconfig.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.ValueButton;
import raccoonman.reterraforged.common.data.preset.Preset;
import raccoonman.reterraforged.common.data.preset.settings.RiverSettings;
import raccoonman.reterraforged.common.data.preset.settings.RiverSettings.Lake;
import raccoonman.reterraforged.common.data.preset.settings.RiverSettings.River;
import raccoonman.reterraforged.common.data.preset.settings.RiverSettings.Wetland;

public class RiverSettingsPage extends PresetEditorPage {
	private ValueButton<Integer> seedOffset;
	private Slider riverCount;
	
	private Slider mainRiverBedDepth;
	private Slider mainRiverMinBankHeight;
	private Slider mainRiverMaxBankHeight;
	private Slider mainRiverBedWidth;
	private Slider mainRiverBankWidth;
	private Slider mainRiverFade;

	private Slider branchRiverBedDepth;
	private Slider branchRiverMinBankHeight;
	private Slider branchRiverMaxBankHeight;
	private Slider branchRiverBedWidth;
	private Slider branchRiverBankWidth;
	private Slider branchRiverFade;
	
	private Slider lakeChance;
	private Slider lakeMinStartDistance;
	private Slider lakeMaxStartDistance;
	private Slider lakeDepth;
	private Slider lakeSizeMin;
	private Slider lakeSizeMax;
	private Slider lakeMinBankHeight;
	private Slider lakeMaxBankHeight;
	
	private Slider wetlandChance;
	private Slider wetlandSizeMin;
	private Slider wetlandSizeMax;
	
	public RiverSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}

	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_RIVER_SETTINGS_TITLE);
	}
	
	@Override
	public void init() {
		super.init();
		
		Preset preset = this.preset.getPreset();
		RiverSettings river = preset.rivers();
		
		this.seedOffset = PresetWidgets.createRandomButton(RTFTranslationKeys.GUI_BUTTON_RIVER_SEED_OFFSET, river.seedOffset, (value) -> {
			river.seedOffset = value;
		});
		this.riverCount = PresetWidgets.createIntSlider(river.riverCount, 0, 30, RTFTranslationKeys.GUI_SLIDER_RIVER_COUNT, (value) -> {
			river.riverCount = (int) this.riverCount.scaleValue((float) value);
			return value;
		});
		
		River mainRivers = river.mainRivers;
		this.mainRiverBedDepth = PresetWidgets.createIntSlider(mainRivers.bedDepth, 1, 10, RTFTranslationKeys.GUI_SLIDER_RIVER_BED_DEPTH, (value) -> {
			mainRivers.bedDepth = (int) this.mainRiverBedDepth.scaleValue((float) value);
			return value;
		});
		this.mainRiverMinBankHeight = PresetWidgets.createIntSlider(mainRivers.minBankHeight, 0, 10, RTFTranslationKeys.GUI_SLIDER_RIVER_MIN_BANK_HEIGHT, (value) -> {
			mainRivers.minBankHeight = (int) this.mainRiverMinBankHeight.scaleValue((float) value);
			return value;
		});
		this.mainRiverMaxBankHeight = PresetWidgets.createIntSlider(mainRivers.maxBankHeight, 1, 10, RTFTranslationKeys.GUI_SLIDER_RIVER_MAX_BANK_HEIGHT, (value) -> {
			mainRivers.maxBankHeight = (int) this.mainRiverMaxBankHeight.scaleValue((float) value);
			return value;
		});
		this.mainRiverBedWidth = PresetWidgets.createIntSlider(mainRivers.bedWidth, 1, 20, RTFTranslationKeys.GUI_SLIDER_RIVER_BED_WIDTH, (value) -> {
			mainRivers.bedWidth = (int) this.mainRiverBedWidth.scaleValue((float) value);
			return value;
		});
		this.mainRiverBankWidth = PresetWidgets.createIntSlider(mainRivers.bankWidth, 1, 50, RTFTranslationKeys.GUI_SLIDER_RIVER_BANK_WIDTH, (value) -> {
			mainRivers.bankWidth = (int) this.mainRiverBankWidth.scaleValue((float) value);
			return value;
		});
		this.mainRiverFade = PresetWidgets.createFloatSlider(mainRivers.fade, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_RIVER_FADE, (value) -> {
			mainRivers.fade = this.mainRiverFade.scaleValue((float) value);
			return value;
		});
		
		River branchRivers = river.branchRivers;
		this.branchRiverBedDepth = PresetWidgets.createIntSlider(branchRivers.bedDepth, 1, 10, RTFTranslationKeys.GUI_SLIDER_RIVER_BED_DEPTH, (value) -> {
			branchRivers.bedDepth = (int) this.branchRiverBedDepth.scaleValue((float) value);
			return value;
		});
		this.branchRiverMinBankHeight = PresetWidgets.createIntSlider(branchRivers.minBankHeight, 0, 10, RTFTranslationKeys.GUI_SLIDER_RIVER_MIN_BANK_HEIGHT, (value) -> {
			branchRivers.minBankHeight = (int) this.branchRiverMinBankHeight.scaleValue((float) value);
			return value;
		});
		this.branchRiverMaxBankHeight = PresetWidgets.createIntSlider(branchRivers.maxBankHeight, 1, 10, RTFTranslationKeys.GUI_SLIDER_RIVER_MAX_BANK_HEIGHT, (value) -> {
			branchRivers.maxBankHeight = (int) this.branchRiverMaxBankHeight.scaleValue((float) value);
			return value;
		});
		this.branchRiverBedWidth = PresetWidgets.createIntSlider(branchRivers.bedWidth, 1, 20, RTFTranslationKeys.GUI_SLIDER_RIVER_BED_WIDTH, (value) -> {
			branchRivers.bedWidth = (int) this.branchRiverBedWidth.scaleValue((float) value);
			return value;
		});
		this.branchRiverBankWidth = PresetWidgets.createIntSlider(branchRivers.bankWidth, 1, 50, RTFTranslationKeys.GUI_SLIDER_RIVER_BANK_WIDTH, (value) -> {
			branchRivers.bankWidth = (int) this.branchRiverBankWidth.scaleValue((float) value);
			return value;
		});
		this.branchRiverFade = PresetWidgets.createFloatSlider(branchRivers.fade, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_RIVER_FADE, (value) -> {
			branchRivers.fade = this.branchRiverFade.scaleValue((float) value);
			return value;
		});
		
		Lake lake = river.lakes;
		this.lakeChance = PresetWidgets.createFloatSlider(lake.chance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_LAKE_CHANCE, (value) -> {
			lake.chance = this.lakeChance.scaleValue((float) value);
			return value;
		});
		this.lakeMinStartDistance = PresetWidgets.createFloatSlider(lake.minStartDistance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_START_DISTANCE, (value) -> {
			lake.minStartDistance = this.lakeMinStartDistance.scaleValue((float) value);
			return value;
		});
		this.lakeMaxStartDistance = PresetWidgets.createFloatSlider(lake.maxStartDistance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_START_DISTANCE, (value) -> {
			lake.maxStartDistance = this.lakeMaxStartDistance.scaleValue((float) value);
			return value;
		});
		this.lakeDepth = PresetWidgets.createIntSlider(lake.depth, 1, 20, RTFTranslationKeys.GUI_SLIDER_LAKE_DEPTH, (value) -> {
			lake.depth = (int) this.lakeDepth.scaleValue((float) value);
			return value;
		});
		this.lakeSizeMin = PresetWidgets.createIntSlider(lake.sizeMin, 1, 100, RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MIN, (value) -> {
			lake.sizeMin = (int) this.lakeSizeMin.scaleValue((float) value);
			return value;
		});
		this.lakeSizeMax = PresetWidgets.createIntSlider(lake.sizeMax, 1, 500, RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MAX, (value) -> {
			lake.sizeMax = (int) this.lakeSizeMax.scaleValue((float) value);
			return value;
		});
		this.lakeMinBankHeight = PresetWidgets.createIntSlider(lake.minBankHeight, 1, 10, RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_BANK_HEIGHT, (value) -> {
			lake.minBankHeight = (int) this.lakeMinBankHeight.scaleValue((float) value);
			return value;
		});
		this.lakeMaxBankHeight = PresetWidgets.createIntSlider(lake.maxBankHeight, 1, 10, RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_BANK_HEIGHT, (value) -> {
			lake.maxBankHeight = (int) this.lakeMaxBankHeight.scaleValue((float) value);
			return value;
		});
		
		Wetland wetland = river.wetlands;
		this.wetlandChance = PresetWidgets.createFloatSlider(wetland.chance, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_WETLAND_CHANCE, (value) -> {
			wetland.chance = this.wetlandChance.scaleValue((float) value);
			return value;
		});
		this.wetlandSizeMin = PresetWidgets.createIntSlider(wetland.sizeMin, 50, 500, RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MIN, (value) -> {
			wetland.sizeMin = (int) this.wetlandSizeMin.scaleValue((float) value);
			return value;
		});
		this.wetlandSizeMax = PresetWidgets.createIntSlider(wetland.sizeMax, 50, 500, RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MAX, (value) -> {
			wetland.sizeMax = (int) this.wetlandSizeMax.scaleValue((float) value);
			return value;
		});
		
		this.left.addWidget(this.seedOffset);
		this.left.addWidget(this.riverCount);
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_MAIN_RIVERS));
		this.left.addWidget(this.mainRiverBedDepth);
		this.left.addWidget(this.mainRiverMinBankHeight);
		this.left.addWidget(this.mainRiverMaxBankHeight);
		this.left.addWidget(this.mainRiverBedWidth);
		this.left.addWidget(this.mainRiverBankWidth);
		this.left.addWidget(this.mainRiverFade);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_BRANCH_RIVERS));
		this.left.addWidget(this.branchRiverBedDepth);
		this.left.addWidget(this.branchRiverMinBankHeight);
		this.left.addWidget(this.branchRiverMaxBankHeight);
		this.left.addWidget(this.branchRiverBedWidth);
		this.left.addWidget(this.branchRiverBankWidth);
		this.left.addWidget(this.branchRiverFade);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_LAKES));
		this.left.addWidget(this.lakeChance);
		this.left.addWidget(this.lakeMinStartDistance);
		this.left.addWidget(this.lakeMaxStartDistance);
		this.left.addWidget(this.lakeDepth);
		this.left.addWidget(this.lakeSizeMin);
		this.left.addWidget(this.lakeSizeMax);
		this.left.addWidget(this.lakeMinBankHeight);
		this.left.addWidget(this.lakeMaxBankHeight);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_WETLANDS));
		this.left.addWidget(this.wetlandChance);
		this.left.addWidget(this.wetlandSizeMin);
		this.left.addWidget(this.wetlandSizeMax);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.of(new TerrainSettingsPage(this.screen, this.preset));
	}

	@Override
	public Optional<Page> next() {
		return Optional.of(new FilterSettingsPage(this.screen, this.preset));
	}

}
