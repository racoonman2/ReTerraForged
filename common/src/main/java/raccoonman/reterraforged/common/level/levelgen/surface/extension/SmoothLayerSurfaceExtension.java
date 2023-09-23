package raccoonman.reterraforged.common.level.levelgen.surface.extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.SmoothLayerSurfaceExtension.Layer;

public record SmoothLayerSurfaceExtension(Holder<DensityFunction> height, int yScale, List<Layer> layers) implements SurfaceExtensionSource {
	public static final Codec<SmoothLayerSurfaceExtension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("height").forGetter(SmoothLayerSurfaceExtension::height),
		Codec.INT.fieldOf("y_scale").forGetter(SmoothLayerSurfaceExtension::yScale),
		Layer.CODEC.listOf().fieldOf("layers").forGetter(SmoothLayerSurfaceExtension::layers)
	).apply(instance, SmoothLayerSurfaceExtension::new));
	
	public SmoothLayerSurfaceExtension {
		layers = ImmutableList.copyOf(layers);
	}
	
	@Override
	public Extension apply(Context surfaceContext) {
		if((Object) surfaceContext.randomState instanceof RandomStateExtension randomStateExt) {		
			DensityFunction cachedHeight = randomStateExt.cache(this.height.value(), surfaceContext.noiseChunk);
			Map<Block, Extension.Layer> layers = new HashMap<>();
			for(Layer layer : this.layers) {
				Block full = layer.full();
				@SuppressWarnings("unchecked")
				Property<Integer> property = (Property<Integer>) full.getStateDefinition().getProperty(layer.sliceProperty());
				layers.put(full, new Extension.Layer(full, layer.slice(), property, min(property), max(property), layer.surfaceCorrection().apply(surfaceContext)));
			}
			return new Extension(surfaceContext, cachedHeight, this.yScale, layers);
		} else {
			throw new IllegalStateException();
		}
	}
	
	@Override
	public Codec<SmoothLayerSurfaceExtension> codec() {
		return CODEC;
	}
	
	public record Layer(Block full, Block slice, String sliceProperty, SurfaceRules.RuleSource surfaceCorrection) {
		public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("full").forGetter(Layer::full),
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("slice").forGetter(Layer::slice),
			Codec.STRING.fieldOf("slice_property").forGetter(Layer::sliceProperty),
			SurfaceRules.RuleSource.CODEC.fieldOf("surface_correction").forGetter(Layer::surfaceCorrection)
		).apply(instance, Layer::new));
		
	}

    private static int min(Property<Integer> property) {
        return property.getPossibleValues().stream().min(Integer::compareTo).orElse(0);
    }

    private static int max(Property<Integer> property) {
        return property.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
    }
	
	private record Extension(Context surfaceContext, DensityFunction height, int yScale, Map<Block, Extension.Layer> layers, MutableFunctionContext functionContext) implements SurfaceExtension {

		public Extension(Context surfaceContext, DensityFunction height, int yScale, Map<Block, Extension.Layer> layers) {
			this(surfaceContext, height, yScale, layers, new MutableFunctionContext());
		}
		
		@Override
		public void apply(BlockColumn column) {
			ChunkPos chunkPos = this.surfaceContext.chunk.getPos();
			int chunkLocalX = chunkPos.getBlockX(this.surfaceContext.blockX);
			int chunkLocalZ = chunkPos.getBlockZ(this.surfaceContext.blockZ);
            int y = this.surfaceContext.chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, chunkLocalX, chunkLocalZ);
			int y2 = y + 1;
            
			BlockState state = column.getBlock(y2);
            if (state.isAir()) {
            	y2 = y;
                state = column.getBlock(y);
                if (state.isAir()) {
                    return;
                }
            }
            
            Layer layer = this.layers.get(state.getBlock());
            if(layer == null) {
            	return;
            }
            
            this.setLayer(column, y2, layer, 0F);
		}

	    private void setLayer(BlockColumn column, int y, Layer layer, float min) {
			float height = (float) (this.sample(this.height, this.surfaceContext.blockX, 0, this.surfaceContext.blockZ) * this.yScale);
	        float depth = Layer.getDepth(height);
	        if (depth > min) {
	            int level = layer.getLevel(depth);
	            BlockState material = layer.getState(level);
	            if (material == Blocks.AIR.defaultBlockState()) {
	                return;
	            }
	            column.setBlock(y, material);

//	            this.applySurfaceCorrection(column, y, material, level);
	        }
	    }

	    private void applySurfaceCorrection(BlockColumn column, int y, BlockState material, int level, Layer layer) {
	        if (level > 1 && material.is(Blocks.SNOW)) {
	        	int yBelow = y - 1;
//	            this.surfaceContext.updateY(yBelow);
	        	
	        	BlockState below = column.getBlock(yBelow);
	            
	            
	            if (below.getBlock() instanceof SnowyDirtBlock) {
	                column.setBlock(yBelow, below.setValue(SnowyDirtBlock.SNOWY, true));
	            }
	        }
	    }
	    
		private double sample(DensityFunction function, int x, int y, int z) {
			this.functionContext.blockX = x;
			this.functionContext.blockY = y;
			this.functionContext.blockZ = z;
			return function.compute(this.functionContext);
		}
		
		private record Layer(Block full, Block slice, Property<Integer> property, int min, int max, SurfaceRule surfaceCorrection) {

		    public int getLevel(float depth) {
		        if (depth > 1) {
		            depth = getDepth(depth);
		        } else if (depth < 0) {
		            depth = 0;
		        }
		        return NoiseUtil.round(depth * max);
		    }
			
			public BlockState getState(int level) {
				if(level < this.min) {
					return Blocks.AIR.defaultBlockState();
				} else if (level >= this.max) {
					return this.full.defaultBlockState();
				} else {
					return this.slice.defaultBlockState().setValue(this.property, level);
				}
			}

		    protected static float getDepth(float height) {
		        return height - (int) height;
		    }
		}
	}
}
