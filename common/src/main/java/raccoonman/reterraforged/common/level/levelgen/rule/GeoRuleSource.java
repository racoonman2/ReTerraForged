package raccoonman.reterraforged.common.level.levelgen.rule;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.asm.extensions.SurfaceSystemExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;
import raccoonman.reterraforged.common.level.levelgen.geology.Geology;
import raccoonman.reterraforged.common.level.levelgen.geology.Strata;

//TODO we still need to check that this matches 1.16.5
public record GeoRuleSource(List<Strata> strata, Holder<DensityFunction> selector) implements SurfaceRules.RuleSource {
	public static final Codec<GeoRuleSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Strata.CODEC.listOf().fieldOf("strata").forGetter(GeoRuleSource::strata),
		DensityFunction.CODEC.fieldOf("selector").forGetter(GeoRuleSource::selector)
	).apply(instance, GeoRuleSource::new));
	
	public GeoRuleSource {
		strata = ImmutableList.copyOf(strata);
	}
	
	@Override
	public Rule apply(Context surfaceContext) {
		if((Object) surfaceContext.randomState instanceof RandomStateExtension randomStateExt) {
			return new Rule(surfaceContext, randomStateExt.cache(this.selector.value(), surfaceContext.noiseChunk));
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<GeoRuleSource> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	private class Rule extends CachedXZRule {
		private DensityFunction selector;
		private Geology.Buffer buffer;
		private MutableFunctionContext functionContext;
		private BlockPos.MutableBlockPos pos;
		private Geology geology;
		private int surfaceY;
		
		public Rule(Context surfaceContext, DensityFunction selector) {
			super(surfaceContext);
			this.selector = selector;
			this.buffer = new Geology.Buffer();
			this.functionContext = new MutableFunctionContext();
			this.pos = new BlockPos.MutableBlockPos();
			this.geology = null;
			this.surfaceY = Integer.MIN_VALUE;
		}

		@Override
		void cache(int x, int z) {
			if((Object) this.surfaceContext.system instanceof SurfaceSystemExtension extension) {
				ChunkPos chunkPos = this.surfaceContext.chunk.getPos();
				int chunkLocalX = chunkPos.getBlockX(this.surfaceContext.blockX);
				int chunkLocalZ = chunkPos.getBlockZ(this.surfaceContext.blockZ);
				this.geology = extension.getOrCreateGeology(this.selectStrata(x, 0, z), this.surfaceContext.randomState);
				this.buffer.init(this.geology, x, z);
				this.surfaceY = this.surfaceContext.chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, chunkLocalX, chunkLocalZ) - 1;
			} else {
				throw new IllegalStateException();
			}
		}

		@Override
		BlockState apply(int x, int y, int z) {
			if(y < 0) return null;
			
			ChunkPos chunkPos = this.surfaceContext.chunk.getPos();
			int chunkLocalX = chunkPos.getBlockX(this.surfaceContext.blockX);
			int chunkLocalZ = chunkPos.getBlockZ(this.surfaceContext.blockZ);

	        this.pos.set(chunkLocalX, y, chunkLocalZ);
	        if(!this.surfaceContext.chunk.getBlockState(this.pos).is(this.geology.replaceable())) {
	        	return null;
	        }
	        
	        return this.geology.getMaterial(y, this.surfaceY, this.buffer);
		}

		private Strata selectStrata(int x, int y, int z) {
			this.functionContext.blockX = x;
			this.functionContext.blockY = y;
			this.functionContext.blockZ = z;
			double selector = this.selector.compute(this.functionContext);
			
			int layerCount = GeoRuleSource.this.strata.size();
			int index = (int)(selector * layerCount);
	        index = Math.min(layerCount - 1, index);
	        return GeoRuleSource.this.strata.get(index);
		}
	}
}
