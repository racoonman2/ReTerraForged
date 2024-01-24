package raccoonman.reterraforged.world.worldgen.surface.condition;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;

public class RTFSurfaceConditions {

	public static void bootstrap() {
		register("terrain", TerrainCondition.Source.CODEC);
		register("height", HeightCondition.Source.CODEC);
		register("steepness", SteepnessCondition.Source.CODEC);
		register("erosion", ErosionCondition.Source.CODEC);
		register("sediment", SedimentCondition.Source.CODEC);
		register("distance_to_river", DistanceToRiverCondition.Source.CODEC);
	}
	
	public static TerrainCondition.Source isTerrain(Terrain... terrain) {
		return new TerrainCondition.Source(Arrays.stream(terrain).collect(Collectors.toSet()));
	}
	
	public static HeightCondition.Source isHigherThan(float threshold) {
		return isHigherThan(threshold, Holder.direct(Noises.zero()));
	}
	
	public static HeightCondition.Source isHigherThan(float threshold, Holder<Noise> variance) {
		return new HeightCondition.Source(threshold, variance);
	}
	
	public static SteepnessCondition.Source isSteeperThan(float threshold) {
		return isSteeperThan(threshold, Holder.direct(Noises.zero()));
	}

	public static SteepnessCondition.Source isSteeperThan(float threshold, Holder<Noise> variance) {
		return new SteepnessCondition.Source(threshold, variance);
	}
	
	public static ErosionCondition.Source isMoreErodedThan(float threshold, Holder<Noise> variance) {
		return new ErosionCondition.Source(threshold, variance);
	}
	
	public static ErosionCondition.Source isMoreErodedThan(float threshold) {
		return isMoreErodedThan(threshold, Holder.direct(Noises.zero()));
	}
	
	public static SedimentCondition.Source isMoreSedimentThan(float threshold) {
		return isMoreSedimentThan(threshold, Holder.direct(Noises.zero()));
	}
	
	public static SedimentCondition.Source isMoreSedimentThan(float threshold, Holder<Noise> variance) {
		return new SedimentCondition.Source(threshold, variance);
	}
	
	public static DistanceToRiverCondition.Source isCloserToRiverThan(float threshold) {
		return isCloserToRiverThan(threshold, Holder.direct(Noises.zero()));
	}
	
	public static DistanceToRiverCondition.Source isCloserToRiverThan(float threshold, Holder<Noise> variance) {
		return new DistanceToRiverCondition.Source(threshold, variance);
	}
	
	public static void register(String name, Codec<? extends SurfaceRules.ConditionSource> value) {
		RegistryUtil.register(BuiltInRegistries.MATERIAL_CONDITION, name, value);
	}
}
