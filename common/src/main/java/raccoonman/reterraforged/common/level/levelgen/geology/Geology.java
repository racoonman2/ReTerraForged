package raccoonman.reterraforged.common.level.levelgen.geology;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record Geology(List<Layer> layers, TagKey<Block> replaceable) {

	public Geology {
		layers = ImmutableList.copyOf(layers);
	}
	
	public BlockState getMaterial(int y, int surfaceY, Geology.Buffer buffer) {
		BlockState state = null;
		int lastHeight = Integer.MIN_VALUE;
        for (int i = 0; i < this.layers.size(); ++i) {
            float depth = buffer.getDepth(i);
            int height = NoiseUtil.round(depth * surfaceY);
            BlockState value = this.layers.get(i).material();
            
            if (height < y && height > lastHeight) {
                state = value;
                lastHeight = height;
            }
        }
        return state;
	}
	
	public record Layer(BlockState material, Noise depth) {
	}
		
	public static class Buffer {
	    private float sum;
	    private float[] buffer;

	    public void init(Geology geology, int x, int z) {
	    	List<Layer> layers = geology.layers();
	    	int count = layers.size();
	        if (this.buffer == null || this.buffer.length < count) {
	            this.buffer = new float[count];
	        }
	        
	        float sum = 0.0F;
	        for (int i = 0; i < count; ++i) {
	        	// depth noise should be shifted by the level seed so its okay to pass 0 here
	        	// i still don't like it though
	        	
	        	//TODO depth should be a density function
	            float depth = (float) layers.get(i).depth().compute(x, z, 0);
		        sum += depth;
		        this.buffer[i] = depth;
	        }
	        this.sum = sum;
	    }
	    
	    public float getDepth(final int index) {
	        return this.buffer[index] / this.sum;
	    }
	}
}
