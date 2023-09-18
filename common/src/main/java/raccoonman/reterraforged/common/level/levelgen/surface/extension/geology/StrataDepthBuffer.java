package raccoonman.reterraforged.common.level.levelgen.surface.extension.geology;

import java.util.List;

import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;

class StrataDepthBuffer {
    private float sum;
    private float[] buffer;
    private MutableFunctionContext ctx;
    
    public void init(Strata strata, int x, int z) {
        List<Stratum> stratum = strata.stratum();
    	int count = stratum.size();
    	
        this.sum = 0.0F;
        
        if (this.buffer == null || this.buffer.length < count) {
            this.buffer = new float[count];
        }
        
        this.ctx.blockX = x;
        this.ctx.blockZ = z;
        for (int i = 0; i < count; ++i) {
            double depth = stratum.get(i).depth().compute(this.ctx);
            this.set(i, (float) depth);
        }
    }
    
    public float getDepth(int index) {
        return this.buffer[index] / this.sum;
    }
    
    public void set(int index, float value) {
        this.sum += value;
        this.buffer[index] = value;
    }
}