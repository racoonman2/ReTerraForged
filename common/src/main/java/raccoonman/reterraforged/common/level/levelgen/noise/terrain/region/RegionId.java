package raccoonman.reterraforged.common.level.levelgen.noise.terrain.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;

public record RegionId(DistanceFunction distance, float jitter) implements Noise {
	public static final Codec<RegionId> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DistanceFunction.CODEC.fieldOf("distance").forGetter(RegionId::distance),
		Codec.FLOAT.fieldOf("jitter").forGetter(RegionId::jitter)
	).apply(instance, RegionId::new));

	@Override
	public Noise mapAll(Visitor visitor) {
		return this;
	}
	
	@Override
	public Codec<RegionId> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		int cellX = 0;
        int cellY = 0; 
        final int xi = NoiseUtil.floor(x);
        final int yi = NoiseUtil.floor(y);
        float edgeDistance = Float.MAX_VALUE;
        float edgeDistance2 = Float.MAX_VALUE;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xi + dx;
                final int cy = yi + dy;
                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                final float vecX = cx + vec.x() * this.jitter;
                final float vecY = cy + vec.y() * this.jitter;
                final float distance = this.distance.apply(vecX - x, vecY - y);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    cellX = cx;
                    cellY = cy;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
		return cellValue(seed, cellX, cellY);
	}
    
    private static float cellValue(int cellX, int cellY, int seed) {
        final float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }
}
