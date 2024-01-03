package raccoonman.reterraforged.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.biome.RTFClimateSampler;
import raccoonman.reterraforged.world.worldgen.densityfunction.CellSampler;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile;

@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	private RandomState randomState;
	private int chunkX, chunkZ;
	@Nullable
	private Tile.Chunk chunk;
	private CellSampler.Cache2d cache2d;
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/RandomState;router()Lnet/minecraft/world/level/levelgen/NoiseRouter;"
		),
		method = "<init>"
	)
	public NoiseRouter NoiseChunk(RandomState randomState, int cellCountXZ, RandomState rs, int minBlockX, int minBlockZ) {
		this.randomState = randomState;
		this.chunkX = SectionPos.blockToSectionCoord(minBlockX);
		this.chunkZ = SectionPos.blockToSectionCoord(minBlockZ);
		GeneratorContext generatorContext;
		if((Object) randomState instanceof RTFRandomState rtfRandomState && cellCountXZ > 1 && (generatorContext = rtfRandomState.generatorContext()) != null) {
			Tile tile = generatorContext.cache.provideAtChunk(this.chunkX, this.chunkZ);
			this.chunk = tile.getChunkReader(this.chunkX, this.chunkZ);
		}
		this.cache2d = new CellSampler.Cache2d();
		return randomState.router();
	}
	
	@Inject(
		at = @At("RETURN"),
		method = "cachedClimateSampler"
	)
    protected void cachedClimateSampler(NoiseRouter noiseRouter, List<Climate.ParameterPoint> list, CallbackInfoReturnable<Climate.Sampler> callback) {
    	if((Object) callback.getReturnValue() instanceof RTFClimateSampler cachedSampler && (Object) this.randomState.sampler() instanceof RTFClimateSampler globalSampler) {
    		DensityFunction uniqueness = globalSampler.getUniqueness();

    		if(uniqueness != null) {
    			cachedSampler.setUniqueness(this.wrap(uniqueness));
    		}
    	}
    }

	@ModifyVariable(
		method = "<init>",
		at = @At("HEAD"),
		name = "fluidPicker",
		index = 7,
		ordinal = 0,
		argsOnly = true
	)
	private static Aquifer.FluidPicker modifyFluidPicker(Aquifer.FluidPicker fluidPicker, int i, RandomState randomState, int j, int k, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifierOrMarker, NoiseGeneratorSettings noiseGeneratorSettings) {
		if((Object) randomState instanceof RTFRandomState rtfRandomState) {
//			@Nullable
//			TileProvider tileCache = rtfRandomState.getTileCache();
//			if(tileCache != null) {
//				int lavaDepth = noiseSettings.minY() + 10;
//		        Aquifer.FluidStatus lava = new Aquifer.FluidStatus(lavaDepth, Blocks.LAVA.defaultBlockState());
//		        int seaLevel = noiseGeneratorSettings.seaLevel();
//		        Aquifer.FluidStatus defaultFluid = new Aquifer.FluidStatus(seaLevel, noiseGeneratorSettings.defaultFluid());
//		        return (x, y, z) -> {
//		            if (y < Math.min(lavaDepth, seaLevel)) {
//		                return lava;
//		            }
//		            return defaultFluid;
//		        };
//			}
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

	@Shadow
    public DensityFunction wrap(DensityFunction densityFunction) {
		throw new IllegalStateException();
    }
}