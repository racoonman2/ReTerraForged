package raccoonman.reterraforged.common.level.levelgen.rule;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;

abstract class CachedXZRule implements SurfaceRule {
	protected Context surfaceContext;
	private long lastUpdate;
	
	public CachedXZRule(Context surfaceContext) {
		this.surfaceContext = surfaceContext;
		this.lastUpdate = Long.MIN_VALUE;
	}
	
	abstract void cache(int x, int z);
	
	abstract BlockState apply(int x, int y, int z);
	
	@Override
	public BlockState tryApply(int x, int y, int z) {
		if(this.lastUpdate != this.surfaceContext.lastUpdateXZ) {
			this.cache(x, z);
			this.lastUpdate = this.surfaceContext.lastUpdateXZ;
		}
		return this.apply(x, y, z);
	}
}
