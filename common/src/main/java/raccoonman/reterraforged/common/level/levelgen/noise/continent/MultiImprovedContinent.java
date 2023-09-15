package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public record MultiImprovedContinent(Domain warp, float frequency, DistanceFunction distanceFunc, float jitter, float clampMin, float clampMax, float lerpEnd, Noise shape) implements Noise {
	public static final Codec<MultiImprovedContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("warp").forGetter(MultiImprovedContinent::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(MultiImprovedContinent::frequency),
		DistanceFunction.CODEC.fieldOf("distance_function").forGetter(MultiImprovedContinent::distanceFunc),
		Codec.FLOAT.fieldOf("jitter").forGetter(MultiImprovedContinent::jitter),
		Codec.FLOAT.fieldOf("clamp_min").forGetter(MultiImprovedContinent::clampMin),
		Codec.FLOAT.fieldOf("clamp_max").forGetter(MultiImprovedContinent::clampMax),
		Codec.FLOAT.fieldOf("lerp_end").forGetter(MultiImprovedContinent::lerpEnd),
		Noise.HOLDER_HELPER_CODEC.fieldOf("shape").forGetter(MultiImprovedContinent::shape)
	).apply(instance, MultiImprovedContinent::new));
	
	@Override
	public Codec<MultiImprovedContinent> codec() {
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
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0f;
        float edgeDistance2 = 999999.0f;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float cxf = cx + vec.x() * this.jitter;
                float cyf = cy + vec.y() * this.jitter;
                float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        float edgeValue = this.cellEdgeValue(edgeDistance, edgeDistance2);
        float shapeNoise = this.getShape(x, y, edgeValue, seed);
        return edgeValue * shapeNoise;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new MultiImprovedContinent(this.warp, this.frequency, this.distanceFunc, this.jitter, this.clampMin, this.clampMax, this.lerpEnd, this.shape.mapAll(visitor)));
	}

	private float cellEdgeValue(float distance, float distance2) {
		EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
		float value = edge.apply(distance, distance2);
		value = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
		if (value <= this.clampMin) {
			return 0.0F;
		}
		if (value >= this.clampMax) {
			return 1.0F;
		}
		return (value - this.clampMin) / (this.clampMax - this.clampMin);
	}
	    
	private float getShape(float x, float z, float edgeValue, int seed) {
		if (edgeValue >= this.lerpEnd) {
			return 1.0F;
		}
		float alpha = edgeValue / this.lerpEnd;
		return this.shape.compute(x, z, seed) * alpha;
	}
}
