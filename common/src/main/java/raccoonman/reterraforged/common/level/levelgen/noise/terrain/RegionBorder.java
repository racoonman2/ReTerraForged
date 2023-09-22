package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public record RegionBorder(Domain warp, float frequency, float edgeMin, float edgeMax) implements Noise {
	public static final Codec<RegionBorder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("warp").forGetter(RegionBorder::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(RegionBorder::frequency),
		Codec.FLOAT.fieldOf("edge_min").forGetter(RegionBorder::edgeMin),
		Codec.FLOAT.fieldOf("edge_max").forGetter(RegionBorder::edgeMax)
	).apply(instance, RegionBorder::new));
	
	@Override
	public Codec<RegionBorder> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		Domain warp = this.warp();
        float ox = warp.getOffsetX(x, y, seed);
        float oz = warp.getOffsetY(x, y, seed);
        float px = x + ox;
        float py = y + oz;
        float frequency = this.frequency();
        px *= frequency;
        py *= frequency;
        int xi = NoiseUtil.floor(px);
        int yi = NoiseUtil.floor(py);
        float edgeDistance = Float.MAX_VALUE;
        float edgeDistance2 = Float.MAX_VALUE;
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
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        return this.edgeValue(edgeDistance, edgeDistance2);
    }

	private float edgeValue(float distance, float distance2) {
        EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        float edgeValue = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        edgeValue = NoiseUtil.pow(edgeValue, 1.5F);
        if (edgeValue < this.edgeMin) {
            return 0.0F;
        }
        if (edgeValue > this.edgeMax) {
            return 1.0F;
        }
        return (edgeValue - this.edgeMin) / (this.edgeMax - this.edgeMin);
    }
	
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}
}
