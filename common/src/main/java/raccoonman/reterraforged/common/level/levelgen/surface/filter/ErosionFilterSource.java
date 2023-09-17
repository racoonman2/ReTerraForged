package raccoonman.reterraforged.common.level.levelgen.surface.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
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
			return new Filter(ctx, extension.seedAndCache(this.height.value(), ctx.noiseChunk));
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
			double height = this.sampleHeight(worldX, worldZ);
			double steepness = this.sampleGradient(worldX, worldZ) + (this.rand.compute(worldX, worldZ, 4) * (3F / 255F));
            BlockState material = this.getMaterial(chunkLocalX, chunkLocalZ, (float) height, (float) steepness, Blocks.GRASS.defaultBlockState(), Blocks.STONE.defaultBlockState());
            if (material != Blocks.GRASS.defaultBlockState()) {
                if (material.is(BlockTags.BASE_STONE_OVERWORLD)) {
                	erodeRock(column, chunkLocalX, y, chunkLocalZ);
                    return;
                } else {
                	fillDownSolid(column, chunkLocalX, chunkLocalZ, y, y - 4, material);
                }
            }
//            placeScree(this.context.chunk, context, chunkLocalX, y, chunkLocalZ);
		}
		
		private static int fillDownSolid(BlockColumn column, int x, int z, int from, int to, BlockState state) {
			for (int dy = from; dy > to; dy--) {
				replaceSolid(column, dy, state);
			}
			return to;
		}
		
		private static void replaceSolid(BlockColumn column, int y, BlockState state) {
            if (column.getBlock(y).isAir()) {
                return;
            }
            column.setBlock(y, state);
		}
		
		private static void erodeRock(BlockColumn column, int dx, int y, int dz) {
	        int depth = 32;
	        BlockState material = Blocks.GRAVEL.defaultBlockState();
	        // find the uppermost layer of rock & record it's depth
	        for (int dy = 3; dy < 32; dy++) {
	            BlockState state = column.getBlock(y - dy);
	            if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
	                material = state;
	                depth = dy + 1;
	                break;
	            }
	        }

	        // fill downwards to the first rock layer
	        for (int dy = 0; dy < depth; dy++) {
	        	replaceSolid(column, y - dy, material);
	        }
	    }

//	    protected void placeScree(BlockColumn chunk, DecoratorContext context, int x, int y, int z) {
//	        float steepness = context.cell.gradient + context.climate.getRand().getValue(seed2, (float) x, (float) z) * SLOPE_MODIFIER;
//	        if (steepness < SCREE_STEEPNESS) {
//	            return;
//	        }
//
//	        float sediment = context.cell.sediment * SEDIMENT_MODIFIER;
//	        float noise = context.climate.getRand().getValue(seed3, (float) x, (float) z) * SEDIMENT_NOISE;
//	        if (sediment + noise > SCREE_VALUE) {
//	            fillDownSolid(context, chunk, x, z, y, y - 2, States.GRAVEL.get());
//	        }
//	    }

	    public static final float ROCK_STEEPNESS = 0.65F;
	    private static final float DIRT_STEEPNESS = 0.475F;
	    private static final float SCREE_STEEPNESS = 0.4F;
	    public static final float HEIGHT_MODIFIER = 6F / 255F;
	    public static final float SLOPE_MODIFIER = 3F / 255F;
	    private static final int ROCK_VAR = 30;
	    private static final int ROCK_MIN = 300;

	    private static final int DIRT_VAR = 40;
	    private static final int DIRT_MIN = 95;
	    private static final Noise VARIANCE = Source.perlin(0, 100, 1);
	    private static float getNoise(float x, float z, int seed, float scale, float bias) {
	        return (VARIANCE.compute(x, z, seed) * scale) + bias;
	    }

	    private static float getNoise(float x, float z, int seed, int scale, int bias) {
	        return getNoise(x, z, seed, scale / 255F, bias / 255F);
	    }
	    
	    private static final Rand RAND = new Rand(1.0F);
	    private BlockState getMaterial(float x, float z, float heightValue, float steepnessValue, BlockState top, BlockState middle) {
	        float height = heightValue + RAND.compute(x, z, 4) * HEIGHT_MODIFIER;
	        float steepness = steepnessValue + RAND.compute(x, z, 7) * SLOPE_MODIFIER;

	        if (steepness > ROCK_STEEPNESS || height > getNoise(x, z, 4, ROCK_VAR, ROCK_MIN)) {
	            return rock(middle);
	        }

	        if (steepness > DIRT_STEEPNESS && height > getNoise(x, z, 7, DIRT_VAR, DIRT_MIN)) {
	            return ground(top);
	        }

	        return top;
	    }

	    private static BlockState rock(BlockState state) {
	        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
	            return state;
	        }
	        return Blocks.STONE.defaultBlockState();
	    }

	    private static BlockState ground(BlockState state) {
	        if (state.is(Blocks.GRASS)) {
	            return Blocks.COARSE_DIRT.defaultBlockState();
	        }
	        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
	        	return Blocks.GRAVEL.defaultBlockState();
	        }
	        if (state.is(BlockTags.DIRT)) {
	            return state;
	        }
	        if (state.is(BlockTags.SAND)) {
	            if (state.getBlock() == Blocks.SAND) {
	            	return Blocks.SMOOTH_SANDSTONE.defaultBlockState();
	            }
	            if (state.getBlock() == Blocks.RED_SAND) {
	                return Blocks.SMOOTH_RED_SANDSTONE.defaultBlockState();
	            }
	        }
	        return Blocks.COARSE_DIRT.defaultBlockState();
	    }
		
		private double sampleHeight(int x, int z) {
			this.functionContext.blockX = x;
			this.functionContext.blockZ = z;
			return this.height.compute(this.functionContext);
		}
		
		// TODO: make this a flat cached density function
		private double sampleGradient(int cx, int cz) {
			this.functionContext.blockX = cx;
			this.functionContext.blockZ = cz;
			double totalHeightDif = 0.0D;
	        double height = this.height.compute(this.functionContext);
	        for (int dz = -1; dz <= 2; ++dz) {
	            for (int dx = -1; dx <= 2; ++dx) {
	                if (dx != 0 || dz != 0) {
	                    int x = cx + dx;
	                    int z = cz + dz;
	                    this.functionContext.blockX = x;
	                    this.functionContext.blockZ = z;
	                    double neighborHeight = this.height.compute(this.functionContext);
	                    neighborHeight = Math.max(neighborHeight, 63.0F / 256.0F);
	                    totalHeightDif += Math.abs(height - neighborHeight) / 1;
	                }
	            }
	        }
	        return Math.min(1.0D, totalHeightDif * 10);
		}
	}
}
