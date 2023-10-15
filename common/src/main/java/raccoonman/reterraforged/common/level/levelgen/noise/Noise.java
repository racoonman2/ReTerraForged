/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.level.levelgen.noise;

import java.util.function.Function;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.DensityFunctions;
import raccoonman.reterraforged.common.level.levelgen.noise.combiner.Add;
import raccoonman.reterraforged.common.level.levelgen.noise.combiner.Div;
import raccoonman.reterraforged.common.level.levelgen.noise.combiner.Max;
import raccoonman.reterraforged.common.level.levelgen.noise.combiner.Min;
import raccoonman.reterraforged.common.level.levelgen.noise.combiner.Mul;
import raccoonman.reterraforged.common.level.levelgen.noise.combiner.Sub;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.CurveFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.MidPointCurve;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Abs;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.AdvancedTerrace;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Alpha;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Bias;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Boost;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Clamp;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Curve;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Freq;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Grad;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Invert;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.LegacyTerrace;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Map;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Modulate;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Negate;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Power;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.PowerCurve;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Round;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Scale;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Shift;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Steps;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Terrace;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Threshold;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.VariableCurve;
import raccoonman.reterraforged.common.level.levelgen.noise.modifier.Warp;
import raccoonman.reterraforged.common.level.levelgen.noise.selector.Base;
import raccoonman.reterraforged.common.level.levelgen.noise.selector.Blend;
import raccoonman.reterraforged.common.level.levelgen.noise.selector.MultiBlend;
import raccoonman.reterraforged.common.level.levelgen.noise.selector.Select;
import raccoonman.reterraforged.common.level.levelgen.noise.selector.VariableBlend;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Constant;
import raccoonman.reterraforged.common.registries.RTFBuiltInRegistries;
import raccoonman.reterraforged.common.registries.RTFRegistries;

/**
 * @author dags <dags@dags.me>
 */
public interface Noise {	
	static final Codec<Float> NOISE_VALUE_CODEC = Codec.floatRange((float) -DensityFunctions.MAX_REASONABLE_NOISE_VALUE, (float) DensityFunctions.MAX_REASONABLE_NOISE_VALUE);
	public static final Codec<Noise> DIRECT_CODEC = Codec.<Float, Noise>either(NOISE_VALUE_CODEC, RTFBuiltInRegistries.NOISE_TYPE.byNameCodec().dispatchStable(Noise::codec, Functions.identity())).xmap(either -> either.map(Constant::new, Function.identity()), noise -> {
		if (noise instanceof Constant constant) {
			return Either.left(constant.value());
	    }
		return Either.right(noise);
	});
	public static final Codec<Holder<Noise>> CODEC = RegistryFileCodec.create(RTFRegistries.NOISE, DIRECT_CODEC);
    public static final Codec<Noise> HOLDER_HELPER_CODEC = CODEC.xmap(HolderNoise::new, noise -> {
        if (noise instanceof HolderNoise holderHolder) {
            return holderHolder.holder();
        }
        return new Holder.Direct<>(noise);
    });
	public static final Codec<HolderSet<Noise>> LIST_CODEC = RegistryCodecs.homogeneousList(RTFRegistries.NOISE, HOLDER_HELPER_CODEC);
	
	Codec<? extends Noise> codec();
	
	float compute(float x, float y, int seed);

    Noise mapAll(Noise.Visitor visitor);
	
	public interface Visitor {
		Noise apply(Noise noise);
	}
	
	//TODO these shouldn't be defaulted
    default float minValue() {
    	return 0.0F;
    }
    
    default float maxValue() {
    	return 1.0F;
    }
	
    /**
     * Create a Noise who's output is the absolute value of this Noise's output (ie negative values return positive)
     *
     * @return a new Abs Noise
     */
    default Noise abs() {
        if (this instanceof Abs) {
            return this;
        }
        return new Abs(this);
    }

