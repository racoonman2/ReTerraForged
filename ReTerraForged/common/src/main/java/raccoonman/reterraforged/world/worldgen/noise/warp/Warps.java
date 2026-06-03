package raccoonman.reterraforged.world.worldgen.noise.warp;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registry.RTFBuiltInRegistries;
import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.Noise.Visitor;
import raccoonman.reterraforged.world.worldgen.noise.Noises;

public class Warps {

	public static void bootstrap() {
		register("noise", NoiseWarp.CODEC);
		register("direction", DirectionWarp.CODEC);
		register("add", AddWarp.CODEC);
		register("compound", CompoundWarp.CODEC);
		register("worley_center", WorleyCenter.CODEC);
		register("frequency", FrequencyWarp.CODEC);
		register("sequence", SequenceWarp.CODEC);
	}
	
	public static Warp perlin(int seed, int scale, int octaves, float strength) {
		return noise(
			Noises.perlin(seed, scale, octaves), 
			Noises.perlin(seed + 1, scale, octaves), 
			Noises.constant(strength)
		);
	}
	
	public static Warp perlin2(int seed, int scale, int octaves, float lacunarity, float gain, float strength) {
		return noise(
			Noises.perlin2(seed, scale, octaves, lacunarity, gain), 
			Noises.perlin2(seed + 1, scale, octaves, lacunarity, gain), 
			Noises.constant(strength)
		);
	}
	
	public static Warp simplex(int seed, int scale, int octaves, float strength) {
		return noise(
			Noises.simplex(seed, scale, octaves), 
			Noises.simplex(seed + 1, scale, octaves), 
			Noises.constant(strength)
		);
	}
	
	public static Warp noise(Noise x, Noise z, Noise distance) {
		return new NoiseWarp(x, z, distance);
	}
	
	public static Warp direction(Noise direction, Noise distance) {
		return new DirectionWarp(direction, distance);
	}
	
	public static Warp add(Warp input1, Warp input2) {
		return new AddWarp(input1, input2);
	}
	
	public static Warp compound(Warp input1, Warp input2) {
		return new CompoundWarp(input1, input2);
	}
	
	public static Warp worleyCenter() {
		return new WorleyCenter();
	}

	public static Warp frequency(Warp input, float frequency) {
		return frequency(input, Noises.constant(frequency), Noises.constant(frequency));
	}
	
	public static Warp frequency(Warp input, Noise xFrequency, Noise zFrequency) {
		return new FrequencyWarp(input, xFrequency, zFrequency);
	}
	
	public static Warp sequence(Warp warp1, Warp warp2) {
		return new SequenceWarp(warp1, warp2);
	}
	
	private static void register(String name, MapCodec<? extends Warp> value) {
		RegistryUtil.register(RTFBuiltInRegistries.WARP_TYPE, name, value);
	}

	public record HolderHolder(Holder<Warp> holder) implements Warp {

		@Override
		public float getX(float x, float z, int seed) {
			return this.holder.value().getX(x, z, seed);
		}

		@Override
		public float getZ(float x, float z, int seed) {
			return this.holder.value().getZ(x, z, seed);
		}

		@Override
		public float getOffsetX(float x, float z, int seed) {
			return this.holder.value().getOffsetX(x, z, seed);
		}

		@Override
		public float getOffsetZ(float x, float z, int seed) {
			return this.holder.value().getOffsetZ(x, z, seed);
		}
		
		@Override
		public Warp mapAll(Visitor visitor) {
			return new HolderHolder(Holder.direct(this.holder.value().mapAll(visitor)));
		}

		@Override
		public MapCodec<HolderHolder> codec() {
			throw new UnsupportedOperationException("Called .codec() on HolderHolder");
		}
	}
}
