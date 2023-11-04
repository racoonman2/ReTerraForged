package raccoonman.reterraforged.common.level.levelgen.test.world.heightmap;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record RegionConfig(int seed, int scale, Noise warpX, Noise warpZ, double warpStrength) {
}
