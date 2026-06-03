package raccoonman.reterraforged.world.worldgen.noise;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registry.RTFBuiltInRegistries;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.CellFunction;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.CurveFunction;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.Interpolation;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warps;

public class Noises {
    private static final Codec<Noise> CODEC = RTFBuiltInRegistries.NOISE_TYPE.byNameCodec().dispatch(Noise::codec, Function.identity());

    public static final Codec<Noise> DIRECT_CODEC = Codec.either(Codec.FLOAT, CODEC).xmap(either -> either.map(Noises::constant, Function.identity()), noise -> {
        if (noise instanceof Constant constant) {
            return Either.left(constant.value());
        }
        return Either.right(noise);
    });

    public static final Noise ZERO = Noises.constant(0.0F);
    public static final Noise ONE = Noises.constant(1.0F);
    
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
		register("lerp", Lerp.CODEC);
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
		register("cell_value", CellValue.CODEC);
		register("pseudo_erosion", PseudoErosion.CODEC);		
		register("temperature", Temperature.CODEC);
		register("continent", Continent.CODEC);
	}

	public static Noise constant(float value) {
		return new Constant(value);
	}
	
	public static Noise negative(Noise input) {
		return Noises.mul(input, -1.0F);
	}
	
	public static Noise sin(int seed, float scale, Noise alpha) {
		return shiftSeed(new Sin(1.0F / scale, alpha), seed);
	}
	
	public static Noise white(int seed, float scale) {
		return shiftSeed(new White(1.0F / scale), seed);
	}
	
	public static Noise perlin(int seed, float scale, int octaves) {
		return perlin(seed, scale, octaves, 2.0F);
	}

	public static Noise perlin(int seed, float scale, int octaves, float lacunarity) {
		return perlin(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise perlin(int seed, float scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new Perlin(1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3), seed);
	}

	public static Noise perlin2(int seed, float scale, int octaves) {
		return perlin2(seed, scale, octaves, 2.0F);
	}

	public static Noise perlin2(int seed, float scale, int octaves, float lacunarity) {
		return perlin2(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise perlin2(int seed, float scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new Perlin2(1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3), seed);
	}
	
	public static Noise perlinRidge(int seed, float scale, int octaves) {
		return perlinRidge(seed, scale, octaves, 2.0F);
	}

	public static Noise perlinRidge(int seed, float scale, int octaves, float lacunarity) {
		return perlinRidge(seed, scale, octaves, lacunarity, 0.975F);
	}
	
	public static Noise perlinRidge(int seed, float scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new PerlinRidge(1.0F / scale, octaves, lacunarity, gain, Interpolation.CURVE3), seed);
	}
	
	public static Noise simplex(int seed, float scale, int octaves) {
		return simplex(seed, scale, octaves, 2.0F);
	}

	public static Noise simplex(int seed, float scale, int octaves, float lacunarity) {
		return simplex(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise simplex(int seed, float scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new Simplex(1.0F / scale, octaves, lacunarity, gain), seed);
	}

	public static Noise simplex2(int seed, float scale, int octaves) {
		return simplex2(seed, scale, octaves, 2.0F);
	}
	
	public static Noise simplex2(int seed, float scale, int octaves, float lacunarity) {
		return simplex2(seed, scale, octaves, lacunarity, 0.5F);
	}
	
	public static Noise simplex2(int seed, float scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new Simplex2(1.0F / scale, octaves, lacunarity, gain), seed);
	}
	
	public static Noise simplexRidge(int seed, float scale, int octaves) {
		return simplexRidge(seed, scale, octaves, 2.0F);
	}

	public static Noise simplexRidge(int seed, float scale, int octaves, float lacunarity) {
		return simplexRidge(seed, scale, octaves, lacunarity, 0.975F);
	}
	
	public static Noise simplexRidge(int seed, float scale, int octaves, float lacunarity, float gain) {
		return shiftSeed(new SimplexRidge(1.0F / scale, octaves, lacunarity, gain), seed);
	}
	
	public static Noise worley(int seed, int scale) {
		return worley(seed, scale, CellFunction.VALUE, DistanceFunction.EUCLIDEAN);
	}
	
	public static Noise worley(int seed, int scale, CellFunction cellFunction, DistanceFunction distanceFunction) {
		return shiftSeed(new Worley(1.0F / scale, 1.0F, cellFunction, distanceFunction), seed);
	}
	
	public static Noise worleyEdge(int seed, int scale) {
		return worleyEdge(seed, scale, EdgeFunction.DISTANCE_2);
	}

	public static Noise worleyEdge(int seed, int scale, EdgeFunction edgeFunction) {
		return worleyEdge(seed, scale, edgeFunction, DistanceFunction.EUCLIDEAN);			
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
		return shiftSeed(new Billow(1.0F / scale, octaves, lacunarity, gain), seed);
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
	
	public static Noise shiftSeed(Noise input, int shift) {
		return shift != 0 ? new ShiftSeed(input, shift) : input;
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
	
	public static Noise lerp(Noise alpha, float from, float to) {
		return lerp(alpha, constant(from), constant(to));
	}
	
	public static Noise lerp(Noise alpha, Noise from, float to) {
		return lerp(alpha, from, constant(to));
	}
	
	public static Noise lerp(Noise alpha, float from, Noise to) {
		return lerp(alpha, constant(from), to);
	}
	
	public static Noise lerp(Noise alpha, Noise from, Noise to) {
		return new Lerp(alpha, from, to);
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

	public static Noise warpSimplex(Noise input, int seed, int scale, int octaves, float pow) {
		return warp(input, 
        	simplex(seed, scale, octaves), 
        	simplex(seed + 1, scale, octaves), 
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
		return warp(input, Warps.noise(warpX, warpZ, pow));
	}
	
	public static Noise warp(Noise input, raccoonman.reterraforged.world.worldgen.noise.warp.Warp warp) {
		return new Warp(input, warp);
	}
	
	public static Noise cellValue() {
		return new CellValue();
	}
	
	public static Noise erosion(Noise input, int seed, int octaves, float strength, float gridSize, float amplitude, float lacunarity, float distanceFallOff, PseudoErosion.BlendMode blendMode) {
		return new PseudoErosion(input, seed, octaves, strength, gridSize, amplitude, lacunarity, distanceFallOff, blendMode);
	}

	public static Noise temperature(float frequency, int power) {
		return new Temperature(frequency, power);
	}
	
	public static Noise continent(float scale, float jitter, float sizeVariance, float skipping, Noise cliffNoise, Noise bayNoise, float oceanThreshold, float coastThreshold) {
		return new Continent(1.0F / scale, jitter, sizeVariance, skipping, cliffNoise, bayNoise, oceanThreshold, coastThreshold);
	}

	private static void register(String name, MapCodec<? extends Noise> value) {
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
		public MapCodec<HolderHolder> codec() {
			throw new UnsupportedOperationException("Called .codec() on HolderHolder");
		}
	}
}
