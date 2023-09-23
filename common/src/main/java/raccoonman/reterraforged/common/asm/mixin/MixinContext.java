package raccoonman.reterraforged.common.asm.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.SurfaceExtension;

@Mixin(Context.class)
@Implements(@Interface(iface = ContextExtension.class, prefix = ReTerraForged.MOD_ID + "$ContextExtension$"))
class MixinContext {
	private List<SurfaceExtension> extensions = new ArrayList<>();
	private List<SurfaceExtension> decorators = new ArrayList<>();
	
	public void reterraforged$ContextExtension$addSurfaceExtension(SurfaceExtension extension) {
		this.extensions.add(extension);
	}

	public void reterraforged$ContextExtension$addSurfaceDecorator(SurfaceExtension extension) {
		this.decorators.add(extension);
	}
	
	public void reterraforged$ContextExtension$applySurfaceExtensions(BlockColumn column) {
		for(SurfaceExtension extension : this.extensions) {
			extension.apply(column);
		}
	}
	
	public void reterraforged$ContextExtension$applySurfaceDecorators(BlockColumn column) {
		for(SurfaceExtension extension : this.decorators) {
			extension.apply(column);
		}
	}
}
