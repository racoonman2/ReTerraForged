package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
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
	public static final ResourceKey<Codec<? extends Domain>> CACHE_WARP = resolve("cache_warp");
	public static final ResourceKey<Codec<? extends Domain>> SHIFT_WARP = resolve("shift_warp");
	public static final ResourceKey<Codec<? extends Domain>> ADD_WARP = resolve("add_warp");
	public static final ResourceKey<Codec<? extends Domain>> COMPOUND_WARP = resolve("compound_warp");
	public static final ResourceKey<Codec<? extends Domain>> CUMULATIVE_WARP = resolve("cumulative_warp");
	public static final ResourceKey<Codec<? extends Domain>> DIRECTION_WARP = resolve("direction_warp");
	public static final ResourceKey<Codec<? extends Domain>> DOMAIN_WARP = resolve("domain_warp");
	
	public static void register() {
		RegistryUtil.register(CACHE_WARP, () -> CacheWarp.CODEC);
		RegistryUtil.register(SHIFT_WARP, () -> ShiftWarp.CODEC);
		RegistryUtil.register(ADD_WARP, () -> AddWarp.CODEC);
		RegistryUtil.register(COMPOUND_WARP, () -> CompoundWarp.CODEC);
		RegistryUtil.register(CUMULATIVE_WARP, () -> CumulativeWarp.CODEC);
		RegistryUtil.register(DIRECTION_WARP, () -> DirectionWarp.CODEC);
		RegistryUtil.register(DOMAIN_WARP, () -> DomainWarp.CODEC);
	}
	
	private static ResourceKey<Codec<? extends Domain>> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.DOMAIN_TYPE, path);
	}
}
