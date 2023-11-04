package raccoonman.reterraforged.common.level.levelgen.test.module;

import java.util.Arrays;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;

public class Ridge {
	private int seed;
	private int octaves;
	private float strength;
	private float gridSize;
	private float amplitude;
	private float lacunarity;
	private float distanceFallOff;
	private Mode blendMode;

	public Ridge(int seed, float strength, float gridSize, Mode blendMode) {
		this(seed, 1, strength, gridSize, blendMode);
	}

	public Ridge(int seed, int octaves, float strength, float gridSize, Mode blendMode) {
		this(seed, octaves, strength, gridSize, 1.0f / (octaves + 1), 2.25f, 0.75f, blendMode);
	}

	public Ridge(int seed, int octaves, float strength, float gridSize, float amplitude, float lacunarity, float distanceFallOff, Mode blendMode) {
		this.seed = seed;
		this.octaves = octaves;
		this.strength = strength;
		this.gridSize = gridSize;
		this.amplitude = amplitude;
		this.lacunarity = lacunarity;
		this.distanceFallOff = distanceFallOff;
		this.blendMode = blendMode;
	}

	public Module wrap(Noise source) {
		return new Module(this, source);
	}

	public float getValue(int seed, float x, float y, Noise source) {
		return this.getValue(seed, x, y, source, new float[25]);
	}

	public float getValue(int seed, float x, float y, Noise source, float[] cache) {
		float value = source.compute(x, y, seed);
		float erosion = this.getErosionValue(x, y, source, cache, seed);
		return NoiseUtil.lerp(erosion, value, this.blendMode.blend(value, erosion, this.strength));
	}

	public float getErosionValue(float x, float y, Noise source, float[] cache, int seed) {
		float sum = 0.0F;
		float max = 0.0F;
		float gain = 1.0F;
		float distance = this.gridSize;
		for (int i = 0; i < this.octaves; ++i) {
			float value = this.getSingleErosionValue(x, y, distance, source, cache, seed);
			value *= gain;
			sum += value;
			max += gain;
			gain *= this.amplitude;
			distance *= this.distanceFallOff;
			x *= this.lacunarity;
			y *= this.lacunarity;
		}
		return sum / max;
	}

	public float getSingleErosionValue(float x, float y, float gridSize, Noise source, float[] cache, int seed) {
		Arrays.fill(cache, -1.0F);
		int pix = NoiseUtil.floor(x / gridSize);
		int piy = NoiseUtil.floor(y / gridSize);
		float minHeight2 = Float.MAX_VALUE;
		for (int dy1 = -1; dy1 <= 1; ++dy1) {
			for (int dx1 = -1; dx1 <= 1; ++dx1) {
				int pax = pix + dx1;
				int pay = piy + dy1;
				Vec2f vec1 = NoiseUtil.cell(this.seed + seed, pax, pay);
				float ax = (pax + vec1.x()) * gridSize;
				float ay = (pay + vec1.y()) * gridSize;
				float bx = ax;
				float by = ay;
				float lowestNeighbour = Float.MAX_VALUE;
				for (int dy2 = -1; dy2 <= 1; ++dy2) {
					for (int dx2 = -1; dx2 <= 1; ++dx2) {
						int pbx = pax + dx2;
						int pby = pay + dy2;
						Vec2f vec2 = (pbx == pax && pby == pay) ? vec1 : NoiseUtil.cell(this.seed + seed, pbx, pby);
						float candidateX = (pbx + vec2.x()) * gridSize;
						float candidateY = (pby + vec2.y()) * gridSize;
						float height = getNoiseValue(dx1 + dx2, dy1 + dy2, candidateX, candidateY, source, cache, seed);
						if (height < lowestNeighbour) {
							lowestNeighbour = height;
							bx = candidateX;
							by = candidateY;
						}
					}
				}
				float height2 = sd(x, y, ax, ay, bx, by);
				if (height2 < minHeight2) {
					minHeight2 = height2;
				}
			}
		}
		return NoiseUtil.clamp(sqrt(minHeight2) / gridSize, 0.0F, 1.0F);
	}

	private static float getNoiseValue(int dx, int dy, float px, float py, Noise module, float[] cache, int seed) {
		int index = (dy + 2) * 5 + (dx + 2);
		float value = cache[index];
		if (value == -1.0F) {
			value = module.compute(px, py, seed);
			cache[index] = value;
		}
		return value;
	}

	private static float sd(float px, float py, float ax, float ay, float bx, float by) {
		float padx = px - ax;
		float pady = py - ay;
		float badx = bx - ax;
		float bady = by - ay;
		float paba = padx * badx + pady * bady;
		float baba = badx * badx + bady * bady;
		float h = NoiseUtil.clamp(paba / baba, 0.0F, 1.0F);
		return len2(padx, pady, badx * h, bady * h);
	}

	private static float len2(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return dx * dx + dy * dy;
	}

	private static float sqrt(float value) {
		return (float) Math.sqrt(value);
	}

	public static class Module implements Noise {
		private Ridge ridge;
		private Noise source;
		private ThreadLocal<float[]> cache;

		private Module(Ridge ridge, Noise source) {
			this.cache = ThreadLocal.withInitial(() -> new float[25]);
			this.ridge = ridge;
			this.source = source;
		}
		
		@Override
		public float compute(float x, float y, int seed) {
			return this.ridge.getValue(seed, x, y, this.source, this.cache.get());
		}

		@Override
		public Codec<Module> codec() {
			return Codec.unit(this);
		}

		@Override
		public Noise mapAll(Visitor visitor) {
			return visitor.apply(this);
		}
	}

	public enum Mode {
		CONSTANT {
			@Override
			public float blend(float value, float erosion, float strength) {
				return 1.0F - strength;
			}
		},
		INPUT_LINEAR {
			@Override
			public float blend(float value, float erosion, float strength) {
				return 1.0F - strength * value;
			}
		},
		OUTPUT_LINEAR {
			@Override
			public float blend(float value, float erosion, float strength) {
				return 1.0F - strength * erosion;
			}
		};

		public abstract float blend(float value, float erosion, float strength);
	}
}
