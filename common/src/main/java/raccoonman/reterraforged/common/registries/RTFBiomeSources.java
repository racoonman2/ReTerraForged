package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.biome.BiomeSource;
import raccoonman.reterraforged.common.level.levelgen.biome.source.ClimateBasedBiomeSource;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFBiomeSources {
	
	public static void bootstrap() {
    	register("climate_based", ClimateBasedBiomeSource.CODEC);
	}
	
	private static void register(String name, Codec<? extends BiomeSource> value) {
		RegistryUtil.register(BuiltInRegistries.BIOME_SOURCE, name, value);
	}
}