    /**
     * Add the output of another Noise to this Noise's output
     *
     * @param other - the Noise to add
     * @return a new Add Noise
     */
    default Noise add(Noise other) {
        return new Add(ImmutableList.of(this, other));
    }

    /**
     * A utility to Scale and Bias the output of this Noise such that the output is scaled by the alpha amount
     * and biased by 1 - alpha
     *
     * @param alpha - the alpha value (expected to be within the range 0-1)
     * @return a new Alpha Noise
     */
    default Noise alpha(double alpha) {
        return alpha(Source.constant(alpha));
    }

    /**
     * A utility to Scale and Bias the output of this Noise such that the output is scaled by the alpha value
     * and biased by 1 - alpha
     *
     * @param alpha - a Noise who's output provides the alpha value
     * @return a new Alpha Noise
     */
    default Noise alpha(Noise alpha) {
        if (alpha.minValue() < 0 || alpha.maxValue() > 1) {
            return this;
        }
        return new Alpha(this, alpha);
    }

    /**
     * Combines this and the other Noise by blending their outputs only when the other Noise's output
     * falls below the provided falloff value.
     *
     * This Noise's output becomes more dominant the lower the other Noise's output becomes.
     *
     * When the output is above the falloff value only the other Noise's output is returned.
     *
     * @param other - the Noise that this Noise should form the 'base' for
     * @param falloff - the value below which the blending will occur
     * @return a new Base Noise
     */
    default Noise base(Noise other, double falloff) {
        return base(other, falloff, Interpolation.CURVE3);
    }

    /**
     * Combines this and the other Noise by blending their outputs only when the other Noise's output
     * falls below the provided falloff value.
     *
     * This Noise's output becomes more dominant the lower the other Noise's output becomes.
     *
     * When the output is above the falloff value only the other Noise's output is returned.
     *
     * @param other - the Noise that this Noise should form the 'base' for
     * @param falloff - the value below which the blending will occur
     * @param interpolation - the interpolation method to use while blending the outputs
     * @return a new Base Noise
     */
    default Noise base(Noise other, double falloff, Interpolation interpolation) {
        return new Base(this, other, (float) falloff, interpolation);
    }

    /**
     * Modifies this Noise's output by adding the bias Noise's output to the returned value.
     *
     * @param bias - the Noise that biases the output
     * @return a new Bias Noise
     */
    default Noise bias(Noise bias) {
        if (bias.minValue() == 0 && bias.maxValue() == 0) {
            return this;
        }
        return new Bias(this, bias);
    }

    /**
     * Modifies this Noise's output by adding the bias to the returned value.
     *
     * @param bias - the amount to bias this Noise's output by
     * @return a new Bias Noise
     */
    default Noise bias(double bias) {
        return bias(Source.constant(bias));
    }

    /**
     * Combine two other Noises by using this one to d)ecide how much of each should be blended together
     *
     * @param source0 - the first of the two Noises to blend
     * @param source1 - the second of the two Noises to blend
     * @param midpoint - the value at which source0 & source1 will be blended 50%:50% - values either side of this point
     *                 will strengthen the effect of either source Noise proportionally to the distance from the midpoint
     * @param blendRange - the range over which blending occurs, outside of which will produce 100% source 1 or 2
     * @return a new Blend Noise
     */
    default Noise blend(Noise source0, Noise source1, double midpoint, double blendRange) {
        return blend(source0, source1, midpoint, blendRange, Interpolation.LINEAR);
    }

    /**
     * Combine two other Noises by using this one to decide how much of each should be blended together
     *
     * @param source0 - the first of the two Noises to blend
     * @param source1 - the second of the two Noises to blend
     * @param midpoint - the value at which source0 & source1 will be blended 50%:50% - values either side of this point
     *                 will strengthen the effect of either source Noise proportionally to the distance from the midpoint
     * @param blendRange - the range over which blending occurs, outside of which will produce 100% source 1 or 2
     * @param interpolation - the interpolation method to use while blending the outputs
     * @return a new Blend Noise
     */
    default Noise blend(Noise source0, Noise source1, double midpoint, double blendRange, Interpolation interpolation) {
        return new Blend(this, source0, source1, (float) midpoint, (float) blendRange, interpolation);
    }

