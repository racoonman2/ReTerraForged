package raccoonman.reterraforged.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.data.preset.Preset;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.densityfunction.CellSampler;
import raccoonman.reterraforged.world.worldgen.densityfunction.CellSampler.Cache2d;
import raccoonman.reterraforged.world.worldgen.tile.Tile;

@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	private RandomState randomState;
	private int chunkX, chunkZ;
	private int generationHeight;
	private Tile.Chunk chunk;
	private Cache2d cache2d;
	
	@Shadow
    @Final
    private DensityFunction initialDensityNoJaggedness;
    
	@Shadow
    @Final
    private NoiseSettings noiseSettings;
    
	@Shadow
    @Final
	int firstNoiseX;
	
	@Shadow
    @Final
    int firstNoiseZ;
	
	@Shadow
    @Final
	private int cellCountXZ;
	
	@Shadow
	private int cellCountY;
	
	@Shadow
    @Final
    private int cellHeight;
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/RandomState;router()Lnet/minecraft/world/level/levelgen/NoiseRouter;"
		),
		method = "<init>"
	)
	private NoiseRouter NoiseChunk(RandomState randomState1, int cellCountXZ, RandomState randomState2, int minBlockX, int minBlockZ, NoiseSettings noiseSettings) {
		this.randomState = randomState1;
		this.chunkX = SectionPos.blockToSectionCoord(minBlockX);
		this.chunkZ = SectionPos.blockToSectionCoord(minBlockZ);
		GeneratorContext generatorContext;
		if((Object) this.randomState instanceof RTFRandomState rtfRandomState && (generatorContext = rtfRandomState.generatorContext()) != null) {
			boolean cache = CellSampler.isCachedNoiseChunk(cellCountXZ);
			
			this.generationHeight = generatorContext.lookup.getGenerationHeight(this.chunkX, this.chunkZ, noiseSettings, cache);
			this.cellCountY = Math.min(this.cellCountY, this.generationHeight / this.cellHeight);
			this.cache2d = new CellSampler.Cache2d();
			
			if(cache) {
				this.chunk = generatorContext.cache.provideAtChunk(this.chunkX, this.chunkZ).getChunkReader(this.chunkX, this.chunkZ);
			}
		} else {
			this.generationHeight = noiseSettings.height();
		}
		return this.randomState.router();
	}
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseSettings;height()I",
			ordinal = 1
		),
		require = 1,
		method = "<init>"
	)
	private int redirectHeight(NoiseSettings settings) {
		return this.generationHeight;
	}

	@ModifyVariable(
		method = "<init>",
		at = @At("HEAD"),
		name = "fluidPicker",
		index = 7,
		ordinal = 0,
		argsOnly = true
	)
	//TODO clean this up
	private static Aquifer.FluidPicker modifyFluidPicker(Aquifer.FluidPicker fluidPicker, int cellCountXZ, RandomState randomState, int minBlockX, int minBlockZ, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifierOrMarker, NoiseGeneratorSettings noiseGeneratorSettings) {
		if((Object) randomState instanceof RTFRandomState rtfRandomState) {
			@Nullable
			Preset preset = rtfRandomState.preset();
			if(preset != null && rtfRandomState.generatorContext() != null) {
				int lavaLevel = preset.world().properties.lavaLevel;
		        Aquifer.FluidStatus lava = new Aquifer.FluidStatus(lavaLevel, Blocks.LAVA.defaultBlockState());
		        int seaLevel = noiseGeneratorSettings.seaLevel();
		        Aquifer.FluidStatus defaultFluid = new Aquifer.FluidStatus(seaLevel, noiseGeneratorSettings.defaultFluid());
		        return (x, y, z) -> {
		        	if (y < Math.min(lavaLevel, seaLevel)) {
		                return lava;
		            }
		            return defaultFluid;
		        };
			}
		}
		return fluidPicker;
	}

	@Inject(
		at = @At("HEAD"),
		method = "wrapNew",
		cancellable = true
	)
	private void wrapNew(DensityFunction function, CallbackInfoReturnable<DensityFunction> callback) {
		if((Object) this.randomState instanceof RTFRandomState randomState && function instanceof CellSampler mapped) {
			callback.setReturnValue(mapped.new CacheChunk(this.chunk, this.cache2d, this.chunkX, this.chunkZ));
		}
	}
	
	@Overwrite
	private int computePreliminarySurfaceLevel(long l) {
        int blockX = ColumnPos.getX(l);
        int blockZ = ColumnPos.getZ(l);
        int k = this.noiseSettings.minY();
        int generationHeight = this.noiseSettings.height();
        if((Object) this.randomState instanceof RTFRandomState rtfRandomState) {
        	generationHeight = rtfRandomState.generatorContext().lookup.getGenerationHeight(this.chunkX, this.chunkZ, this.noiseSettings, false);
        }
        
        for (int m = k + generationHeight; m >= k; m -= this.cellHeight) {
            DensityFunction.SinglePointContext singlePointContext = new DensityFunction.SinglePointContext(blockX, m, blockZ);
            if (!(this.initialDensityNoJaggedness.compute(singlePointContext) > 0.390625)) continue;
            return m;
        }
        return Integer.MAX_VALUE;
    }
}