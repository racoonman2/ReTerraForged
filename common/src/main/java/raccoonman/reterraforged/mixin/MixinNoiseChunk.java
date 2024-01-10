package raccoonman.reterraforged.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.densityfunction.CellSampler;
import raccoonman.reterraforged.world.worldgen.densityfunction.ConditionalFlatCache;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile;

@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	private RandomState randomState;
	private int chunkX, chunkZ;
	@Nullable
	private Tile.Chunk chunk;
	private CellSampler.Cache2d cache2d;
	@Shadow
    @Final
	int firstNoiseX;
	@Shadow
    @Final
    int firstNoiseZ;
	@Shadow
    @Final
	private int cellCountXZ;
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/RandomState;router()Lnet/minecraft/world/level/levelgen/NoiseRouter;"
		),
		method = "<init>"
	)
	private NoiseRouter NoiseChunk(RandomState randomState1, int cellCountXZ, RandomState randomState2, int minBlockX, int minBlockZ) {
		this.randomState = randomState1;
		this.chunkX = SectionPos.blockToSectionCoord(minBlockX);
		this.chunkZ = SectionPos.blockToSectionCoord(minBlockZ);
		GeneratorContext generatorContext;
		if((Object) randomState instanceof RTFRandomState rtfRandomState && cellCountXZ > 1 && (generatorContext = rtfRandomState.generatorContext()) != null) {
			this.chunk = generatorContext.cache.provideAtChunk(this.chunkX, this.chunkZ).getChunkReader(this.chunkX, this.chunkZ);
		}
		this.cache2d = new CellSampler.Cache2d();
		return randomState.router();
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
		
        if(function instanceof ConditionalFlatCache cache && this.cellCountXZ == 1) {
//        	callback.setReturnValue(((NoiseChunk) (Object) this).new FlatCache(cache.function(), true));
        }
	}
}