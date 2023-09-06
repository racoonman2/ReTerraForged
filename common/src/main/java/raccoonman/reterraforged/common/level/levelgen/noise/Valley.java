/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.noise;

import java.util.Arrays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;

public record Valley(Noise source, int octaves, float strength, float gridSize, float amplitude, float lacunarity, float falloff, Valley.Mode blendMode, ThreadLocal<float[]> erosionCache) implements Noise {
	public static final Codec<Valley> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((v) -> v.source),
		Codec.INT.fieldOf("octaves").forGetter((v) -> v.octaves),
		Codec.FLOAT.fieldOf("strength").forGetter((v) -> v.strength),
		Codec.FLOAT.fieldOf("grid_size").forGetter((v) -> v.gridSize),
		Codec.FLOAT.fieldOf("amplitude").forGetter((v) -> v.amplitude),
		Codec.FLOAT.fieldOf("lacunarity").forGetter((v) -> v.lacunarity),
		Codec.FLOAT.fieldOf("falloff").forGetter((v) -> v.falloff),
		Valley.Mode.CODEC.fieldOf("blend_mode").forGetter((v) -> v.blendMode)
	).apply(instance, Valley::new));

    public Valley(Noise source, float strength, float gridSize, Valley.Mode blendMode) {
        this(source, 1, strength, gridSize, blendMode);
    }

    public Valley(Noise source, int octaves, float strength, float gridSize, Valley.Mode blendMode) {
        this(source, octaves, strength, gridSize, 1.0F / (float) (octaves + 1), 2.25F, 0.75F, blendMode);
    }

    public Valley(Noise source, int octaves, float strength, float gridSize, float amplitude, float lacunarity, float falloff, Valley.Mode blendMode) {
		this(source, octaves, strength, gridSize, amplitude, lacunarity, falloff, blendMode, ThreadLocal.withInitial(() -> new float[25]));
    }
    
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Valley(this.source.mapAll(visitor), this.octaves, this.strength, this.gridSize, this.amplitude, this.lacunarity, this.falloff, this.blendMode));
	}

	@Override
	public Codec<Valley> codec() {
		return CODEC;
	}

    @Override
    public float compute(float x, float y, int seed) {
        float value = this.source.compute(x, y, seed);
        float erosion = this.getErosionValue(x, y, seed, this.erosionCache.get());
        return NoiseUtil.lerp(erosion, value, this.blendMode.blend(value, erosion, this.strength));
    }

    @Override
    public boolean equals(Object o) {
	    return o instanceof Valley valley && 
	    	this.source.equals(valley.source) && 
	    	this.octaves == valley.octaves && 
	    	this.strength == valley.strength &&
	    	this.gridSize == valley.gridSize &&
	    	this.amplitude == valley.amplitude &&
	    	this.lacunarity == valley.lacunarity &&
	    	this.falloff == valley.falloff &&
	    	this.blendMode.equals(valley.blendMode)
	    ;
    }
    
    private float getErosionValue(float x, float y, int seed, float[] cache) {
        float sum = 0.0F;
        float max = 0.0F;
        float gain = 1.0F;
        float distance = this.gridSize;
        for (int i = 0; i < this.octaves; ++i) {
            float value = this.getSingleErosionValue(x, y, distance, seed, cache);
            sum += (value *= gain);
            max += gain;
            gain *= this.amplitude;
            distance *= this.falloff;
            x *= this.lacunarity;
            y *= this.lacunarity;
        }
        return sum / max;
    }

    private float getSingleErosionValue(float x, float y, float gridSize, int seed, float[] cache) {
        Arrays.fill(cache, -1.0F);
        int pix = NoiseUtil.floor(x / gridSize);
        int piy = NoiseUtil.floor(y / gridSize);
        float minHeight2 = Float.MAX_VALUE;
        for (int dy1 = -1; dy1 <= 1; ++dy1) {
            for (int dx1 = -1; dx1 <= 1; ++dx1) {
                int pax = pix + dx1;
                int pay = piy + dy1;
                Vec2f vec1 = NoiseUtil.cell(seed, pax, pay);
                float ax = ((float) pax + vec1.x()) * gridSize;
                float ay = ((float) pay + vec1.y()) * gridSize;
                float bx = ax;
                float by = ay;
                float lowestNeighbour = Float.MAX_VALUE;
                for (int dy2 = -1; dy2 <= 1; ++dy2) {
                    for (int dx2 = -1; dx2 <= 1; ++dx2) {
                        int pbx = pax + dx2;
                        int pby = pay + dy2;
                        Vec2f vec2 = pbx == pax && pby == pay ? vec1 : NoiseUtil.cell(seed, pbx, pby);
                        float candidateX = ((float) pbx + vec2.x()) * gridSize;
                        float candidateY = ((float) pby + vec2.y()) * gridSize;
                        float height = Valley.getNoiseValue(dx1 + dx2, dy1 + dy2, candidateX, candidateY, seed, source, cache);
                        if (!(height < lowestNeighbour)) continue;
                        lowestNeighbour = height;
                        bx = candidateX;
                        by = candidateY;
                    }
                }
                float height2 = Valley.sd(x, y, ax, ay, bx, by);
                if (!(height2 < minHeight2)) continue;
                minHeight2 = height2;
            }
        }
        return NoiseUtil.clamp((float) Math.sqrt(minHeight2) / gridSize, 0.0F, 1.0F);
    }

    private static float getNoiseValue(int dx, int dy, float px, float py, int seed, Noise noise, float[] cache) {
        int index = (dy + 2) * 5 + (dx + 2);
        float value = cache[index];
        if (value == -1.0F) {
            cache[index] = value = noise.compute(px, py, seed);
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
        return Valley.len2(padx, pady, badx * h, bady * h);
    }

    private static float len2(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }
    
    public static enum Mode implements StringRepresentable {
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

    	public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
    	private String name;
    	
    	private Mode(String name) {
    		this.name = name;
    	}
    	
    	@Override
    	public String getSerializedName() {
    		return this.name;
    	}
    	
        public abstract float blend(float value, float erosion, float strength);
    }
}

