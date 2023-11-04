package raccoonman.reterraforged.common.level.levelgen.surface.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Rand;

public record ErosionRule(Holder<DensityFunction> height, Holder<DensityFunction> steepness, Holder<DensityFunction> sediment, TagKey<Block> erodible, int radius, float scaler) implements SurfaceRules.RuleSource {
	public static final Codec<ErosionRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("height").forGetter(ErosionRule::height),
		DensityFunction.CODEC.fieldOf("steepness").forGetter(ErosionRule::steepness),
		DensityFunction.CODEC.fieldOf("sediment").forGetter(ErosionRule::sediment),
		TagKey.hashedCodec(Registries.BLOCK).fieldOf("erodible").forGetter(ErosionRule::erodible),
		Codec.INT.fieldOf("radius").forGetter(ErosionRule::radius),
		Codec.FLOAT.fieldOf("scaler").forGetter(ErosionRule::scaler)
	).apply(instance, ErosionRule::new));
	
	@Override
	public SurfaceRules.SurfaceRule apply(Context ctx) {
		if((Object) ctx instanceof ContextExtension contextExt) {
			Rule rule = new Rule(wrap(ctx, this.height.value()), wrap(ctx, this.steepness.value()), wrap(ctx, this.sediment.value()));
			contextExt.setErosionRule(rule);
			return rule;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<ErosionRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}

	private static DensityFunction wrap(Context ctx, DensityFunction function) {
		if((Object) ctx.randomState instanceof RandomStateExtension randomStateExt) {
			return ctx.noiseChunk.wrap(randomStateExt.wrap(function));
		} else {
			throw new IllegalStateException();
		}
	}
	
	public class Rule implements SurfaceRules.SurfaceRule {
	    private static final Noise VARIANCE = Source.perlin(0, 100, 1);
	    public static final Noise RANDOM = new Rand(1.0F);
	    public static final float HEIGHT_MODIFIER = 6F / 255F;
	    public static final float SLOPE_MODIFIER = 3F / 255F;
	    public static final float ROCK_STEEPNESS = 0.65F;
	    public static final float DIRT_STEEPNESS = 0.475F;
	    public static final int DIRT_VAR = 40;
	    public static final int DIRT_MIN = 95;
	    private static final float SCREE_STEEPNESS = 0.4F;
	    private static final float SCREE_VALUE = 0.55F;
	    private static final float SEDIMENT_MODIFIER = 256;
	    private static final float SEDIMENT_NOISE = 3F / 255F;
		private DensityFunction height;
		private DensityFunction steepness;
		private DensityFunction sediment;
		private MutableFunctionContext functionContext;
		
		public Rule(DensityFunction height, DensityFunction steepness, DensityFunction sediment) {
			this.height = height;
			this.steepness = steepness;
			this.sediment = sediment;
			this.functionContext = new MutableFunctionContext();
		}

		@Override
		public BlockState tryApply(int x, int y, int z) {
			return null;
		}
		
		//TODO erode beach sand
		//TODO add randomness to the height & steepness value
		public void applyExtension(int blockX, int blockZ, int surfaceY, BlockColumn column) {
			FunctionContext ctx = this.functionContext.at(blockX, surfaceY, blockZ);
	        double height = this.height.compute(ctx) + RANDOM.compute(blockX, blockZ, 0) * HEIGHT_MODIFIER;
	        double gradient = this.steepness.compute(ctx);
	        double steepness = gradient + RANDOM.compute(blockX, blockZ, 2) * SLOPE_MODIFIER;
			double sediment = this.sediment.compute(ctx) ;
	        BlockState surface = column.getBlock(surfaceY);
	        if(surface.is(ErosionRule.this.erodible)) {
	        	BlockState filler = surface;
	    	        
	        	if(steepness > ROCK_STEEPNESS || height > getNoise(blockX, blockZ, 213513, 30, 140)) {
		        	if(surface.is(BlockTags.BASE_STONE_OVERWORLD)) {
		        		filler = surface;
		        	} else {
		        		filler = Blocks.STONE.defaultBlockState();
		        	}
	        	} else if(steepness > DIRT_STEEPNESS && height > getNoise(blockX, blockZ, 983530, DIRT_VAR, DIRT_MIN)) {
	        		filler = Blocks.COARSE_DIRT.defaultBlockState();
	        	}
	        		
	        	if(filler != surface) {
	        		if(filler.is(BlockTags.BASE_STONE_OVERWORLD)) { 
	        			erodeRock(column, surfaceY);
	        			return;
	        		} else {
	        			for (int dy = surfaceY; dy > surfaceY - 4; dy--) {;
	        				replaceSolid(column, dy, filler);
	        			}
	        		}
	        	}
	        	
	        	placeScree(column, gradient, sediment, blockX, surfaceY, blockZ);
	        }
		}

	    protected static void erodeRock(BlockColumn column, int y) {
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

	    static void replaceSolid(BlockColumn chunk, int y, BlockState state) {
	        if (chunk.getBlock(y).isAir()) {
	            return;
	        }
	        chunk.setBlock(y, state);
	    }
	    
	    static void placeScree(BlockColumn column, double steepnessNoise, double sedimentNoise, int x, int y, int z) {
	    	double steepness = steepnessNoise + RANDOM.compute((float) x, (float) z, 95) * SLOPE_MODIFIER;
	        if (steepness < SCREE_STEEPNESS) {
	            return;
	        }

	        double sediment = sedimentNoise * SEDIMENT_MODIFIER;
	        double noise = RANDOM.compute((float) x, (float) z, 2) * SEDIMENT_NOISE;
	        if (sediment + noise > SCREE_VALUE) {
	        	fillDownSolid(column, y, y - 2, Blocks.GRAVEL.defaultBlockState());
	        }
	    }

	    static int fillDownSolid(BlockColumn column, int from, int to, BlockState state) {
	        for (int dy = from; dy > to; dy--) { ;
	            replaceSolid(column, dy, state);
	        }
	        return to;
	    }
	    
	    static float getNoise(float x, float z, int seed, float scale, float bias) {
	        return (VARIANCE.compute(x, z, seed) * scale) + bias;
	    }

	    public static float getNoise(float x, float z, int seed, int scale, int bias) {
	        return getNoise(x, z, seed, scale / 255F, bias / 255F);
	    }
	}
}
