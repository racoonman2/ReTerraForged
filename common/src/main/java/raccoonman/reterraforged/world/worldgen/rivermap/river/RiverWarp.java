package raccoonman.reterraforged.world.worldgen.rivermap.river;

import java.util.Random;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Simplex2;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

public class RiverWarp {
    public static final RiverWarp NONE = new RiverWarp(0, 0.0F, 0.0F, 0.0F, 0.0F);
    private int seed;
    private float lower;
    private float upper;
    private float lowerRange;
    private float upperRange;
    private float frequency;
    private float scale;
    
    public RiverWarp(int seed, float lower, float upper, float frequency, float scale) {
        this.seed = seed;
        this.frequency = frequency;
        this.scale = scale;
        this.lower = lower;
        this.upper = upper;
        this.lowerRange = 1.0F / lower;
        this.upperRange = 1.0F / (1.0F - upper);
    }
    
    public RiverWarp createChild(float lower, float upper, float factor, Random random) {
        return new RiverWarp(random.nextInt(), lower, upper, this.frequency * factor, this.scale * factor);
    }
    
    public boolean test(float t) {
        return this != RiverWarp.NONE && t >= 0.0F && t <= 1.0F;
    }
    
    public long getOffset(float x, float z, float t, River river) {
        float alpha1 = this.getWarpAlpha(t);
        float px = x * this.frequency;
        float pz = z * this.frequency;
        float distance = alpha1 * this.scale;
        float noise = Simplex2.sample(px, pz, this.seed);
        float dx = river.normX * noise * distance;
        float dz = river.normZ * noise * distance;
        float alpha2 = this.getWiggleAlpha(t);
        float factor = river.length * 4.0E-4F;
        float wiggleFreq = 8.0f * factor;
        float wiggleDist = NoiseUtil.clamp(alpha2 * 25.0F * factor, 2.0F, 45.0F);
        float rads = noise + t * 6.2831855F * wiggleFreq;
        dx += NoiseUtil.cos(rads) * river.normX * wiggleDist;
        dz += NoiseUtil.sin(rads) * river.normZ * wiggleDist;
        return PosUtil.packf(dx, dz);
    }
    
    private float getWarpAlpha(float t) {
        if (t < 0.0F || t > 1.0F) {
            return 0.0F;
        }
        if (t < this.lower) {
            return t * this.lowerRange;
        }
        if (t > this.upper) {
            return (1.0F - t) * this.upperRange;
        }
        return 1.0F;
    }
    
    private float getWiggleAlpha(float t) {
        return NoiseUtil.map(t, 0.0F, 0.075F, 0.075F);
    }
    
    public static RiverWarp create(float fade, Random random) {
        return create(fade, 1.0F - fade, random);
    }
    
    public static RiverWarp create(float lower, float upper, Random random) {
        float scale = 125.0F + random.nextInt(50);
        float frequency = 5.0E-4F + random.nextFloat() * 5.0E-4F;
        return new RiverWarp(random.nextInt(), lower, upper, frequency, scale);
    }
}