    /**
     * Similar to the Blend Noise but uses an additional Noise (blendVar) to vary the blend range at a given position
     *
     * @param variable
     * @param source0 - the first of the two Noises to blend
     * @param source1 - the second of the two Noises to blend
     * @param midpoint - the value at which source0 & source1 will be blended 50%:50% - values either side of this point
     *                  will strengthen the effect of either source Noise proportionally to the distance from the midpoint
     * @param min - the lowest possible bound of the blend range
     * @param max - the highest possible bound of the blend range
     * @param interpolation - the interpolation method to use while blending the outputs
     * @return a new BlendVar Noise
     */
    default Noise blendVar(Noise variable, Noise source0, Noise source1, double midpoint, double min, double max, Interpolation interpolation) {
        return new VariableBlend(this, variable, source0, source1, (float) midpoint, (float) min, (float) max, interpolation);
    }

    /**
     * Modifies this Noise's output by amplifying lower values while higher values amplify less
     *
     * @return a new Boost Noise
     */
    default Noise boost() {
        return boost(1);
    }

    /**
     * Modifies this Noise's output by amplifying lower values while higher values amplify less
     *
     * @param iterations - the number of times this Noise should be boosted
     * @return a new Boost Noise
     */
    default Noise boost(int iterations) {
        if (iterations < 1) {
            return this;
        }
        return new Boost(this, iterations);
    }
    
    /**
     * Clamps the output of this Noise between the provided min and max Noise's output at a given coordinate
     *
     * @param min - a Noise that provides the lower bound of the clamp
     * @param max - a Noise that provides the upper bound of the clamp
     * @return a new Clamp Noise
     */
    default Noise clamp(Noise min, Noise max) {
        if (min.minValue() == min.maxValue() && min.minValue() == minValue() && max.minValue() == max.maxValue() && max.maxValue() == maxValue()) {
            return this;
        }
        return new Clamp(this, min, max);
    }

    /**
     * Clamps the output of this Noise between the min and max values
     *
     * @param min - the lower bound of the clamp
     * @param max - the upper bound of the clamp
     * @return a new Clamp Noise
     */
    default Noise clamp(double min, double max) {
        return clamp(Source.constant(min), Source.constant(max));
    }

    /**
     * Applies a Curve function to the output of this Noise
     *
     * @param func - the Curve function to apply to the output
     * @return a new Curve Noise
     */
    default Noise curve(CurveFunction func) {
        return new Curve(this, func);
    }

    /**
     * Applies a custom Curve function to the output of this Noise
     *
     * @param mid - the mid point of the curve
     * @param steepness - the steepness of the curve
     * @return a new Curve Noise
     */
    default Noise curve(double mid, double steepness) {
        return new Curve(this, new MidPointCurve((float) mid, (float) steepness));
    }

    default Noise freq(double x, double y) {
        return freq(Source.constant(x), Source.constant(y));
    }

    default Noise freq(Noise x, Noise y) {
        return new Freq(this, x, y);
    }

    /**
     * Applies a custom Curve function to the output of this Noise
     *
     * @param mid - the mid point of the curve
     * @param steepness - the steepness of the curve
     * @return a new Curve Noise
     */
    default Noise curve(Noise mid, Noise steepness) {
        return new VariableCurve(this, mid, steepness);
    }

    // TODO
    default Noise grad(double lower, double upper, double strength) {
        return grad(Source.constant(lower), Source.constant(upper), Source.constant(strength));
    }

    // TODO
    default Noise grad(Noise lower, Noise upper, Noise strength) {
        return new Grad(this, lower, upper, strength);
    }

    /**
     * Inverts the output of this Noise (0.1 becomes 0.9 for a standard Noise who's output is between 0 and 1)
     *
     * @return a new Invert Noise
     */
    default Noise invert() {
        return new Invert(this);
    }

