package raccoonman.reterraforged.common.asm.mixin;

import java.util.OptionalInt;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import raccoonman.reterraforged.common.asm.extensions.TerrainDataHolder;
import raccoonman.reterraforged.common.level.levelgen.generator.RTFChunkGenerator;

@Mixin(NoiseBasedChunkGenerator.class)
class MixinNoiseBasedChunkGenerator {

	@Inject(
		at = @At(
			value = "INVOKE",
			desc = @Desc(
				value = "initializeForFirstCellX",
				owner = NoiseChunk.class
			)
		),
		target = @Desc(
			value = "iterateNoiseColumn",
			ret = OptionalInt.class,
			args = {
				LevelHeightAccessor.class,
				RandomState.class,
				int.class,
				int.class,
				MutableObject.class,
				Predicate.class
			}
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void iterateNoiseColumn_generate(LevelHeightAccessor level, RandomState rand, int x, int z, @Nullable MutableObject<NoiseColumn> column, @Nullable Predicate<BlockState> filter, CallbackInfoReturnable<OptionalInt> callback, NoiseSettings noiseSettings, int i, int j, int k, int l, BlockState[] ablockstate, int i1, int j1, int k1, int l1, int i2, int j2, int k2, double d0, double d1, NoiseChunk noisechunk) {
		if((Object) this instanceof RTFChunkGenerator tf) {
			if(noisechunk instanceof TerrainDataHolder holder) {
				if(level instanceof TerrainDataHolder target) {
					holder.setTerrainData(target.getTerrainData());
				} else {
//					holder.setTerrainData(tf.getTerrainGenerator().generate(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)));
				}
			}
		}
	}
	
	@Inject(
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			desc = @Desc(
				value = "stopInterpolation",
				owner = NoiseChunk.class
			)
		),
		target = @Desc(
			value = "iterateNoiseColumn",
			ret = OptionalInt.class,
			args = {
				LevelHeightAccessor.class,
				RandomState.class,
				int.class,
				int.class,
				MutableObject.class,
				Predicate.class
			}
		),
		expect = 2,
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void iterateNoiseColumn_restore(LevelHeightAccessor level, RandomState rand, int x, int z, @Nullable MutableObject<NoiseColumn> column, @Nullable Predicate<BlockState> filter, CallbackInfoReturnable<OptionalInt> callback, NoiseSettings noiseSettings, int i, int j, int k, int l, BlockState[] ablockstate, int i1, int j1, int k1, int l1, int i2, int j2, int k2, double d0, double d1, NoiseChunk noisechunk) {
		if((Object) this instanceof RTFChunkGenerator tf) {
			if(noisechunk instanceof TerrainDataHolder holder) {
//				tf.getTerrainGenerator().restore(holder.getTerrainData());
			}
		}
	}
	
	@Inject(
		at = @At("RETURN"),
		target = @Desc(
			value = "createNoiseChunk",
			ret = NoiseChunk.class,
			args = {
				ChunkAccess.class,
				StructureManager.class,
				Blender.class,
				RandomState.class
			}
		)
	)
	private void createNoiseChunk(ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState rand, CallbackInfoReturnable<NoiseChunk> callback) {
		if((Object) this instanceof RTFChunkGenerator tf) {
			NoiseChunk ret = callback.getReturnValue();
			if(ret != null && ret instanceof TerrainDataHolder holder && chunk instanceof TerrainDataHolder target) {
				holder.setTerrainData(target.getTerrainData());
			}
		}
	}
}
