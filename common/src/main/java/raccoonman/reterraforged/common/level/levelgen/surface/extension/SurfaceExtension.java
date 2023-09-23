package raccoonman.reterraforged.common.level.levelgen.surface.extension;

import net.minecraft.world.level.chunk.BlockColumn;

// TODO the surface is built in x -> z -> y order so we can make this into a y cached surface rule
@Deprecated
public interface SurfaceExtension {
	void apply(BlockColumn column);
}
