package raccoonman.reterraforged.common.level.levelgen.noise.terrain.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public record RegionEdge(float edgeMin, float edgeMax, EdgeFunction edge, DistanceFunction distance, float jitter, Domain warp, float frequency) implements Noise {
	public static final Codec<RegionEdge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("edge_min").forGetter(RegionEdge::edgeMin),
		Codec.FLOAT.fieldOf("edge_max").forGetter(RegionEdge::edgeMax),
		EdgeFunction.CODEC.fieldOf("edge").forGetter(RegionEdge::edge),
		DistanceFunction.CODEC.fieldOf("distance").forGetter(RegionEdge::distance),
		Codec.FLOAT.fieldOf("jitter").forGetter(RegionEdge::jitter),
		Domain.CODEC.fieldOf("warp").forGetter(RegionEdge::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(RegionEdge::frequency)
	).apply(instance, RegionEdge::new));
	
	@Override
	public Noise mapAll(Visitor visitor) {
		return this;
	}
	
	@Override
	public Codec<RegionEdge> codec() {
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
        final int xi = NoiseUtil.floor(px);
        final int yi = NoiseUtil.floor(py);
        float edgeDistance = Float.MAX_VALUE;
        float edgeDistance2 = Float.MAX_VALUE;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xi + dx;
                final int cy = yi + dy;
                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                final float vecX = cx + vec.x() * this.jitter;
                final float vecY = cy + vec.y() * this.jitter;
                final float distance = this.distance.apply(vecX - px, vecY - py);
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
	
	 private float edgeValue(final float distance, final float distance2) {
		 final float value = this.edge.apply(distance, distance2);
		 float edgeValue = 1.0F - NoiseUtil.map(value, this.edge.min(), this.edge.max(), this.edge.range());
		 edgeValue = NoiseUtil.pow(edgeValue, 1.5F);
		 if (edgeValue < this.edgeMin) {
			 return 0.0F;
		 }
		 if (edgeValue > this.edgeMax) {
			 return 1.0F;
	     }
		 return (edgeValue - this.edgeMin) / (this.edgeMax - this.edgeMin);
	 }
}
