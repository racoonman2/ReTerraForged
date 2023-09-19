package raccoonman.reterraforged.common.asm.extensions;

import net.minecraft.world.level.chunk.BlockColumn;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.SurfaceExtension;

public interface ContextExtension {
	void addSurfaceExtension(SurfaceExtension extension);
	
	void applySurfaceExtensions(BlockColumn column);
}
