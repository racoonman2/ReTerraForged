package raccoonman.reterraforged.common.level.levelgen.geology;

import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record Geology(List<Layer> layers) {
	
	public void apply(BlockColumn column, int x, int z, int surfaceY, Buffer buffer) {
		buffer.init(this.layers, x, z);
        int py = surfaceY;
        BlockState last = null;
        for (int i = 0; i < this.layers.size(); ++i) {
            float depth = buffer.getDepth(i);
            int height = NoiseUtil.round(depth * surfaceY);
            BlockState value = last = this.layers.get(i).material();
            for (int dy = 0; dy < height; ++dy) {
                if (py <= surfaceY) {
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

	public record Layer(BlockState material, Noise depth) {
	}
		
	public static class Buffer {
	    private float sum;
	    private float[] buffer;

	    public void init(List<Layer> strata, int x, int z) {
	    	int count = strata.size();
	        if (this.buffer == null || this.buffer.length < count) {
	            this.buffer = new float[count];
	        }
	        
	        float sum = 0.0F;
	        for (int i = 0; i < count; ++i) {
	        	// depth noise should be shifted by the level seed so its okay to pass 0 here
	        	// i still don't like it though
	            float depth = (float) strata.get(i).depth().compute(x, z, 0);
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
