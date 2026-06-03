package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.extensions.RTFSurfaceContext;

@Mixin(SurfaceRules.Context.class)
public class MixinSurfaceContext implements RTFSurfaceContext {
	private double terrainHeight = Double.MIN_VALUE;
	
	@Override
	public double getTerrainHeight() {
		return this.terrainHeight;
	}

	@Override
	public void setTerrainHeight(double terrainHeight) {
		this.terrainHeight = terrainHeight;
	}
}
