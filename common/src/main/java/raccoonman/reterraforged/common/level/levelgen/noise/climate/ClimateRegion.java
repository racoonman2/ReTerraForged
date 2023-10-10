package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;

public record ClimateRegion(Noise source, float frequency, Noise warpX, Noise warpY, float warpStrength) implements Noise {
	public static final Codec<ClimateRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(ClimateRegion::source),
		Codec.FLOAT.fieldOf("frequency").forGetter(ClimateRegion::frequency),
		Noise.HOLDER_HELPER_CODEC.fieldOf("warp_x").forGetter(ClimateRegion::warpX),
		Noise.HOLDER_HELPER_CODEC.fieldOf("warp_y").forGetter(ClimateRegion::warpY),
		Codec.FLOAT.fieldOf("warp_strength").forGetter(ClimateRegion::warpStrength)
	).apply(instance, ClimateRegion::new));

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new ClimateRegion(this.source.mapAll(visitor), this.frequency, this.warpX.mapAll(visitor), this.warpY.mapAll(visitor), this.warpStrength));
	}
	
	@Override
	public Codec<ClimateRegion> codec() {
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
        float centerX = x;
        float centerY = y;
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
                    centerX = cxf;
                    centerY = cyf;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        return this.source.compute(centerX, centerY, seed);
	}
}
