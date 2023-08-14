/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.settings;

import raccoonman.reterraforged.common.noise.Noise;

public class RegionConfig {
    public final int seed;
    public final int scale;
    public final Noise warpX;
    public final Noise warpZ;
    public final double warpStrength;

    public RegionConfig(int seed, int scale, Noise warpX, Noise warpZ, double warpStrength) {
        this.seed = seed;
        this.scale = scale;
        this.warpX = warpX;
        this.warpZ = warpZ;
        this.warpStrength = warpStrength;
    }
}

