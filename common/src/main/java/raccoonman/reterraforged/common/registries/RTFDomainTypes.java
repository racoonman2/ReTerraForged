package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.noise.domain.AddWarp;
import raccoonman.reterraforged.common.noise.domain.CacheWarp;
import raccoonman.reterraforged.common.noise.domain.CompoundWarp;
import raccoonman.reterraforged.common.noise.domain.CumulativeWarp;
import raccoonman.reterraforged.common.noise.domain.DirectionWarp;
import raccoonman.reterraforged.common.noise.domain.Domain;
import raccoonman.reterraforged.common.noise.domain.DomainWarp;
import raccoonman.reterraforged.common.noise.domain.ShiftWarp;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFDomainTypes {
	
	public static void bootstrap() {
		register("cache_warp", CacheWarp.CODEC);
		register("shift_warp", ShiftWarp.CODEC);
		register("add_warp", AddWarp.CODEC);
		register("compound_warp", CompoundWarp.CODEC);
		register("cumulative_warp", CumulativeWarp.CODEC);
		register("direction_warp", DirectionWarp.CODEC);
		register("domain_warp", DomainWarp.CODEC);
	}
	
	private static void register(String name, Codec<? extends Domain> value) {
		RegistryUtil.register(RTFBuiltInRegistries.DOMAIN_TYPE, name, value);
	}
}
