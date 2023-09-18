package raccoonman.reterraforged.common.level.levelgen.surface.filter;

import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.surface.filter.geology.StrataDepthBuffer;
import raccoonman.reterraforged.common.level.levelgen.surface.filter.geology.StrataGenerator;
import raccoonman.reterraforged.common.level.levelgen.surface.filter.geology.Stratum;

public record StrataExtensionSource(Holder<DensityFunction> selector, StrataGenerator generator, Supplier<List<List<Stratum>>> strata) implements ExtensionRuleSource.ExtensionSource {
	public static final Codec<StrataExtensionSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("selector").forGetter(StrataExtensionSource::selector),
		StrataGenerator.CODEC.fieldOf("generator").forGetter(StrataExtensionSource::generator)
	).apply(instance, StrataExtensionSource::new));
	
	public StrataExtensionSource(Holder<DensityFunction> selector, StrataGenerator generator) {
		this(selector, generator, Suppliers.memoize(generator::generate)); // im not at all a fan of this being a supplier but im yet to come up with a better solution
	}
	
	@Override
	public Extension apply(Context ctx) {
		if((Object) ctx.randomState instanceof RandomStateExtension extension) {
			return new Extension(ctx, new StrataDepthBuffer(), extension.seedAndCache(this.selector.value(), ctx.noiseChunk), this.strata.get().stream().map((strata) -> {
				return strata.stream().map((stratum) -> {
					return stratum.mapAll((function) -> extension.seedAndCache(function, ctx.noiseChunk));
				}).toList();
			}).toList(), new MutableFunctionContext());
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public Codec<StrataExtensionSource> codec() {
		return CODEC;
	}
	
	private record Extension(Context context, StrataDepthBuffer buffer, DensityFunction selector, List<List<Stratum>> strata, MutableFunctionContext functionContext) implements ExtensionRuleSource.Extension {

		@Override
		public void apply(int worldX, int worldZ, int chunkLocalX, int chunkLocalZ, BlockColumn column) {
			this.functionContext.blockX = worldX;
			this.functionContext.blockZ = worldZ;
			
			List<Stratum> strata = this.getStrata();
			int y = this.context.chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkLocalX, chunkLocalZ) - 1;
			this.initBuffer(strata);
	        int py = y;
	        BlockState last = null;
	        for (int i = 0; i < strata.size(); ++i) {
	            float depth = this.buffer.getDepth(i);
	            int height = NoiseUtil.round(depth * y);
	            BlockState value = last = strata.get(i).state();
	            for (int dy = 0; dy < height; ++dy) {
	                if (py <= y) {
	                    column.setBlock(py, value);
	                }
	                if (--py < 0) {
	                    return;
	                }
	            }
	        }
	        if (last != null) {
	            while (py > 0) {
	                column.setBlock(py, last);
	                --py;
	            }
	        }
		}
	    
		private List<Stratum> getStrata() {
	        double noise = this.selector.compute(this.functionContext);
	        int index = (int)(noise * this.strata.size());
	        index = Math.min(this.strata.size() - 1, index);
	        return this.strata.get(index);
	    }
		
	    private void initBuffer(List<Stratum> strata) {
	    	this.buffer.init(strata.size());
	        for (int i = 0; i < strata.size(); ++i) {
	            double depth = strata.get(i).depth().compute(this.functionContext);
	            this.buffer.set(i, (float) depth);
	        }
	    }
	}
}
