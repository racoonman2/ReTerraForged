package raccoonman.reterraforged.preset.pages;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.mutable.MutableObject;

import com.google.common.collect.ImmutableMap;

import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import raccoonman.reterraforged.data.RTFTranslationKeys;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.option.BoolOption;
import raccoonman.reterraforged.preset.option.Category;
import raccoonman.reterraforged.preset.option.FloatOption;
import raccoonman.reterraforged.preset.option.IntOption;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.option.Page;
import raccoonman.reterraforged.world.worldgen.structure.DisabledStructurePlacement;

public class StructureOptions {
	private static final ResourceKey<StructureSet> EMPTY_STRUCTURE_SET = ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(Lithostitched.MOD_ID, "empty"));
	public static final Category PLACEMENT = new Category("placement", Component.translatable(RTFTranslationKeys.prependModId("structures.placement")));
//	public static final Option<Boolean> TERRAIN_BASED_PLACEMENT = BoolOption.builder("terrainBasedPlacement", true).tooltipModifier(TooltipModifier.EXPERIMENTAL).add(PLACEMENT);
//	TODO public static final Option<Boolean> EXTEND_GENERATION_DEPTH = Option.boolBuilder("extendGenerationDepth", false).add(PLACEMENT);

	public static Page makePage(StructureOptions.Placements structurePlacements) {
		Page page = Page.make("structures");
		page.addCategory(PLACEMENT);
		structurePlacements.placements().forEach((key, factory) -> {
			String categoryName = key.location().toString();
			Category category = page.addCategory(categoryName, Component.translatable(categoryName));
			factory.visitOptions(category::addOption);
		});
		return page;
	}
	
	@Deprecated
	public record Placements(Map<ResourceKey<StructureSet>, PlacementFactory> placements) {

		public static StructureOptions.Placements make(RegistryAccess registryAccess) {
			ImmutableMap.Builder<ResourceKey<StructureSet>, PlacementFactory> placements = ImmutableMap.builder();
			registryAccess.lookupOrThrow(Registries.STRUCTURE_SET).listElements().forEach((holder) -> {
				ResourceKey<StructureSet> key = holder.key();
				if(key.equals(EMPTY_STRUCTURE_SET)) {
					return;
				}
				
				StructureSet structureSet = holder.value();
				StructurePlacement placement = structureSet.placement();
				Class<? extends StructurePlacement> placementType = placement.getClass();
			
				Option<Boolean> enabled = BoolOption.builder("enabled", true).displayName(RTFTranslationKeys.STRUCTURE_ENABLED).build();
				Option<Float> frequency = FloatOption.builder("frequency", placement.frequency, 0.0F, 1.0F).condition(enabled).displayName(RTFTranslationKeys.STRUCTURE_FREQUENCY).build();
				if(placementType.equals(RandomSpreadStructurePlacement.class)) {
					RandomSpreadStructurePlacement randomSpreadPlacement = (RandomSpreadStructurePlacement) placement;
					MutableObject<Option<Integer>> separationHolder = new MutableObject<>();
					
					int padding = 1;
//					4096 is technically the max value for these but it makes it too difficult to make small adjustments in the gui slider
					Option<Integer> spacing = IntOption.builder("spacing", randomSpreadPlacement.spacing(), 0, 1024).lower((preset) -> preset.getOption(separationHolder.getValue()) + padding).condition(enabled).displayName(RTFTranslationKeys.STRUCTURE_SPACING).build();
					Option<Integer> separation = IntOption.builder("separation", randomSpreadPlacement.separation(), 0, 1024).upper((preset) -> preset.getOption(spacing) - padding).condition(enabled).displayName(RTFTranslationKeys.STRUCTURE_SEPARATION).build();
					separationHolder.setValue(separation);
					
					placements.put(key, new RandomSpreadFactory(enabled, frequency, spacing, separation, randomSpreadPlacement));
				}
				
				if(placementType.equals(ConcentricRingsStructurePlacement.class)) {
					ConcentricRingsStructurePlacement concentricRingsPlacement = (ConcentricRingsStructurePlacement) placement;
					Option<Integer> distance = IntOption.builder("distance", concentricRingsPlacement.distance(), 0, 1023).condition(enabled).displayName(RTFTranslationKeys.STRUCTURE_DISTANCE).build();
					Option<Integer> spread = IntOption.builder("spread", concentricRingsPlacement.spread(), 0, 1023).condition(enabled).displayName(RTFTranslationKeys.STRUCTURE_SPREAD).build();
					Option<Integer> count = IntOption.builder("count", concentricRingsPlacement.count(), 1, 4095).condition(enabled).displayName(RTFTranslationKeys.STRUCTURE_COUNT).build();
					placements.put(key, new ConcentricRingsFactory(enabled, frequency, distance, spread, count, concentricRingsPlacement));
				}
			});
			return new StructureOptions.Placements(placements.build());
		}
		
	}
	
	public interface PlacementFactory {
		void visitOptions(Consumer<Option<?>> visitor);

		StructurePlacement makePlacement(Preset preset);
	}
	
	private record RandomSpreadFactory(Option<Boolean> enabled, Option<Float> frequency, Option<Integer> spacing, Option<Integer> separation, RandomSpreadStructurePlacement placement) implements PlacementFactory {

		@Override
		public void visitOptions(Consumer<Option<?>> visitor) {
			visitor.accept(this.enabled);
			visitor.accept(this.frequency);
			visitor.accept(this.spacing);
			visitor.accept(this.separation);
		}

		@Override
		public StructurePlacement makePlacement(Preset preset) {
			boolean enabled = preset.getOption(this.enabled);
			if(!enabled) {
				return DisabledStructurePlacement.INSTANCE;
			}
			
			float frequency = preset.getOption(this.frequency);
			int spacing = preset.getOption(this.spacing);
			int separation = preset.getOption(this.separation);
			return new RandomSpreadStructurePlacement(this.placement.locateOffset, this.placement.frequencyReductionMethod, frequency, this.placement.salt, this.placement.exclusionZone, spacing, separation, this.placement.spreadType());
		}
	}
	
	private record ConcentricRingsFactory(Option<Boolean> enabled, Option<Float> frequency, Option<Integer> distance, Option<Integer> spread, Option<Integer> count, ConcentricRingsStructurePlacement placement) implements PlacementFactory {

		@Override
		public void visitOptions(Consumer<Option<?>> visitor) {
			visitor.accept(this.enabled);
			visitor.accept(this.frequency);
			visitor.accept(this.distance);
			visitor.accept(this.spread);
			visitor.accept(this.count);
		}

		@Override
		public StructurePlacement makePlacement(Preset preset) {
			boolean enabled = preset.getOption(this.enabled);
			if(!enabled) {
				return DisabledStructurePlacement.INSTANCE;
			}
			
			float frequency = preset.getOption(this.frequency);
			int distance = preset.getOption(this.distance);
			int spread = preset.getOption(this.spread);
			int count = preset.getOption(this.count);
			return new ConcentricRingsStructurePlacement(this.placement.locateOffset, this.placement.frequencyReductionMethod, frequency, this.placement.salt, this.placement.exclusionZone, distance, spread, count, this.placement.preferredBiomes());
		}
	}
}
