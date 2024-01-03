package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.CellFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.DistanceFunction;

record Worley(float frequency, float distance, CellFunction cellFunction, DistanceFunction distanceFunction, Noise lookup, float min, float max) implements Noise {
	public static final Codec<Worley> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Worley::frequency),
		Codec.FLOAT.fieldOf("distance").forGetter(Worley::distance),
		CellFunction.CODEC.fieldOf("cell_function").forGetter(Worley::cellFunction),
		DistanceFunction.CODEC.fieldOf("distance_function").forGetter(Worley::distanceFunction),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lookup").forGetter(Worley::lookup)
	).apply(instance, Worley::new));
	
	public Worley(float frequency, float distance, CellFunction cellFunction, DistanceFunction distanceFunction, Noise lookup) {
		this(frequency, distance, cellFunction, distanceFunction, lookup, min(cellFunction, lookup), max(cellFunction, lookup));
	}
	
	@Override
	public float compute(float x, float z, int seed) {
        x *= this.frequency;
        z *= this.frequency;
        float value = sample(x, z, seed, this.distance, this.cellFunction, this.distanceFunction, this.lookup);
        return this.cellFunction.mapValue(value, this.min, this.max, this.max - this.min);
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
		return visitor.apply(new Worley(this.frequency, this.distance, this.cellFunction, this.distanceFunction, this.lookup.mapAll(visitor)));
	}

	@Override
	public Codec<Worley> codec() {
		return CODEC;
	}
	
    public static float sample(float x, float y, int seed, float distance, CellFunction cellFunction, DistanceFunction distanceFunction, Noise lookup) {
        int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        NoiseUtil.Vec2f vec2f = null;
        float nearest = Float.MAX_VALUE;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xi + dx;
                int cy = yi + dy;
                NoiseUtil.Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float deltaX = cx + vec.x() * distance - x;
                float deltaY = cy + vec.y() * distance - y;
                float dist = distanceFunction.apply(deltaX, deltaY);
                if (dist < nearest) {
                    nearest = dist;
                    vec2f = vec;
                    cellX = cx;
                    cellY = cy;
                }
            }
        }
        return cellFunction.apply(seed, cellX, cellY, nearest, vec2f, lookup);
    }
    
    private static float min(CellFunction function, Noise lookup) {
        if (function == CellFunction.NOISE_LOOKUP) {
        	return lookup.minValue();
        }
        return -1.0F;
    }
    
    private static float max(CellFunction function, Noise lookup) {
        if (function == CellFunction.NOISE_LOOKUP) {
            return lookup.maxValue();
        }
        if (function == CellFunction.DISTANCE) {
            return 0.25F;
        }
        return 1.0F;
    }
}
