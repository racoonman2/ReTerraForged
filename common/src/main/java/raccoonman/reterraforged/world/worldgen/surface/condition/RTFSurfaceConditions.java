package raccoonman.reterraforged.world.worldgen.surface.condition;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;

public class RTFSurfaceConditions {

	public static void bootstrap() {
		register("mod", ModCondition.CODEC);
		register("biome_tag", BiomeTagCondition.CODEC);
		register("noise", NoiseCondition.Source.CODEC);
		register("terrain", TerrainCondition.Source.CODEC);
		register("height", HeightCondition.Source.CODEC);
		register("steepness", SteepnessCondition.Source.CODEC);
		register("erosion", ErosionCondition.Source.CODEC);
		register("sediment", SedimentCondition.Source.CODEC);
		register("river_bank", RiverBankCondition.Source.CODEC);
		register("height_modification_detection", HeightModificationDetection.Source.CODEC);
	}
	
	public static ModCondition modLoaded(String modId) {
		return new ModCondition(modId);
	}
	
	public static BiomeTagCondition biomeTag(TagKey<Biome> tag) {
		return new BiomeTagCondition(tag);
	}
	
	public static NoiseCondition.Source noise(Holder<Noise> noise, float threshold) {
		return new NoiseCondition.Source(noise, threshold);
	}
	
	public static TerrainCondition.Source terrain(Terrain... terrain) {
		return new TerrainCondition.Source(Arrays.stream(terrain).collect(Collectors.toSet()));
	}
	
	public static HeightCondition.Source height(float threshold) {
		return height(constant(threshold));
	}

	public static HeightCondition.Source height(float threshold, Holder<Noise> variance) {
		return height(constant(threshold), variance);
	}

	public static HeightCondition.Source height(Holder<Noise> threshold) {
		return height(threshold, constant(0.0F));
	}

	public static HeightCondition.Source height(Holder<Noise> threshold, Holder<Noise> variance) {
		return new HeightCondition.Source(threshold, variance);
	}
	
	public static SteepnessCondition.Source steepness(float threshold) {
		return steepness(constant(threshold), constant(0.0F));
	}

	public static SteepnessCondition.Source steepness(float threshold, Holder<Noise> variance) {
		return steepness(constant(threshold), variance);
	}
	
	public static SteepnessCondition.Source steepness(Holder<Noise> threshold, Holder<Noise> variance) {
		return new SteepnessCondition.Source(threshold, variance);
	}
	
	public static ErosionCondition.Source erosion(float threshold) {
		return erosion(constant(threshold), constant(0.0F));
	}
	
	public static ErosionCondition.Source erosion(Holder<Noise> threshold, Holder<Noise> variance) {
		return new ErosionCondition.Source(threshold, variance);
	}
	
	public static SedimentCondition.Source sediment(float threshold) {
		return sediment(constant(threshold), constant(0.0F));
	}
	
	public static SedimentCondition.Source sediment(Holder<Noise> threshold, Holder<Noise> variance) {
		return new SedimentCondition.Source(threshold, variance);
	}

	public static RiverBankCondition.Source riverBank(float threshold) {
		return riverBank(constant(threshold), constant(0.0F));
	}
	
	public static RiverBankCondition.Source riverBank(Holder<Noise> threshold, Holder<Noise> variance) {
		return new RiverBankCondition.Source(threshold, variance);
	}
	
	public static HeightModificationDetection.Source heightModificationDetection(HeightModificationDetection.Target target) {
		return new HeightModificationDetection.Source(target);
	}
	
	public static void register(String name, Codec<? extends SurfaceRules.ConditionSource> value) {
		RegistryUtil.register(BuiltInRegistries.MATERIAL_CONDITION, name, value);
	}
	
	private static Holder<Noise> constant(float value) {
		return Holder.direct(Noises.constant(value));
	}
}
