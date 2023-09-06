package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public record MultiContinent(Noise shape, Noise inland, Noise clampMin, Noise clampMax, Noise offsetAlpha, Domain warp, Noise frequency, DistanceFunction distance, EdgeFunction edge) implements Noise {
	public static final Codec<MultiContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("shape").forGetter(MultiContinent::shape),
		Noise.HOLDER_HELPER_CODEC.fieldOf("inland").forGetter(MultiContinent::inland),
		Noise.HOLDER_HELPER_CODEC.fieldOf("clamp_min").forGetter(MultiContinent::clampMin),
		Noise.HOLDER_HELPER_CODEC.fieldOf("clamp_max").forGetter(MultiContinent::clampMax),
		Noise.HOLDER_HELPER_CODEC.fieldOf("offset_alpha").forGetter(MultiContinent::offsetAlpha),
		Domain.CODEC.fieldOf("warp").forGetter(MultiContinent::warp),
		Noise.HOLDER_HELPER_CODEC.fieldOf("frequency").forGetter(MultiContinent::frequency),
		DistanceFunction.CODEC.fieldOf("distance").forGetter(MultiContinent::distance),
		EdgeFunction.CODEC.fieldOf("edge").forGetter(MultiContinent::edge)
	).apply(instance, MultiContinent::new));
	
	@Override
	public Codec<MultiContinent> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        float ox = this.warp.getOffsetX(x, y, seed);
        float oz = this.warp.getOffsetY(x, y, seed);
        float px = x + ox;
        float py = y + oz;
        float frequency = this.frequency.compute(x, y, seed);
        px *= frequency;
        py *= frequency;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0f;
        float edgeDistance2 = 999999.0f;
        float offsetAlpha = this.offsetAlpha.compute(x, y, seed);
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float cxf = cx + vec.x() * offsetAlpha;
                float cyf = cy + vec.y() * offsetAlpha;
                float distance = this.distance.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        float clampMin = this.clampMin.compute(x, y, seed);
        float clampMax = this.clampMax.compute(x, y, seed);
        float continentEdge = this.cellEdgeValue(edgeDistance, edgeDistance2, clampMin, clampMax);
        return continentEdge * this.getShape(x, y, continentEdge, seed);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new MultiContinent(this.shape.mapAll(visitor), this.inland.mapAll(visitor), this.clampMin.mapAll(visitor), this.clampMax.mapAll(visitor), this.offsetAlpha.mapAll(visitor), this.warp, this.frequency.mapAll(visitor), this.distance, this.edge));
	}
    
	private float cellEdgeValue(float distance, float distance2, float clampMin, float clampMax) {
        float value = this.edge.apply(distance, distance2);
        value = 1.0f - NoiseUtil.map(value, this.edge.min(), this.edge.max(), this.edge.range());
        if (value <= clampMin) {
            return 0.0f;
        }
        if (value >= clampMax) {
            return 1.0f;
        }
        return (value - clampMin) / (clampMax - clampMin);
    }
    
	private float getShape(float x, float z, float edgeValue, int seed) {
    	float inland = this.inland.compute(x, z, seed);
        if (edgeValue >= inland) {
            return 1.0f;
        }
        float alpha = edgeValue / inland;
        return this.shape.compute(x, z, seed) * alpha;
    }
}
