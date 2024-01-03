package raccoonman.reterraforged.world.worldgen.noise.domain;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registries.RTFBuiltInRegistries;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class Domains {

	public static void bootstrap() {
		register("domain", DomainWarp.CODEC);
		register("direction", DirectionWarp.CODEC);
		register("compound", CompoundWarp.CODEC);
		register("add", AddWarp.CODEC);
		register("direct", DirectWarp.CODEC);
	}

	public static Domain domainPerlin(int seed, int scale, int octaves, float strength) {
		return domain(
			Noises.perlin(seed, scale, octaves), 
			Noises.perlin(seed + 1, scale, octaves), 
			Noises.constant(strength)
		);
	}
	
	public static Domain domainSimplex(int seed, int scale, int octaves, float strength) {
		return domain(
			Noises.simplex(seed, scale, octaves), 
			Noises.simplex(seed + 1, scale, octaves), 
			Noises.constant(strength)
		);
	}
	
	public static Domain domain(Noise x, Noise z, Noise distance) {
		return new DomainWarp(x, z, distance);
	}
	
	public static Domain direction(Noise direction, Noise distance) {
		return new DirectionWarp(direction, distance);
	}
	
	public static Domain compound(Domain input1, Domain input2) {
		return new CompoundWarp(input1, input2);
	}
	
	public static Domain add(Domain input1, Domain input2) {
		return new AddWarp(input1, input2);
	}
	
	public static Domain direct() {
		return new DirectWarp();
	}
	
	private static void register(String name, Codec<? extends Domain> value) {
		RegistryUtil.register(RTFBuiltInRegistries.DOMAIN_TYPE, name, value);
	}
}
