package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Optional;
import java.util.Set;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen.Page;
import raccoonman.reterraforged.client.gui.screen.presetconfig.PresetListPage.PresetEntry;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.ValueButton;
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldPreset;
import raccoonman.reterraforged.data.worldgen.preset.settings.StructureSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.StructureSettings.StructureSetEntry;

public class StructureSettingsPage extends PresetEditorPage {

	public StructureSettingsPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen, preset);
	}

	@Override
	public Component title() {
		return Component.translatable(RTFTranslationKeys.GUI_STRUCTURE_SETTINGS_TITLE);
	}
	
	@Override
	public void init() {
		super.init();
		
		WorldPreset preset = this.preset.getPreset();
		StructureSettings structures = preset.structures();
		
		WorldCreationContext settings = this.screen.getSettings();
		RegistryAccess.Frozen registries = settings.worldgenLoadContext();
		
		registries.lookupOrThrow(Registries.STRUCTURE_SET).listElements().filter((holder) -> {
			return isOverworldStructureSet(settings.selectedDimensions(), holder);
		}).forEach((holder) -> {
			StructureSet set = holder.value();
			if(set.placement() instanceof RandomSpreadStructurePlacement placement) {
				structures.entries.computeIfAbsent(holder.key(), (k) -> {
					return new StructureSetEntry(placement.spacing(), placement.separation(), placement.salt(), false);
				});
			}
		});
		
		structures.entries.forEach((key, entry) -> {
			class SliderHolder {
				Slider slider;
			}
			SliderHolder seperationHolder = new SliderHolder();
			Slider spacing = PresetWidgets.createIntSlider(entry.spacing, 0, 1000, RTFTranslationKeys.GUI_SLIDER_SPACING, (slider, value) -> {
				value = Math.max(value, seperationHolder.slider.getValue() + slider.getSliderValue(1.0F));
				entry.spacing = (int) slider.scaleValue((float) value);
				return value;
			});
			Slider separation = PresetWidgets.createIntSlider(entry.separation, 0, 1000, RTFTranslationKeys.GUI_SLIDER_SEPARATION, (slider, value) -> {
				value = Math.min(value, spacing.getValue() - slider.getSliderValue(1.0F));
				entry.separation = (int) slider.scaleValue((float) value);
				return value;
			});
			seperationHolder.slider = separation;
			ValueButton<Integer> salt = PresetWidgets.createRandomButton(RTFTranslationKeys.GUI_BUTTON_SALT, entry.salt, (value) -> {
				entry.salt = value;
			});
			CycleButton<Boolean> disabled = PresetWidgets.createToggle(entry.disabled, RTFTranslationKeys.GUI_BUTTON_DISABLED, (button, value) -> {
				entry.disabled = value;
			});
			
			this.left.addWidget(PresetWidgets.createLabel(key.location().toString()));
			this.left.addWidget(spacing);
			this.left.addWidget(separation);
			this.left.addWidget(salt);
			this.left.addWidget(disabled);
		});
	}

	@Override
	public Optional<Page> previous() {
		return Optional.of(new FilterSettingsPage(this.screen, this.preset));
	}

	@Override
	public Optional<Page> next() {
		return Optional.of(new MiscellaneousPage(this.screen, this.preset));
	}

	private static boolean isOverworldStructureSet(WorldDimensions dimensions, Holder.Reference<StructureSet> holder) {
		Set<Holder<Biome>> overworldBiomes = dimensions.overworld().getBiomeSource().possibleBiomes();
		for(StructureSelectionEntry structureEntry : holder.value().structures()) {
			Structure structure = structureEntry.structure().value();

			for(Holder<Biome> biome : structure.biomes()) {
				if(overworldBiomes.contains(biome)) {
					return true;
				}
			}
		}
		return false;
	}
}
