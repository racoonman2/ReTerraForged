package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;

public record ClimateRegionEdge(float frequency, Noise warpX, Noise warpY, float warpStrength) implements Noise {
	public static final Codec<ClimateRegionEdge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(ClimateRegionEdge::frequency),
		Noise.HOLDER_HELPER_CODEC.fieldOf("warp_x").forGetter(ClimateRegionEdge::warpX),
		Noise.HOLDER_HELPER_CODEC.fieldOf("warp_y").forGetter(ClimateRegionEdge::warpY),
		Codec.FLOAT.fieldOf("warp_strength").forGetter(ClimateRegionEdge::warpStrength)
	).apply(instance, ClimateRegionEdge::new));

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}
	
	@Override
	public Codec<ClimateRegionEdge> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        float ox = this.warpX.compute(x, y, seed) * this.warpStrength;
        float oz = this.warpY.compute(x, y, seed) * this.warpStrength;
        x += ox;
        y += oz;
        x *= this.frequency;
        y *= this.frequency;
        int xr = NoiseUtil.floor(x);
        int yr = NoiseUtil.floor(y);
        float edgeDistance = 999999.0F;
        float edgeDistance2 = 999999.0F;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f cell = NoiseUtil.cell(seed, cx, cy);
                float cxf = cx + cell.x();
                float cyf = cy + cell.y();
                float distance = DistanceFunction.EUCLIDEAN.apply(cxf - x, cyf - y);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
       	return edgeValue(edgeDistance, edgeDistance2);
	}
    
    private static float edgeValue(float distance, float distance2) {
        EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        value = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        return value;
    }
}
