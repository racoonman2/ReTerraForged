package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public record RegionCell(Domain warp, float frequency) implements Noise {
	public static final Codec<RegionCell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("warp").forGetter(RegionCell::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(RegionCell::frequency)
	).apply(instance, RegionCell::new));
	
	@Override
	public Codec<RegionCell> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
	    float ox = this.warp.getOffsetX(x, y, seed);
        float oz = this.warp.getOffsetY(x, y, seed);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        int cellX = 0;
        int cellY = 0;
        int xi = NoiseUtil.floor(px);
        int yi = NoiseUtil.floor(py);
        float edgeDistance = Float.MAX_VALUE;
        DistanceFunction dist = DistanceFunction.NATURAL;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xi + dx;
                int cy = yi + dy;
                Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float vecX = cx + vec.x() * 0.7F;
                float vecY = cy + vec.y() * 0.7F;
                float distance = dist.apply(vecX - px, vecY - py);
                if (distance < edgeDistance) {
                    edgeDistance = distance;
                    cellX = cx;
                    cellY = cy;
                }
            }
        }
        return cellValue(cellX, cellY, seed);
    }

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}
	
	private static float cellValue(int cellX, int cellY, int seed) {
		float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
		return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
	}
}
