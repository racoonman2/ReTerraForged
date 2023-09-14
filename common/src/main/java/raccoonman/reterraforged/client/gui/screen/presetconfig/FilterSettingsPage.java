package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.presetconfig.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.common.data.preset.Preset;
import raccoonman.reterraforged.common.data.preset.settings.FilterSettings;
import raccoonman.reterraforged.common.data.preset.settings.FilterSettings.Erosion;
import raccoonman.reterraforged.common.data.preset.settings.FilterSettings.Smoothing;

public class FilterSettingsPage extends PresetEditorPage {
	private Slider erosionDropletsPerChunk;
	private Slider erosionDropletLifetime;
	private Slider erosionDropletVolume;
	private Slider erosionDropletVelocity;
	private Slider erosionRate;
	private Slider depositeRate;
	
	private Slider smoothingIterations;
	private Slider smoothingRadius;
	private Slider smoothingRate;
	
	public FilterSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}

	@Override
	public void init() {
		super.init();
		
		Preset preset = this.preset.getPreset();
		FilterSettings filters = preset.filters();
		
		Erosion erosion = filters.erosion;
		this.erosionDropletsPerChunk = PresetWidgets.createIntSlider(erosion.dropletsPerChunk, 10, 250, RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLETS_PER_CHUNK, (value) -> {
			erosion.dropletsPerChunk = (int) this.erosionDropletsPerChunk.scaleValue((float) value);
			return value;
		});
		this.erosionDropletLifetime = PresetWidgets.createIntSlider(erosion.dropletLifetime, 1, 32, RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_LIFETIME, (value) -> {
			erosion.dropletLifetime = (int) this.erosionDropletLifetime.scaleValue((float) value);
			return value;
		});
		this.erosionDropletVolume = PresetWidgets.createFloatSlider(erosion.dropletVolume, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VOLUME, (value) -> {
			erosion.dropletVolume = this.erosionDropletVolume.scaleValue((float) value);
			return value;
		});
		this.erosionDropletVelocity = PresetWidgets.createFloatSlider(erosion.dropletVelocity, 0.1F, 1.0F, RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VELOCITY, (value) -> {
			erosion.dropletVelocity = this.erosionDropletVelocity.scaleValue((float) value);
			return value;
		});
		this.erosionRate = PresetWidgets.createFloatSlider(erosion.erosionRate, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_EROSION_RATE, (value) -> {
			erosion.erosionRate =  this.erosionRate.scaleValue((float) value);
			return value;
		});
		this.depositeRate = PresetWidgets.createFloatSlider(erosion.depositeRate, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_DEPOSITE_RATE, (value) -> {
			erosion.depositeRate = this.depositeRate.scaleValue((float) value);
			return value;
		});
		
		Smoothing smoothing = filters.smoothing;
		this.smoothingIterations = PresetWidgets.createIntSlider(smoothing.iterations, 0, 5, RTFTranslationKeys.GUI_SLIDER_SMOOTHING_ITERATIONS, (value) -> {
			smoothing.iterations = (int) this.smoothingIterations.scaleValue((float) value);
			return value;
		});
		this.smoothingRadius = PresetWidgets.createFloatSlider(smoothing.smoothingRadius, 0, 5, RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RADIUS, (value) -> {
			smoothing.smoothingRadius = this.smoothingRadius.scaleValue((float) value);
			return value;
		});
		this.smoothingRate = PresetWidgets.createFloatSlider(smoothing.smoothingRate, 0.0F, 1.0F, RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RATE, (value) -> {
			smoothing.smoothingRate = this.smoothingRate.scaleValue((float) value);
			return value;
		});
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_EROSION));
		this.left.addWidget(this.erosionDropletsPerChunk);
		this.left.addWidget(this.erosionDropletLifetime);
		this.left.addWidget(this.erosionDropletVolume);
		this.left.addWidget(this.erosionDropletVelocity);
		this.left.addWidget(this.erosionRate);
		this.left.addWidget(this.depositeRate);
		
		this.left.addWidget(PresetWidgets.createLabel(RTFTranslationKeys.GUI_LABEL_SMOOTHING));
		this.left.addWidget(this.smoothingIterations);
		this.left.addWidget(this.smoothingRadius);
		this.left.addWidget(this.smoothingRate);
	}
		
	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_FILTER_SETTINGS_TITLE);
	}

	@Override
	public Optional<Page> previous() {
		return Optional.of(new RiverSettingsPage(this.screen, this.preset));
	}

	@Override
	public Optional<Page> next() {
		return Optional.empty();
	}

}