    /**
     * Maps the output of this Noise so that it lies proportionally between the min and max values at a given coordinate
     *
     * @param min - the lower bound
     * @param max - the upper bound
     * @return a new Map Noise
     */
    default Noise map(Noise min, Noise max) {
        return new Map(this, min, max);
    }

    /**
     * Maps the output of this Noise so that it lies proportionally between the min and max values at a given coordinate
     *
     * @param min - the lower bound
     * @param max - the upper bound
     * @return a new Map Noise
     */
    default Noise map(double min, double max) {
        return map(Source.constant(min), Source.constant(max));
    }

    /**
     * Returns the highest value out of this and the other Noise's outputs
     *
     * @param other - the other Noise to use
     * @return a new Max Noise
     */
    default Noise max(Noise other) {
        return new Max(ImmutableList.of(this, other));
    }

    /**
     * Returns the lowest value out of this and the other Noise's outputs
     *
     * @param other - the other Noise to use
     * @return a new Min Noise
     */
    default Noise min(Noise other) {
        return new Min(ImmutableList.of(this, other));
    }

    /**
     * Modulates the coordinates before querying this Noise for an output
     *
     * @param direction - a Noise that controls the direction of the modulation
     * @param strength - a Noise that controls the strength of deviation in the given direction
     * @return a new Modulate Noise
     */
    default Noise mod(Noise direction, Noise strength) {
        return new Modulate(this, direction, strength);
    }

    /**
     * Multiplies the outputs of this and the other Noise
     *
     * @param other - the other Noise to multiply
     * @return a new Multiply Noise
     */
    default Noise mul(Noise other) {
        if (other.minValue() == 1F && other.maxValue() == 1F) {
            return this;
        }
        return new Mul(ImmutableList.of(this, other));
    }
    
    default Noise div(Noise other) {
        if (other.minValue() == 1F && other.maxValue() == 1F) {
            return this;
        }
        return new Div(ImmutableList.of(this, other));
    }

    default Noise blend(double blend, Noise... sources) {
        return blend(blend, Interpolation.LINEAR, sources);
    }

    default Noise blend(double blend, Interpolation interpolation, Noise... sources) {
        return new MultiBlend((float) blend, interpolation, this, ImmutableList.copyOf(sources));
    }

    default Noise pow(Noise n) {
        if (n.minValue() == 0 && n.maxValue() == 0) {
            return Source.ONE;
        }
        if (n.minValue() == 1 && n.maxValue() == 1) {
            return this;
        }
        return new Power(this, n);
    }

    default Noise pow(double n) {
        return pow(Source.constant(n));
    }

    default Noise powCurve(double n) {
        return new PowerCurve(this, (float) n);
    }

    default Noise scale(Noise scale) {
        if (scale.minValue() == 1 && scale.maxValue() == 1) {
            return this;
        }
        return new Scale(this, scale);
    }

    default Noise scale(double scale) {
        return scale(Source.constant(scale));
    }

    default Noise round() {
    	return new Round(this);
    }
    
    default Noise threshold(double threshold, double below, double above) {
    	return threshold(Source.constant(threshold), Source.constant(below), Source.constant(above));
    }

    default Noise threshold(double threshold, Noise below, Noise above) {
    	return threshold(Source.constant(threshold), below, above);
    }
    
    default Noise threshold(Noise threshold, Noise below, Noise above) {
    	return new Threshold(this, threshold, below, above);
    }
    
    default Noise select(Noise lower, Noise upper, double lowerBound, double upperBound, double falloff) {
        return select(lower, upper, lowerBound, upperBound, falloff, Interpolation.CURVE3);
    }

    default Noise select(Noise lower, Noise upper, double lowerBound, double upperBound, double falloff, Interpolation interpolation) {
        return new Select(this, lower, upper, (float) lowerBound, (float) upperBound, (float) falloff, interpolation);
    }

    default Noise steps(int steps) {
        return steps(steps, 0, 0);
    }

