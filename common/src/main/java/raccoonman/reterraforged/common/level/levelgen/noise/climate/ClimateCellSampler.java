package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;

public record ClimateCellSampler(Noise source, DistanceFunction distance) implements Noise {
	public static final Codec<ClimateCellSampler> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(ClimateCellSampler::source),
		DistanceFunction.CODEC.fieldOf("distance").forGetter(ClimateCellSampler::distance)
	).apply(instance, ClimateCellSampler::new));

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new ClimateCellSampler(this.source.mapAll(visitor), this.distance));
	}
	
	@Override
	public Codec<ClimateCellSampler> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        final int xr = NoiseUtil.floor(x);
        final int yr = NoiseUtil.floor(y);
        float centerX = x;
        float centerY = y;
        float edgeDistance = 999999.0f;
        float edgeDistance2 = 999999.0f;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xr + dx;
                final int cy = yr + dy;
                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                final float cxf = cx + vec.x();
                final float cyf = cy + vec.y();
                final float distance = this.distance.apply(cxf - x, cyf - y);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        return this.source.compute(centerX, centerY, seed);
	}
}
