package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public final class RTFClimates {
	public static final ResourceKey<Climate> TEMPERATE = resolve("temperate");
	public static final ResourceKey<Climate> DEEP = resolve("deep");
	
	public static void register(BootstapContext<Climate> ctx) {
		HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
		
		ctx.register(TEMPERATE, new Climate(
			new WeightMap.Builder<>()
				.entry(1.0F, biomes.getOrThrow(Biomes.PLAINS))
				.build()
		));
		ctx.register(DEEP, new Climate(
			new WeightMap.Builder<>()
				.entry(1.0F, biomes.getOrThrow(Biomes.DEEP_DARK))
				.build()
		));

	}
	
	private static ResourceKey<Climate> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.CLIMATE, path);
	}
}