    default Noise steps(int steps, double slopeMin, double slopeMax) {
        return steps(steps, slopeMin, slopeMax, Interpolation.LINEAR);
    }

    default Noise steps(int steps, double slopeMin, double slopeMax, CurveFunction curveFunc) {
        return steps(Source.constant(steps), Source.constant(slopeMin), Source.constant(slopeMax), curveFunc);
    }

    default Noise steps(Noise steps, Noise slopeMin, Noise slopeMax) {
        return steps(steps, slopeMin, slopeMax, Interpolation.LINEAR);
    }

    default Noise steps(Noise steps, Noise slopeMin, Noise slopeMax, CurveFunction curveFunc) {
        return new Steps(this, steps, slopeMin, slopeMax, curveFunc);
    }

    default Noise sub(Noise other) {
        return new Sub(ImmutableList.of(this, other));
    }

    default Noise legacyTerrace(double lowerCurve, double upperCurve, int steps, double blendRange) {
        return legacyTerrace(Source.constant(lowerCurve), Source.constant(upperCurve), steps, blendRange);
    }

    default Noise legacyTerrace(Noise lowerCurve, Noise upperCurve, int steps, double blendRange) {
        return new LegacyTerrace(this, lowerCurve, upperCurve, steps, (float) blendRange);
    }

    default Noise terrace(double lowerCurve, double upperCurve, double lowerHeight, int steps, double blendRange) {
        return terrace(Source.constant(lowerCurve), Source.constant(upperCurve), Source.constant(lowerHeight), steps, blendRange);
    }

    default Noise terrace(Noise lowerCurve, Noise upperCurve, Noise lowerHeight, int steps, double blendRange) {
        return new Terrace(this, lowerCurve, upperCurve, lowerHeight, steps, (float) blendRange);
    }

    default Noise terrace(Noise modulation, double slope, double blendMin, double blendMax, int steps) {
        return terrace(modulation, Source.ONE, slope, blendMin, blendMax, steps);
    }

    default Noise terrace(Noise modulation, double slope, double blendMin, double blendMax, int steps, int octaves) {
        return terrace(modulation, Source.ONE, Source.constant(slope), blendMin, blendMax, steps, octaves);
    }

    default Noise terrace(Noise modulation, Noise mask, double slope, double blendMin, double blendMax, int steps) {
        return terrace(modulation, mask, Source.constant(slope), blendMin, blendMax, steps, 1);
    }

    default Noise terrace(Noise modulation, Noise mask, Noise slope, double blendMin, double blendMax, int steps) {
        return new AdvancedTerrace(this, modulation, mask, slope, (float) blendMin, (float) blendMax, steps, 1);
    }

    default Noise terrace(Noise modulation, Noise mask, Noise slope, double blendMin, double blendMax, int steps, int octaves) {
        return new AdvancedTerrace(this, modulation, mask, slope, (float) blendMin, (float) blendMax, steps, octaves);
    }

    default Noise warp(final Domain domain) {
        return new Warp(this, domain);
    }
    
    default Noise warp(final Noise warpX, final Noise warpZ, final double power) {
        return this.warp(warpX, warpZ, Source.constant(power));
    }
    
    default Noise warp(final Noise warpX, final Noise warpZ, final Noise power) {
        return this.warp(Domain.warp(warpX, warpZ, power));
    }
    
    default Noise warp(final int seed, final int scale, final int octaves, final double power) {
        return this.warp(Source.PERLIN, seed, scale, octaves, power);
    }
    
    default Noise warp(final Source source, final int seed, final int scale, final int octaves, final double power) {
        final Noise x = Source.build(seed, scale, octaves).build(source);
        final Noise y = Source.build(seed + 1, scale, octaves).build(source);
        final Noise p = Source.constant(power);
        return this.warp(x, y, p);
    }
    
    default Noise shift(int offset) {
    	return new Shift(this, offset);
    }
    
    default Noise negate() {
    	return new Negate(this);
    }
}
