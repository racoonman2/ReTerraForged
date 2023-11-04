package raccoonman.reterraforged.common.asm.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.CellSampler;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileProvider;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkReader;

@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	private int chunkX, chunkZ;
	private RandomState randomState;
	
	@Nullable
	private Cell[] sampleCache;
	
	@Shadow
	@Final
	private int cellCountXZ,
				cellWidth,
				noiseSizeXZ;
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/RandomState;router()Lnet/minecraft/world/level/levelgen/NoiseRouter;"
		),
		method = "<init>"
	)
	public NoiseRouter router(RandomState randomState, int i, RandomState rs, int j, int k) {
		this.randomState = randomState;

        this.chunkX = SectionPos.blockToSectionCoord(j);
        this.chunkZ = SectionPos.blockToSectionCoord(k);
		return randomState.router();
	}

	@Inject(
		at = @At("HEAD"),
		method = "wrapNew",
		cancellable = true
	)
	private void wrapNew(DensityFunction densityFunction, CallbackInfoReturnable<DensityFunction> callback) {
		if((Object) this.randomState instanceof RandomStateExtension randomStateExtension && densityFunction instanceof CellSampler function) {
			int width = 16;//this.cellCountXZ * this.cellWidth;
			if(this.sampleCache == null) {
				@Nullable
				TileProvider tileCache = randomStateExtension.tileCache();
				if(tileCache != null) {
					this.sampleCache = new Cell[width * width];
					try(ChunkReader chunk = tileCache.getChunk(this.chunkX, this.chunkZ)) {
						for(int x = 0; x < width; x++) {
							for(int z = 0; z < width; z++) {
								Cell cell = this.sampleCache[x * width + z] = new Cell();
								cell.copyFrom(chunk.getCell(x, z));
							}
						}
					}
				}
			}
			callback.setReturnValue(function.new Cached(function.channel(), this.sampleCache, this.chunkX * 16, this.chunkZ * 16, width));
		}
	}
}