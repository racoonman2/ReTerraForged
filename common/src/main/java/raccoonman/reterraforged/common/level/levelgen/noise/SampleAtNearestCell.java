package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.cell.CellShape;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.MathUtil;

public record SampleAtNearestCell(Noise source, CellShape cellShape, float jitter) implements Noise {
	public static final Codec<SampleAtNearestCell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("source").forGetter(SampleAtNearestCell::source),
		CellShape.CODEC.fieldOf("cell_shape").forGetter(SampleAtNearestCell::cellShape),
		Codec.FLOAT.fieldOf("jitter").forGetter(SampleAtNearestCell::jitter)
	).apply(instance, SampleAtNearestCell::new));
	
	
	@Override
	public Codec<SampleAtNearestCell> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		x = this.cellShape.adjustX(x);
		y = this.cellShape.adjustY(y);

		int minX = NoiseUtil.floor(x) - 1;
        int minY = NoiseUtil.floor(y) - 1;
        int maxX = NoiseUtil.floor(x) + 2;
        int maxY = NoiseUtil.floor(y) + 2;

        float nearestX = x;
        float nearestY = y;
        float distance = Float.MAX_VALUE;
        float distance2 = Float.MAX_VALUE;

        for (int cy = minY; cy <= maxY; cy++) {
            for (int cx = minX; cx <= maxX; cx++) {
                int hash = MathUtil.hash(seed, cx, cy);
                float px = this.cellShape.getCellX(hash, cx, cy, this.jitter);
                float py = this.cellShape.getCellY(hash, cx, cy, this.jitter);
                float d2 = NoiseUtil.dist2(x, y, px, py);

                if (d2 < distance) {
                    distance2 = distance;
                    distance = d2;
                    nearestX = px;
                    nearestY = py;
                } else if (d2 < distance2) {
                    distance2 = d2;
                }
            }
        }
        return this.source.getValue(nearestX, nearestY, seed);
	}
}
