package raccoonman.reterraforged.common.level.levelgen.surface.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Rand;

public record ErosionFilterSource(Holder<DensityFunction> height) implements FilterSurfaceRuleSource.FilterSource {
	public static final Codec<ErosionFilterSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("height").forGetter(ErosionFilterSource::height)
	).apply(instance, ErosionFilterSource::new));
	
	@Override
	public Codec<ErosionFilterSource> codec() {
		return CODEC;
	}

	@Override
	public Filter apply(Context ctx) {
		if((Object) ctx.randomState instanceof RandomStateExtension extension) {
			DensityFunction seeded = this.height.value().mapAll(extension.visitor());
			DensityFunction cached = ctx.noiseChunk.wrap(seeded);
			return new Filter(ctx, cached);
		} else {
			throw new IllegalStateException();
		}
	}
	
	private record Filter(Context context, DensityFunction height, MutableFunctionContext functionContext, Rand rand) implements FilterSurfaceRuleSource.Filter {

		public Filter(Context ctx, DensityFunction height) {
			this(ctx, height, new MutableFunctionContext(), new Rand(1.0F));
		}
		
		@Override
		public void apply(int worldX, int worldZ, int chunkLocalX, int chunkLocalZ, BlockColumn column) {
			int y = this.context.chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkLocalX, chunkLocalZ);
			double steepness = this.sampleSteepness(worldX, worldZ, chunkLocalX, chunkLocalZ, 1, 10.0F);
			if(steepness > 0.65F) {

				column.setBlock(y, Blocks.STONE.defaultBlockState());	
			}
		}
		
		private double sampleHeight(int x, int z, int clX, int clZ) {
			this.context.noiseChunk.inCellX = 0;
			this.context.noiseChunk.inCellY = 0;
			this.context.noiseChunk.inCellZ = 0;
			this.context.noiseChunk.cellStartBlockX = clX;
			this.context.noiseChunk.cellStartBlockY = 0;
			this.context.noiseChunk.cellStartBlockZ = clZ;
			this.functionContext.blockX = x;
			this.functionContext.blockZ = z;
			return this.height.compute(this.functionContext);
		}
		
		private double sampleSteepness(int cx, int cz, int clx, int clz, int radius, float scaler) {
			double totalHeightDif = 0.0F;
			double height = this.sampleHeight(cx, cz, clx, clz);
	        for (int dz = -1; dz <= 2; ++dz) {
	            for (int dx = -1; dx <= 2; ++dx) {
	                if (dx != 0 || dz != 0) {
	                	int drx = dx * radius;
	                	int drz = dz * radius;
	                    int x = cx + drx;
	                    int z = cz + drz;
	                    double neighborHeight = this.sampleHeight(x, z, clx + drx, clz + drz);
	                    totalHeightDif += Math.abs(height - neighborHeight) / radius;
	                }
	            }
	        }
	        return Math.min(1.0D, totalHeightDif * scaler) + (this.rand.compute(cx, cz, 4) * (3F / 255F));
		}
		
		private static class MutableFunctionContext implements FunctionContext {
			public int blockX, blockY, blockZ;

			@Override
			public int blockX() {
				return this.blockX;
			}

			@Override
			public int blockY() {
				return this.blockY;
			}

			@Override
			public int blockZ() {
				return this.blockZ;
			}
		}
	}
}
