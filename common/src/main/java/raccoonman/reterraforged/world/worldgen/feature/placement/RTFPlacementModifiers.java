package raccoonman.reterraforged.world.worldgen.feature.placement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.feature.placement.poisson.FastPoissonModifier;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;

public class RTFPlacementModifiers {
	public static final PlacementModifierType<DimensionFilter> DIMENSION_FILTER = register("dimension_filter", DimensionFilter.CODEC);
	public static final PlacementModifierType<TerrainFilter> TERRAIN_FILTER = register("terrain_filter", TerrainFilter.CODEC);
	public static final PlacementModifierType<MacroBiomeFilter> MACRO_BIOME_FILTER = register("macro_biome_filter", MacroBiomeFilter.CODEC);
	public static final PlacementModifierType<NoiseFilter> NOISE_FILTER = register("noise_filter", NoiseFilter.CODEC);
	public static final PlacementModifierType<FastPoissonModifier> FAST_POISSON = register("fast_poission", FastPoissonModifier.CODEC);
	public static final PlacementModifierType<LegacyCountExtraModifier> LEGACY_COUNT_EXTRA = register("legacy_count_extra", LegacyCountExtraModifier.CODEC);

    public static void bootstrap() {
    }
    
    @SafeVarargs
	public static DimensionFilter dimensionFilter(ResourceKey<LevelStem>... levels) {
    	return new DimensionFilter(ImmutableList.copyOf(levels));
    }
    
    public static TerrainFilter terrainFilter(boolean exclude, Terrain... terrain) {
    	return new TerrainFilter(ImmutableSet.copyOf(terrain), exclude);
    }
    
    public static MacroBiomeFilter macroBiomeFilter(float chance) {
    	return new MacroBiomeFilter(chance);
    }
	
    public static NoiseFilter noiseFilter(Holder<Noise> noise, float threshold) {
    	return new NoiseFilter(noise, threshold);
    }
    
    public static FastPoissonModifier poisson(int radius, float scale, float biomeFade, int densityVariationScale, float densityVariation) {
		 return poisson(radius, scale, 0.8F, biomeFade, densityVariationScale, densityVariation);
	}
	
    public static FastPoissonModifier poisson(int radius, float scale, float jitter, float biomeFade, int densityVariationScale, float densityVariation) {
		 return new FastPoissonModifier(radius, scale, jitter, biomeFade, densityVariationScale, densityVariation);
	}
    
    @Deprecated
    public static LegacyCountExtraModifier countExtra(int count, float extraChance, int extraCount) {
    	return new LegacyCountExtraModifier(count, extraChance, extraCount);
    }
    
    private static <P extends PlacementModifier> PlacementModifierType<P> register(String name, Codec<P> codec) {
    	PlacementModifierType<P> type = () -> codec;
		RegistryUtil.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, name, type);
		return type;
    }
}
