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
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.material.Material;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;

public record ErosionRule(Holder<DensityFunction> steepness, TagKey<Block> erodible, int radius, float scaler) implements SurfaceRules.RuleSource {
	public static final Codec<ErosionRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("steepness").forGetter(ErosionRule::steepness),
		TagKey.hashedCodec(Registries.BLOCK).fieldOf("erodible").forGetter(ErosionRule::erodible),
		Codec.INT.fieldOf("radius").forGetter(ErosionRule::radius),
		Codec.FLOAT.fieldOf("scaler").forGetter(ErosionRule::scaler)
	).apply(instance, ErosionRule::new));
	
	@Override
	public SurfaceRules.SurfaceRule apply(Context ctx) {
		if((Object) ctx instanceof ContextExtension contextExt && (Object) ctx.randomState instanceof RandomStateExtension randomStateExt) {
			Rule rule = new Rule(ctx.noiseChunk.wrap(randomStateExt.wrap(this.steepness.value())));
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
	
	public class Rule implements SurfaceRules.SurfaceRule {
		private DensityFunction steepness;
		private MutableFunctionContext functionContext;
		
		public Rule(DensityFunction steepness) {
			this.steepness = steepness;
			this.functionContext = new MutableFunctionContext();
		}

		@Override
		public BlockState tryApply(int x, int y, int z) {
			return null;
		}
		
		private double calculateSteepness(int blockX, int blockY, int blockZ) {
			double heightDelta = 0.0D;
	        double height = this.steepness.compute(this.functionContext.at(blockX, blockY, blockZ));
	        for (int dz = -1; dz <= 2; ++dz) {
	            for (int dx = -1; dx <= 2; ++dx) {
	                if (dx != 0 || dz != 0) {
	                    int x = blockX + dx * ErosionRule.this.radius;
	                    int z = blockZ + dz * ErosionRule.this.radius;
	                    heightDelta += Math.abs(height - this.steepness.compute(this.functionContext.at(x, blockY, z))) / ErosionRule.this.radius;
	                }
	            }
	        }
	        return Math.min(1.0D, heightDelta * ErosionRule.this.scaler);
		}
		
		public void applyExtension(int blockX, int blockZ, int surfaceY, BlockColumn column) {
			double steepness = this.calculateSteepness(blockX, surfaceY, blockZ);
	        BlockState surface = column.getBlock(surfaceY);
	        if(surface.is(ErosionRule.this.erodible)) {
	        	if(steepness > 0.65F) {
	    	        BlockState filler;
	    	        
	        		if(surface.getMaterial() == Material.STONE) {
	        			filler = surface;
	        		} else {
	        			filler = Blocks.STONE.defaultBlockState();
	        		}
	        		
	        		if(filler != surface) {
	        			erodeRock(column, surfaceY);
	        		}
	        	}
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
	}
}
