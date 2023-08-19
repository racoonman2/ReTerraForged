package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.cell.CellShape;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.MathUtil;

public record Weirdness(CellShape shape, float frequency, float jitter) implements Noise {
	public static final Codec<Weirdness> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		CellShape.CODEC.fieldOf("shape").forGetter(Weirdness::shape),
		Codec.FLOAT.fieldOf("frequency").forGetter(Weirdness::frequency),
		Codec.FLOAT.fieldOf("jitter").forGetter(Weirdness::jitter)
	).apply(instance, Weirdness::new));
	
	@Override
	public Codec<Weirdness> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		return MathUtil.rand(this.getCell(x, y, seed), seed);
	}
	
	private int getCell(float x, float y, int seed) {
        x = this.shape.adjustX(x);
        y = this.shape.adjustY(y);
		
		x = x * this.frequency;
		y = y * this.frequency;

		int minX = NoiseUtil.floor(x) - 1;
		int minY = NoiseUtil.floor(y) - 1;
		int maxX = NoiseUtil.floor(x) + 2;
		int maxY = NoiseUtil.floor(y) + 2;
		int nearestHash = 0;
		float shortestDist = Float.MAX_VALUE;

		for (int cy = minY; cy <= maxY; cy++) {
			for (int cx = minX; cx <= maxX; cx++) {
				int hash = MathUtil.hash(seed, cx, cy);
				float cellX = this.shape.getCellX(hash, cx, cy, this.jitter);
				float cellY = this.shape.getCellY(hash, cx, cy, this.jitter);
				float dist = NoiseUtil.dist2(x, y, cellX, cellY);

				if (dist < shortestDist) {
					shortestDist = dist;
					nearestHash = hash;
				}
			}
		}

		return nearestHash;
	}
}
