package raccoonman.reterraforged.common.level.levelgen.test.util;

import java.util.Random;

public record Variance(float min, float range) {
    
    public float apply(float value) {
        return this.min + value * this.range;
    }
    
    public float apply(float value, float scaler) {
        return this.apply(value) * scaler;
    }
    
    public float next(FastRandom random) {
        return this.apply(random.nextFloat());
    }
    
    public float next(Random random) {
        return this.apply(random.nextFloat());
    }
    
    public float next(FastRandom random, float scalar) {
        return this.apply(random.nextFloat(), scalar);
    }
    
    public float next(Random random, float scalar) {
        return this.apply(random.nextFloat(), scalar);
    }
    
    public static Variance min(float min) {
        return new Variance(min, 1.0F - min);
    }
    
    public static Variance range(float range) {
        return new Variance(1.0F - range, range);
    }
    
    public static Variance of(float min, float range) {
        return new Variance(min, range);
    }
}
