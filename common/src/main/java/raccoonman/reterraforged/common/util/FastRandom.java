/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.util;

public class FastRandom {
    private static final long GOLDEN_GAMMA = -7046029254386353131L;
    private long seed;
    private long gamma;

    public FastRandom() {
        this(System.currentTimeMillis(), GOLDEN_GAMMA);
    }

    public FastRandom(long seed) {
        this(seed, GOLDEN_GAMMA);
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
        this.gamma = FastRandom.mixGamma(gamma);
        return this;
    }

    public FastRandom gamma(long gamma) {
        this.gamma = gamma;
        return this;
    }

    public int nextInt() {
        return FastRandom.mix32(this.nextSeed());
    }

    public int nextInt(int bound) {
        int r = FastRandom.mix32(this.nextSeed());
        int m = bound - 1;
        if ((bound & m) == 0) {
            r &= m;
        } else {
            int u = r >>> 1;
            while (u + m - (r = u % bound) < 0) {
                u = FastRandom.mix32(this.nextSeed()) >>> 1;
            }
        }
        return r;
    }

    public float nextFloat() {
        return (float)(FastRandom.mix32(this.nextSeed()) >>> 8) * 5.9604645E-8f;
    }

    public boolean nextBoolean() {
        return FastRandom.mix32(this.nextSeed()) < 0;
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
        int n = Long.bitCount((z = z ^ z >>> 33 | 1L) ^ z >>> 1);
        return n < 24 ? z ^ 0xAAAAAAAAAAAAAAAAL : z;
    }
}

