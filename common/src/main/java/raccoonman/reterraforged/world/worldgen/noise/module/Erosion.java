package raccoonman.reterraforged.world.worldgen.noise.module;

import java.util.Arrays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;

public record Erosion(Noise input, int seed, int octaves, float strength, float gridSize, float amplitude, float lacunarity, float distanceFallOff, BlendMode blendMode, ThreadLocal<float[]> cache) implements Noise {
	public static final Codec<Erosion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Erosion::input),
		Codec.INT.fieldOf("seed").forGetter(Erosion::seed),
		Codec.INT.fieldOf("octaves").forGetter(Erosion::octaves),
		Codec.FLOAT.fieldOf("strength").forGetter(Erosion::strength),
		Codec.FLOAT.fieldOf("grid_size").forGetter(Erosion::gridSize),
		Codec.FLOAT.fieldOf("amplitude").forGetter(Erosion::amplitude),
		Codec.FLOAT.fieldOf("lacunarity").forGetter(Erosion::lacunarity),
		Codec.FLOAT.fieldOf("distance_falloff").forGetter(Erosion::distanceFallOff),
		BlendMode.CODEC.fieldOf("blend_mode").forGetter(Erosion::blendMode)
	).apply(instance, Erosion::new));

	public Erosion(Noise input, int seed, int octaves, float strength, float gridSize, float amplitude, float lacunarity, float distanceFallOff, BlendMode blendMode) {
		this(input, seed, octaves, strength, gridSize, amplitude, lacunarity, distanceFallOff, blendMode, ThreadLocal.withInitial(() -> new float[5 * 5]));
	}

	@Override
	public float compute(float x, float z, int seed) {
		float value = this.input.compute(x, z, seed);
		float erosion = this.getErosionValue(x, z, this.cache.get());
		return NoiseUtil.lerp(erosion, value, this.blendMode.blend(value, erosion, this.strength));
	}

	@Override
	public float minValue() {
		return 0.0F;
	}

	@Override
	public float maxValue() {
		return 1.0F;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return new Erosion(this.input.mapAll(visitor), this.seed, this.octaves, this.strength, this.gridSize, this.amplitude, this.lacunarity, this.distanceFallOff, this.blendMode);
	}

	@Override
	public Codec<Erosion> codec() {
		return CODEC;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Erosion other && other.input.equals(this.input) && other.seed == this.seed && other.octaves == this.octaves && other.strength == this.strength && other.gridSize == this.gridSize && other.amplitude == this.amplitude && other.lacunarity == this.lacunarity && other.distanceFallOff == this.distanceFallOff && other.blendMode.equals(this.blendMode);
	}
	
	private float getErosionValue(float x, float y, float[] cache) {
		float sum = 0.0F;
		float max = 0.0F;
		float gain = 1.0F;
		float distance = this.gridSize;
		for (int i = 0; i < this.octaves; ++i) {
			float value = this.getSingleErosionValue(x, y, distance, cache);
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

	private float getSingleErosionValue(float x, float y, float gridSize, float[] cache) {
		Arrays.fill(cache, -1.0F);
		int pix = NoiseUtil.floor(x / gridSize);
		int piy = NoiseUtil.floor(y / gridSize);
		float minHeight2 = Float.MAX_VALUE;
		for (int dy1 = -1; dy1 <= 1; ++dy1) {
			for (int dx1 = -1; dx1 <= 1; ++dx1) {
				int pax = pix + dx1;
				int pay = piy + dy1;
				Vec2f vec1 = NoiseUtil.cell(this.seed, pax, pay);
				float ax = (pax + vec1.x()) * gridSize;
				float ay = (pay + vec1.y()) * gridSize;
				float bx = ax;
				float by = ay;
				float lowestNeighbour = Float.MAX_VALUE;
				for (int dy2 = -1; dy2 <= 1; ++dy2) {
					for (int dx2 = -1; dx2 <= 1; ++dx2) {
						int pbx = pax + dx2;
						int pby = pay + dy2;
						Vec2f vec2 = (pbx == pax && pby == pay) ? vec1 : NoiseUtil.cell(this.seed, pbx, pby);
						float candidateX = (pbx + vec2.x()) * gridSize;
						float candidateY = (pby + vec2.y()) * gridSize;
						float height = getNoiseValue(dx1 + dx2, dy1 + dy2, candidateX, candidateY, this.input, cache);
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

	private static float getNoiseValue(int dx, int dy, float px, float py, Noise module, float[] cache) {
		int index = (dy + 2) * 5 + (dx + 2);
		float value = cache[index];
		if (value == -1.0F) {
			value = module.compute(px, py, 0);
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

	public enum BlendMode implements StringRepresentable {
		CONSTANT("constant") {
			
			@Override
			public float blend(float value, float erosion, float strength) {
				return 1.0F - strength;
			}
		},
		INPUT_LINEAR("input_linear") {
			
			@Override
			public float blend(float value, float erosion, float strength) {
				return 1.0F - strength * value;
			}
		},
		OUTPUT_LINEAR("output_linear") {
			
			@Override
			public float blend(float value, float erosion, float strength) {
				return 1.0F - strength * erosion;
			}
		};
		
		public static final Codec<BlendMode> CODEC = StringRepresentable.fromEnum(BlendMode::values);
		
		private String name;
		
		private BlendMode(String name) {
			this.name = name;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}

		public abstract float blend(float value, float erosion, float strength);
	}
}
