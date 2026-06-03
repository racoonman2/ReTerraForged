package raccoonman.reterraforged.data.preset.registry;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.pages.BiomeOptions;
import raccoonman.reterraforged.preset.pages.StructureOptions;
import raccoonman.reterraforged.preset.pages.WorldOptions;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.registry.RegistryFilter;
import raccoonman.reterraforged.registry.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.feature.placement.RTFPlacementModifiers;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.modifier.AddFeaturePlacementModifier;
import raccoonman.reterraforged.world.worldgen.modifier.InsertFeatureModifier;
import raccoonman.reterraforged.world.worldgen.modifier.InsertionOrder;
import raccoonman.reterraforged.world.worldgen.modifier.RemoveFeatureModifier;
import raccoonman.reterraforged.world.worldgen.modifier.ReplaceFeatureModifier;
import raccoonman.reterraforged.world.worldgen.modifier.SetLavaLevelModifier;
import raccoonman.reterraforged.world.worldgen.modifier.SetMaxHeightLayerModifier;
import raccoonman.reterraforged.world.worldgen.modifier.StructurePlacementModifier;

public class RTFModifiers {
	public static final ResourceKey<Modifier> MAX_HEIGHT = createKey("max_height_layer");
	public static final ResourceKey<Modifier> LAVA_LEVEL = createKey("lava_level");
	
	public static final ResourceKey<Modifier> DISABLE_SPRING_WATER = createKey("features/disabled/spring_water");
	public static final ResourceKey<Modifier> DISABLE_SPRING_LAVA = createKey("features/disabled/spring_lava");
	public static final ResourceKey<Modifier> DISABLE_SPRING_LAVA_FROZEN = createKey("features/disabled/spring_lava_frozen");
	public static final ResourceKey<Modifier> DISABLE_LAKE_LAVA_SURFACE = createKey("features/disabled/lake_lava_surface");
	public static final ResourceKey<Modifier> DISABLE_LAKE_LAVA_UNDERGROUND = createKey("features/disabled/lake_lava_underground");
	
	private static final Set<ResourceKey<LevelStem>> LEVELS = ImmutableSet.of(LevelStem.OVERWORLD);
	
	public static void bootstrap(Preset preset, BootstrapContext<Modifier> ctx, StructureOptions.Placements structurePlacements) {
		ctx.register(MAX_HEIGHT, maxHeight(ctx));
		ctx.register(LAVA_LEVEL, lavaLevel(preset));

		HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
		if(!preset.getOption(BiomeOptions.SPRINGS)) {
			ctx.register(DISABLE_SPRING_WATER, disable(placedFeatures, MiscOverworldPlacements.SPRING_WATER));
		}
	
		if(!preset.getOption(BiomeOptions.LAVA_SPRINGS)) {
			ctx.register(DISABLE_SPRING_LAVA, disable(placedFeatures, MiscOverworldPlacements.SPRING_LAVA));
			ctx.register(DISABLE_SPRING_LAVA_FROZEN, disable(placedFeatures, MiscOverworldPlacements.SPRING_LAVA_FROZEN));
		}
	
		if(!preset.getOption(BiomeOptions.LAVA_LAKES)) {
			ctx.register(DISABLE_LAKE_LAVA_SURFACE, disable(placedFeatures, MiscOverworldPlacements.LAKE_LAVA_SURFACE));
			ctx.register(DISABLE_LAKE_LAVA_UNDERGROUND, disable(placedFeatures, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND));
		}
		
		registerStructureModifiers(preset, ctx, structurePlacements);
	}
	
	private static Modifier maxHeight(BootstrapContext<Modifier> ctx) {
		Holder<Layer.Factory<Float>> maxHeightLayer = RegistryUtil.lookupTyped(ctx, RTFLayers.MAX_HEIGHT_LAYER);
		return new SetMaxHeightLayerModifier(Set.of(LevelStem.OVERWORLD), maxHeightLayer);
	}
	
	private static Modifier lavaLevel(Preset preset) {
		int lavaLevel = preset.getOption(WorldOptions.LAVA_LEVEL);
		return new SetLavaLevelModifier(LEVELS, lavaLevel);
	}
	
	private static void registerStructureModifiers(Preset preset, BootstrapContext<Modifier> ctx, StructureOptions.Placements structurePlacements) {
		HolderGetter<StructureSet> structureSets = ctx.lookup(Registries.STRUCTURE_SET);
		MutableBoolean modified = new MutableBoolean();
		structurePlacements
			.placements()
			.forEach((key, factory) -> {
				modified.setFalse();
				factory.visitOptions((option) -> {
					if(preset.isModified(option)) {
						modified.setTrue();
					}
				});

				if(!modified.getValue()) {
					return;
				}
				
				Holder<StructureSet> structureSet = structureSets.getOrThrow(key);
				StructurePlacement placement = factory.makePlacement(preset);
				ResourceLocation location = key.location();
				ResourceKey<Modifier> modifierKey = createKey("structure/" + location.getNamespace() + "/" + location.getPath());
				ctx.register(modifierKey, new StructurePlacementModifier(structureSet, placement));	
			});
	}
	
	private static Modifier disable(HolderGetter<PlacedFeature> features, ResourceKey<PlacedFeature> target) {
		return new AddFeaturePlacementModifier(features.getOrThrow(target), ImmutableList.of(RTFPlacementModifiers.dimension(false, Level.OVERWORLD)), InsertionOrder.PREPEND);
	}
	
	private static Modifier insert(RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter, ResourceKey<PlacedFeature> target, HolderSet<PlacedFeature> additions) {
		return new InsertFeatureModifier(filter, target, additions);
	}
	
	private static Modifier replace(RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter, ResourceKey<PlacedFeature> target, Holder<PlacedFeature> replacement) {
		return replace(filter, ImmutableMap.of(target, replacement));
	}
	
	private static Modifier replace(RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter, Map<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> replacements) {
		return new ReplaceFeatureModifier(filter, replacements);
	}

	@SafeVarargs
	private static Modifier remove(RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter, HolderGetter<PlacedFeature> featureLookup, ResourceKey<PlacedFeature>... keys) {
		return new RemoveFeatureModifier(filter, HolderSet.direct(featureLookup::getOrThrow, keys));
	}
	
	@SafeVarargs
	private static RegistryFilter<Holder<Biome>, HolderSet<Biome>> inclusive(HolderGetter<Biome> biomeLookup, ResourceKey<Biome>... keys) {
		return RegistryFilter.inclusive(holderSet(biomeLookup, keys));
	}
	
	@SafeVarargs
	private static <T> HolderSet<T> holderSet(HolderGetter<T> lookup, ResourceKey<T>... keys) {
		return HolderSet.direct(lookup::getOrThrow, keys);
	}
	
	private static ResourceKey<Modifier> createKey(String name) {
		return RTFRegistries.createKey(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, name);
	}
}
