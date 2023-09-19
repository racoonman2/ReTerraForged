package raccoonman.reterraforged.common.asm.extensions;

import net.minecraft.server.level.WorldGenRegion;

public interface NoiseChunkExtension {
	void applyNoiseFilters(WorldGenRegion region);
}
