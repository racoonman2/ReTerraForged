package raccoonman.reterraforged.common.level.levelgen.noise.terrain.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public record RegionId(DistanceFunction distance, float jitter, Domain warp, float frequency) implements Noise {
	public static final Codec<RegionId> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DistanceFunction.CODEC.fieldOf("distance").forGetter(RegionId::distance),
		Codec.FLOAT.fieldOf("jitter").forGetter(RegionId::jitter),
		Domain.CODEC.fieldOf("warp").forGetter(RegionId::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(RegionId::frequency)
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
        final float ox = this.warp.getOffsetX(x, y, 0);
        final float oz = this.warp.getOffsetY(x, y, 0);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
		int cellX = 0;
        int cellY = 0; 
        int xi = NoiseUtil.floor(px);
        int yi = NoiseUtil.floor(py);
        float edgeDistance = Float.MAX_VALUE;
        float edgeDistance2 = Float.MAX_VALUE;
        DistanceFunction dist = DistanceFunction.NATURAL;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xi + dx;
                final int cy = yi + dy;
                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                final float vecX = cx + vec.x() * this.jitter;
                final float vecY = cy + vec.y() * this.jitter;
                final float distance = dist.apply(vecX - px, vecY - py);
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
