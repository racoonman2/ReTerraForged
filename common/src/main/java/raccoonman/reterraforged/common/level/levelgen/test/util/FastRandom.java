package raccoonman.reterraforged.common.level.levelgen.test.util;

public class FastRandom {
    private long seed;
    private long gamma;
    
    public FastRandom() {
        this(System.currentTimeMillis(), -7046029254386353131L);
    }
    
    public FastRandom(long seed) {
        this(seed, -7046029254386353131L);
    }
    
    public FastRandom(long seed, long gamma) {
        this.seed = seed;
        this.gamma = gamma;
    }
    
    public FastRandom seed(long seed) {
        this.seed = seed;
        return this;
    }
    
    public FastRandom seed(long seed, long gamma) {
        this.seed = seed;
        this.gamma = mixGamma(gamma);
        return this;
    }
    
    public FastRandom gamma(long gamma) {
        this.gamma = gamma;
        return this;
    }
    
    public int nextInt() {
        return mix32(this.nextSeed());
    }
    
    public int nextInt(int bound) {
        int r = mix32(this.nextSeed());
        int m = bound - 1;
        if ((bound & m) == 0x0) {
            r &= m;
        } else {
            for (int u = r >>> 1; u + m - (r = u % bound) < 0; u = mix32(this.nextSeed()) >>> 1) {}
        }
        return r;
    }
    
    public float nextFloat() {
        return (mix32(this.nextSeed()) >>> 8) * 5.9604645E-8f;
    }
    
    public boolean nextBoolean() {
        return mix32(this.nextSeed()) < 0;
    }
    
    private long nextSeed() {
        return this.seed += this.gamma;
    }
    
    private static int mix32(long z) {
        z = (z ^ z >>> 33) * 7109453100751455733L;
        return (int)((z ^ z >>> 28) * -3808689974395783757L >>> 32);
    }
    
    private static long mixGamma(long z) {
        z = (z ^ z >>> 33) * -49064778989728563L;
        z = (z ^ z >>> 33) * -4265267296055464877L;
        z = ((z ^ z >>> 33) | 0x1L);
        int n = Long.bitCount(z ^ z >>> 1);
        return (n < 24) ? (z ^ 0xAAAAAAAAAAAAAAAAL) : z;
    }
}
