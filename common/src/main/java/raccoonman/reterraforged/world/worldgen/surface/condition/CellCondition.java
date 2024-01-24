package raccoonman.reterraforged.world.worldgen.surface.condition;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.tile.Tile;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

abstract class CellCondition implements SurfaceRules.Condition {
	protected SurfaceRules.Context context;
	@Nullable
	private Tile.Chunk chunk;
	private long lastXZ;
	private boolean lastResult;
		
	public CellCondition(SurfaceRules.Context context) {
		this.context = context;
		GeneratorContext generatorContext;
		//TODO store this in SurfaceRules$Context instead so we can cache the chunk lookup
		if((Object) context.randomState instanceof RTFRandomState randomState && (generatorContext = randomState.generatorContext()) != null) {
			ChunkPos chunkPos = context.chunk.getPos();
			this.chunk = generatorContext.cache.provideChunk(chunkPos.x, chunkPos.z);
		}
		this.lastXZ = Long.MIN_VALUE;
	}
		
	public abstract boolean test(Cell cell, int x, int z);
	
	@Override
	public boolean test() {
        int x = this.context.blockX;
        int z = this.context.blockZ;
        long packedPos = PosUtil.pack(x, z);
        if(this.lastXZ != packedPos) {
        	this.lastXZ = packedPos;
            Cell cell = this.chunk.getCell(x, z);
        	this.lastResult = this.test(cell, x, z);
        }
        return this.lastResult;
	}
}
