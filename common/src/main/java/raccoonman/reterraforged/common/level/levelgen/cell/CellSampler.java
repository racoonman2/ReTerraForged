package raccoonman.reterraforged.common.level.levelgen.cell;

import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.MathUtil;

public record CellSampler(CellShape shape, float frequency, float jitter) {
	
	public void sample(float x, float y, int seed, CellSample sample) {
        x = this.shape.adjustX(x);
        y = this.shape.adjustY(y);
		
		x = x * this.frequency;
		y = y * this.frequency;

		int minX = NoiseUtil.floor(x) - 1;
		int minY = NoiseUtil.floor(y) - 1;
		int maxX = NoiseUtil.floor(x) + 2;
		int maxY = NoiseUtil.floor(y) + 2;
		float nearestX = x;
		float nearestY = y;
		int nearestHash = 0;
		float distance = Float.MAX_VALUE;
		float distance2 = Float.MAX_VALUE;

		for (int cy = minY; cy <= maxY; cy++) {
			for (int cx = minX; cx <= maxX; cx++) {
				int hash = MathUtil.hash(seed, cx, cy);
				float px = this.shape.getCellX(hash, cx, cy, this.jitter);
				float py = this.shape.getCellY(hash, cx, cy, this.jitter);
				float d2 = NoiseUtil.dist2(x, y, px, py);

				if (d2 < distance) {
					distance2 = distance;
					distance = d2;
					nearestX = px;
					nearestY = py;
					nearestHash = hash;
				} else if (d2 < distance2) {
					distance2 = d2;
				}
			}
		}

		sample.nearestHash = nearestHash;
		sample.distance = distance;
		sample.distance2 = distance2;
		sample.nearestX = nearestX;
		sample.nearestY = nearestY;
	}
}
