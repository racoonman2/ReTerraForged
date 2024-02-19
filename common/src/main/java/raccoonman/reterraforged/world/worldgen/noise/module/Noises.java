package raccoonman.reterraforged.world.worldgen.noise.module;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registries.RTFBuiltInRegistries;
import raccoonman.reterraforged.world.worldgen.cell.CellField;
import raccoonman.reterraforged.world.worldgen.cell.noise.CellSampler;
import raccoonman.reterraforged.world.worldgen.noise.domain.Domain;
import raccoonman.reterraforged.world.worldgen.noise.domain.Domains;
import raccoonman.reterraforged.world.worldgen.noise.function.CellFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.CurveFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;

public class Noises {
    private static final Codec<Noise> CODEC = RTFBuiltInRegistries.NOISE_TYPE.byNameCodec().dispatch(Noise::codec, Function.identity());
    public static final float MAX_REASONABLE_NOISE_VALUE = 1000000.0F;
    public static final Codec<Float> NOISE_VALUE_CODEC = Codec.floatRange(-MAX_REASONABLE_NOISE_VALUE, MAX_REASONABLE_NOISE_VALUE);
    public static final Codec<Noise> DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, CODEC).xmap(either -> either.map(Noises::constant, Function.identity()), noise -> {
        if (noise instanceof Constant constant) {
            return Either.left(constant.value());
        }
        return Either.right(noise);
    });
    
	public static void bootstrap() {
		register("constant", Constant.CODEC);
		register("sin", Sin.CODEC); 
		register("white", White.CODEC);
		register("perlin", Perlin.CODEC);
		register("perlin2", Perlin2.CODEC);
		register("perlin_ridge", PerlinRidge.CODEC);
		register("simplex", Simplex.CODEC);
		register("simplex2", Simplex2.CODEC);
		register("simplex_ridge", SimplexRidge.CODEC);
		register("worley", Worley.CODEC);
		register("worley_edge", WorleyEdge.CODEC);
		register("billow", Billow.CODEC);
		register("cubic", Cubic.CODEC);
		register("line", Line.CODEC);
		register("shift", ShiftSeed.CODEC);
		register("frequency", Frequency.CODEC);
		register("add", Add.CODEC);
		register("multiply", Multiply.CODEC);
		register("power", Power.CODEC);
		register("power_curve", PowerCurve.CODEC);
		register("curve", Curve.CODEC);
		register("gradient", Gradient.CODEC);
		register("terrace", Terrace.CODEC);
		register("advanced_terrace", AdvancedTerrace.CODEC);
		register("invert", Invert.CODEC);
		register("blend", Blend.CODEC);
		register("alpha", Alpha.CODEC);
		register("boost", Boost.CODEC);
		register("steps", Steps.CODEC);
		register("abs", Abs.CODEC);
		register("map", Map.CODEC);
		register("clamp", Clamp.CODEC);
		register("threshold", Threshold.CODEC);
		register("min", Min.CODEC);
		register("max", Max.CODEC);
		register("warp", Warp.CODEC);
		register("erosion", Erosion.CODEC);
		register("linear_spline", LinearSpline.CODEC);
		register("cache", Cache2d.CODEC);
		register("cell", CellSampler.Marker.CODEC);
		
		register("legacy_temperature", LegacyTemperature.CODEC);
		register("legacy_moisture", LegacyMoisture.CODEC);
	}

	public static Noise constant(float value) {
		return new Constant(value);
	}
	
	public static Noise negative(Noise input) {
		return Noises.mul(input, -1.0F);
	}

	public static Noise zero() {
		return constant(0.0F);
	}

	public static Noise one() {
		return constant(1.0F);
	}
	
	public static Noise sin(int seed, float frequency, Noise alpha) {
		return shiftSeed(new Sin(frequency, alpha), seed);
	}
	
	public static Noise white(int seed, int scale) {
		return shiftSeed(new White(1.0F / scale), seed);
	}
	
	public static Noise perlin(int seed, int scale, int octaves) {
		return perlin(seed, scale, octaves, 2.0F);
	}

	public static Noise perlin(int seed, int scale, int octaves, float lacunarity) {
		return perlin(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise perlin(int seed, int scale, int octaves, float lacunarity, float gain) {
		return new Perlin(seed, 1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3);
	}
	
	public static Noise perlin2(int seed, int scale, int octaves) {
		return perlin2(seed, scale, octaves, 2.0F);
	}

	public static Noise perlin2(int seed, int scale, int octaves, float lacunarity) {
		return perlin2(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise perlin2(int seed, int scale, int octaves, float lacunarity, float gain) {
		return new Perlin2(seed, 1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3);
	}
	
	public static Noise perlinRidge(int seed, int scale, int octaves) {
		return perlinRidge(seed, scale, octaves, 2.0F);
	}

	public static Noise perlinRidge(int seed, int scale, int octaves, float lacunarity) {
		return perlinRidge(seed, scale, octaves, lacunarity, 0.975F);
	}
	
	public static Noise perlinRidge(int seed, int scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new PerlinRidge(1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3), seed);
	}
	
	public static Noise simplex(int seed, int scale, int octaves) {
		return simplex(seed, scale, octaves, 2.0F);
	}

	public static Noise simplex(int seed, int scale, int octaves, float lacunarity) {
		return simplex(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise simplex(int seed, int scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new Simplex(1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3), seed);
	}
	
	public static Noise simplex2(int seed, int scale, int octaves) {
		return simplex2(seed, scale, octaves, 2.0F);
	}

	public static Noise simplex2(int seed, int scale, int octaves, float lacunarity) {
		return simplex2(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise simplex2(int seed, int scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new Simplex2(1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3), seed);
	}
	
	public static Noise simplexRidge(int seed, int scale, int octaves) {
		return simplexRidge(seed, scale, octaves, 2.0F);
	}

	public static Noise simplexRidge(int seed, int scale, int octaves, float lacunarity) {
		return simplexRidge(seed, scale, octaves, lacunarity, 0.975F);
	}
	
	public static Noise simplexRidge(int seed, int scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new SimplexRidge(1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3), seed);
	}
	
	public static Noise worley(int seed, int scale) {
		return worley(seed, scale, CellFunction.CELL_VALUE, DistanceFunction.EUCLIDEAN, zero());
	}
	
	public static Noise worley(int seed, int scale, CellFunction cellFunction, DistanceFunction distanceFunction, Noise lookup) {
		return shiftSeed(new Worley(1.0F / scale, 1.0F, cellFunction, distanceFunction, lookup), seed);
	}
	
	public static Noise worleyEdge(int seed, int scale) {
		return worleyEdge(seed, scale, EdgeFunction.DISTANCE_2, DistanceFunction.EUCLIDEAN);
	}
	
	public static Noise worleyEdge(int seed, int scale, EdgeFunction edgeFunction, DistanceFunction distanceFunction) {
		return shiftSeed(new WorleyEdge(1.0F / scale, 1.0F, edgeFunction, distanceFunction), seed);
	}
	
	public static Noise billow(int seed, int scale, int octaves) {
		return billow(seed, scale, octaves, 2.0F);
	}

	public static Noise billow(int seed, int scale, int octaves, float lacunarity) {
		return billow(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise billow(int seed, int scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new Billow(1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3), seed);
	}
	
	public static Noise cubic(int seed, int scale, int octaves) {
		return cubic(seed, scale, octaves, 2.0F);
	}

	public static Noise cubic(int seed, int scale, int octaves, float lacunarity) {
		return cubic(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise cubic(int seed, int scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new Cubic(1.0F / scale, octaves, lacunarity, gain), seed);
	}
	
	public static Noise line(float x1, float z1, float x2, float z2, Noise radiusSq, Noise fadeIn, Noise fadeOut, float feather) {
		return new Line(x1, z1, x2, z2, radiusSq, fadeIn, fadeOut, feather);
	}
	
	public static Noise shiftSeed(Noise input, int seed) {
		return new ShiftSeed(input, seed);
	}

	public static Noise frequency(Noise input, float freq) {
		return frequency(input, freq, freq);
	}
	
	public static Noise frequency(Noise input, float xFreq, float zFreq) {
		return frequency(input, constant(xFreq), constant(zFreq));
	}
	
	public static Noise frequency(Noise input, Noise xFreq, Noise zFreq) {
		return new Frequency(input, xFreq, zFreq);
	}
	
	public static Noise add(Noise input1, float input2) {
		return add(input1, constant(input2));
	}
	
	public static Noise add(Noise input1, Noise input2) {
		return new Add(input1, input2);
	}

	public static Noise mul(Noise input1, float input2) {
		return mul(input1, constant(input2));
	}
	
	public static Noise mul(Noise input1, Noise input2) {
		return new Multiply(input1, input2);
	}
	
	public static Noise pow(Noise input, float pow) {
		return new Power(input, pow);
	}
	
	public static Noise powCurve(Noise input, float pow) {
		return new PowerCurve(input, pow);
	}
	
	public static Noise curve(Noise input, CurveFunction curveFunction) {
		return new Curve(input, curveFunction);
	}
	
	public static Noise gradient(Noise input, float lower, float upper, float strength) {
		return gradient(input, constant(lower), constant(upper), constant(strength));
	}
	
	public static Noise gradient(Noise input, Noise lower, Noise upper, Noise strength) {
		return new Gradient(input, lower, upper, strength);
	}
	
	public static Noise terrace(Noise input, float lowerCurve, float upperCurve, float lower, float blendRange, int steps) {
		return terrace(input, constant(lowerCurve), constant(upperCurve), constant(lower), blendRange, steps);
	}
	
	public static Noise terrace(Noise input, Noise lowerCurve, Noise upperCurve, Noise rampHeight, float blendRange, int steps) {
        return new Terrace(input, lowerCurve, upperCurve, rampHeight, blendRange, steps);
    }

	public static Noise advancedTerrace(Noise input, float modulation, float mask, float slope, float blendMin, float blendMax, int steps, int octaves) {
        return advancedTerrace(input, constant(modulation), constant(mask), constant(slope), blendMin, blendMax, steps, octaves);
    }
	
    public static Noise advancedTerrace(Noise input, Noise modulation, Noise mask, Noise slope, float blendMin, float blendMax, int steps, int octaves) {
        return new AdvancedTerrace(input, modulation, mask, slope, blendMin, blendMax, steps, octaves);
    }
	
	public static Noise invert(Noise input) {
		return new Invert(input);
	}
	
	public static Noise blend(Noise selector, Noise input1, Noise input2, float mid, float range) {
		return new Blend(selector, input1, input2, mid, range, Interpolation.LINEAR);
	}
	
	public static Noise alpha(Noise input, float alpha) {
		return alpha(input, constant(alpha));
	}
	
	public static Noise alpha(Noise input, Noise alpha) {
		return new Alpha(input, alpha);
	}
	
	public static Noise boost(Noise input) {
		return boost(input, 1);
	}
	
	public static Noise boost(Noise input, int iterations) {
		return new Boost(input, iterations);
	}
	
	public static Noise steps(Noise input, int steps, float slopeMin, float slopeMax) {
		return steps(input, steps, slopeMin, slopeMax, Interpolation.LINEAR);
	}
	
	public static Noise steps(Noise input, int steps, float slopeMin, float slopeMax, CurveFunction slopeCurve) {
		return steps(input, constant(steps), constant(slopeMin), constant(slopeMax), slopeCurve);
	}

	public static Noise steps(Noise input, Noise steps, Noise slopeMin, Noise slopeMax) {
		return new Steps(input, steps, slopeMin, slopeMax, Interpolation.LINEAR);
	}
	
	public static Noise steps(Noise input, Noise steps, Noise slopeMin, Noise slopeMax, CurveFunction slopeCurve) {
		return new Steps(input, steps, slopeMin, slopeMax, slopeCurve);
	}
	
	public static Noise abs(Noise input) {
		return new Abs(input);
	}
	
	public static Noise map(Noise input, float from, float to) {
		return map(input, constant(from), constant(to));
	}
	
	public static Noise map(Noise input, Noise from, Noise to) {
        if (from.minValue() == from.maxValue() && from.minValue() == input.minValue() && to.minValue() == to.maxValue() && to.maxValue() == input.maxValue()) {
            return input;
        }
		return new Map(input, from, to);
	}

	public static Noise clamp(Noise input, float min, float max) {
		return clamp(input, constant(min), constant(max));
	}
	
	public static Noise clamp(Noise input, Noise min, Noise max) {
		return new Clamp(input, min, max);
	}
	
	public static Noise threshold(Noise input, float lower, float upper, float threshold) {
		return threshold(input, constant(lower), constant(upper), constant(threshold));
	}
	
	public static Noise threshold(Noise input, Noise lower, Noise upper, float threshold) {
		return threshold(input, lower, upper, constant(threshold));
	}
	
	public static Noise threshold(Noise input, Noise lower, Noise upper, Noise threshold) {
		return new Threshold(input, lower, upper, threshold);
	}
	
	public static Noise min(Noise input1, float input2) {
		return min(input1, constant(input2));
	}
	
	public static Noise min(Noise input1, Noise input2) {
		return new Min(input1, input2);
	}

	public static Noise max(Noise input1, float input2) {
		return max(input1, constant(input2));
	}
	
	public static Noise max(Noise input1, Noise input2) {
		return new Max(input1, input2);
	}
	
	public static Noise warpPerlin(Noise input, int seed, int scale, int octaves, float pow) {
		return warp(input, 
        	perlin(seed, scale, octaves), 
        	perlin(seed + 1, scale, octaves), 
        	pow
        );
	}
	
	public static Noise warpWhite(Noise input, int seed, int scale, float pow) {
		return warp(input, 
        	white(seed, scale), 
        	white(seed + 1, scale), 
        	pow
        );
	}
	
	public static Noise warp(Noise input, Noise warpX, Noise warpZ, float pow) {
		return warp(input, warpX, warpZ, constant(pow));
	}
	
	public static Noise warp(Noise input, Noise warpX, Noise warpZ, Noise pow) {
		return warp(input, Domains.domain(warpX, warpZ, pow));
	}
	
	public static Noise warp(Noise input, Domain domain) {
		return new Warp(input, domain);
	}

	@Deprecated
	public static Noise cache2d(Noise input) {
		return new Cache2d(input);
	}
	
	public static Noise erosion(Noise input, int seed, int octaves, float strength, float gridSize, float amplitude, float lacunarity, float distanceFallOff, Erosion.BlendMode blendMode) {
		return new Erosion(input, seed, octaves, strength, gridSize, amplitude, lacunarity, distanceFallOff, blendMode);
	}
	
	public static Noise cell(CellField field) {
		return new CellSampler.Marker(field);
	}
 
	private static void register(String name, Codec<? extends Noise> value) {
		RegistryUtil.register(RTFBuiltInRegistries.NOISE_TYPE, name, value);
	}
	
	public record HolderHolder(Holder<Noise> holder) implements Noise {
		
		@Override
		public float compute(float x, float z, int seed) {
			return this.holder.value().compute(x, z, seed);
		}

		@Override
		public float minValue() {
			return this.holder.isBound() ? this.holder.value().minValue() : Float.NEGATIVE_INFINITY;
		}

		@Override
		public float maxValue() {
			return this.holder.isBound() ? this.holder.value().maxValue() : Float.POSITIVE_INFINITY;
		}

		@Override
		public Noise mapAll(Visitor visitor) {
			return visitor.apply(new HolderHolder(Holder.direct(this.holder.value().mapAll(visitor))));
		}

		@Override
		public Codec<HolderHolder> codec() {
			throw new UnsupportedOperationException("Called .codec() on HolderHolder");
		}
	}
}
