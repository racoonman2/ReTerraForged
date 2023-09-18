package raccoonman.reterraforged.common.level.levelgen.surface.extension.geology;

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
import raccoonman.reterraforged.common.level.levelgen.surface.extension.ExtensionRuleSource;

public record StrataExtensionSource(Holder<DensityFunction> selector, StrataGenerator generator, Supplier<List<Strata>> strata) implements ExtensionRuleSource.ExtensionSource {
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
			DensityFunction.Visitor visitor = (function) -> {
				return extension.seedAndCache(this.selector.value(), ctx.noiseChunk);
			};
			return new Extension(ctx, new StrataDepthBuffer(), this.selector.value().mapAll(visitor), this.strata.get().stream().map((strata) -> {
				return strata.mapAll(visitor);
			}).toList(), new MutableFunctionContext());
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public Codec<StrataExtensionSource> codec() {
		return CODEC;
	}
	
	private record Extension(Context surfaceContext, StrataDepthBuffer buffer, DensityFunction selector, List<Strata> strata, MutableFunctionContext functionContext) implements ExtensionRuleSource.Extension {

		@Override
		public void apply(int worldX, int worldZ, int chunkLocalX, int chunkLocalZ, BlockColumn column) {
			Strata strata = this.getStrataAt(worldX, worldZ);
	        List<Stratum> stratum = strata.stratum();
			int y = this.surfaceContext.chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkLocalX, chunkLocalZ) - 1;
			this.buffer.init(strata, worldX, worldZ);
	        int py = y;
	        BlockState last = null;
	        for (int i = 0; i < stratum.size(); ++i) {
	            float depth = this.buffer.getDepth(i);
	            int height = NoiseUtil.round(depth * y);
	            BlockState value = last = stratum.get(i).state();
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
	    
		private Strata getStrataAt(int x, int z) {
			this.functionContext.blockX = x;
			this.functionContext.blockZ = z;
	        double noise = this.selector.compute(this.functionContext);
	        int index = (int)(noise * this.strata.size());
	        index = Math.min(this.strata.size() - 1, index);
	        return this.strata.get(index);
	    }
	}
}
