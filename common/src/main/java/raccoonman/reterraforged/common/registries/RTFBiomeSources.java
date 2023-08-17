package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.biome.source.RTFBiomeSource;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFBiomeSources {
	public static final ResourceKey<Codec<? extends BiomeSource>> RETERRAFORGED = resolve("reterraforged");
	
	public static void register() {
    	RegistryUtil.register(RETERRAFORGED, () -> RTFBiomeSource.CODEC);
	}
	
	private static ResourceKey<Codec<? extends BiomeSource>> resolve(String path) {
		return ReTerraForged.resolve(Registries.BIOME_SOURCE, path);
	}
}
