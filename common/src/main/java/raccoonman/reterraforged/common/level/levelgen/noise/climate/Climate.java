package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;

public record Climate(Noise source, DistanceFunction distance) implements Noise {
	public static final Codec<Climate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(Climate::source),
		DistanceFunction.CODEC.fieldOf("distance").forGetter(Climate::distance)
	).apply(instance, Climate::new));

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Climate(this.source.mapAll(visitor), this.distance));
	}
	
	@Override
	public Codec<Climate> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
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
                float distance = this.distance.apply(cxf - x, cyf - y);
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
