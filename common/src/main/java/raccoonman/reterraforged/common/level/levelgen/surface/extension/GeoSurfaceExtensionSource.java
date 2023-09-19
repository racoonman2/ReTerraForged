package raccoonman.reterraforged.common.level.levelgen.surface.extension;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.asm.extensions.SurfaceSystemExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;
import raccoonman.reterraforged.common.level.levelgen.geology.Geology;
import raccoonman.reterraforged.common.level.levelgen.geology.Strata;

public record GeoSurfaceExtensionSource(List<Strata> strata, Holder<DensityFunction> height, Holder<DensityFunction> selector) implements SurfaceExtensionSource {
	public static final Codec<GeoSurfaceExtensionSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Strata.CODEC.listOf().fieldOf("strata").forGetter(GeoSurfaceExtensionSource::strata),
		DensityFunction.CODEC.fieldOf("height").forGetter(GeoSurfaceExtensionSource::height),
		DensityFunction.CODEC.fieldOf("selector").forGetter(GeoSurfaceExtensionSource::selector)
	).apply(instance, GeoSurfaceExtensionSource::new));
	
	@Override
	public Extension apply(Context surfaceContext) {
		if((Object) surfaceContext.randomState instanceof RandomStateExtension randomStateExt) {
			return new Extension(surfaceContext, this.strata, randomStateExt.cache(this.height.value(), surfaceContext.noiseChunk), randomStateExt.cache(this.selector.value(), surfaceContext.noiseChunk));
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public Codec<GeoSurfaceExtensionSource> codec() {
		return CODEC;
	}
	
	private record Extension(Context surfaceContext, List<Strata> strata, DensityFunction height, DensityFunction selector, Geology.Buffer depthBuffer, MutableFunctionContext functionContext) implements SurfaceExtension {

		public Extension(Context surfaceContext, List<Strata> strata, DensityFunction height, DensityFunction selector) {
			this(surfaceContext, strata, height, selector, new Geology.Buffer(), new MutableFunctionContext());
		}
		
		@Override
		public void apply(BlockColumn column) {
			if(this.surfaceContext.system instanceof SurfaceSystemExtension	surfaceSystemExt) {
				// terrain noise height doesnt seem to match the heightmap??
				// this is pretty bad 
				// TODO investigate this
				ChunkPos chunkPos = this.surfaceContext.chunk.getPos();
				int chunkLocalX = chunkPos.getBlockX(this.surfaceContext.blockX);
				int chunkLocalZ = chunkPos.getBlockZ(this.surfaceContext.blockZ);
				int y = this.surfaceContext.chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkLocalX, chunkLocalZ) - 1;
				Strata strata = this.getStrata(this.surfaceContext.blockX, y, this.surfaceContext.blockZ);
				Geology geology = surfaceSystemExt.getOrCreateGeology(strata, this.surfaceContext.randomState);
				geology.apply(column, this.surfaceContext.blockX, this.surfaceContext.blockZ, y, this.depthBuffer);
			} else {
				throw new IllegalStateException();
			}
		}

		private int sampleHeight(int x, int z) {
			this.functionContext.blockX = x;
			this.functionContext.blockZ = z;
			return (int) (this.height.compute(this.functionContext) * 256);
		}
		
		private Strata getStrata(int x, int y, int z) {
			this.functionContext.blockX = x;
			this.functionContext.blockY = y;
			this.functionContext.blockZ = z;
			double selector = this.selector.compute(this.functionContext);
			
			int layerCount = this.strata.size();
			int index = (int)(selector * layerCount);
	        index = Math.min(layerCount - 1, index);
	        return this.strata.get(index);
		}
	}
}
