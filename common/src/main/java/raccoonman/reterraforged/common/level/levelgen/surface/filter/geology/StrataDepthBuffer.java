package raccoonman.reterraforged.common.level.levelgen.surface.filter.geology;

public class StrataDepthBuffer {
    private float sum;
    private float[] buffer;
    
    public void init(final int size) {
        this.sum = 0.0F;
        if (this.buffer == null || this.buffer.length < size) {
            this.buffer = new float[size];
        }
    }
    
    public float getDepth(final int index) {
        return this.buffer[index] / this.sum;
    }
    
    public void set(final int index, final float value) {
        this.sum += value;
        this.buffer[index] = value;
    }
}