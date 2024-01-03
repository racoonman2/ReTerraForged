package raccoonman.reterraforged.world.worldgen.feature.chance;

import net.minecraft.util.RandomSource;

public class ChanceContext {
    private int length;
    private float total = 0.0F;
    private float[] buffer;

    public ChanceContext(int length, float total, float[] buffer) {
    	this.length = length;
    	this.total = total;
    	this.buffer = buffer;
    }
    
    void record(int index, float chance) {
    	this.buffer[index] = chance;
    	this.total += chance;
    	this.length++;
    }

    int nextIndex(RandomSource random) {
        if (this.total == 0) {
            return -1;
        }
        float value = 0.0F;
        float chance = this.total * random.nextFloat();
        for (int i = 0; i < this.length; i++) {
            value += this.buffer[i];
            if (value >= chance) {
                return i;
            }
        }
        return -1;
    }
    
    public static ChanceContext make(int size) {
    	float total = 0.0F;
    	int length = 0;
    	float[] buffer = new float[size];
        return new ChanceContext(length, total, buffer);
    }
}
