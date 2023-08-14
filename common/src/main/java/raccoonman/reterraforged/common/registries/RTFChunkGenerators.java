package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.generator.RTFChunkGenerator;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFChunkGenerators {
	public static final ResourceKey<Codec<? extends ChunkGenerator>> RETERRAFORGED = resolve("reterraforged");
	
	public static void register() {
    	RegistryUtil.register(RETERRAFORGED, () -> RTFChunkGenerator.CODEC);
	}
	
	private static ResourceKey<Codec<? extends ChunkGenerator>> resolve(String path) {
		return ReTerraForged.resolve(Registries.CHUNK_GENERATOR, path);
	}
}
