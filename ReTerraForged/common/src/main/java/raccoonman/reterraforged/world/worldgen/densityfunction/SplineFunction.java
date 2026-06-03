package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

// works the same as the vanilla spline but can take DensityFunctions as values
public record SplineFunction(DensityFunction input, float[] locations, List<DensityFunction> values, float[] derivatives, double minValue, double maxValue) implements DensityFunction.SimpleFunction {
	public static final MapCodec<SplineFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(SplineFunction::input),
    	ExtraCodecs.nonEmptyList(Point.CODEC.listOf()).fieldOf("points").forGetter(multipoint -> IntStream.range(0, multipoint.locations.length).mapToObj(i -> new Point(multipoint.locations()[i], multipoint.values().get(i), multipoint.derivatives()[i])).toList())
	).apply(instance, (input, points) -> {
		float[] fs = new float[points.size()];
		ImmutableList.Builder<DensityFunction> builder = ImmutableList.builder();
		float[] gs = new float[points.size()];
		for (int i = 0; i < points.size(); ++i) {
			Point lv = points.get(i);
			fs[i] = lv.location();
			builder.add(lv.value());
			gs[i] = lv.derivative();
    	}
		return create(input, fs, builder.build(), gs);
    }));
    
	@Override
	public boolean equals(Object o) {
		return o instanceof SplineFunction other && this.input.equals(other.input) && Arrays.equals(this.locations, other.locations) && this.values.equals(other.values) && Arrays.equals(this.derivatives, other.derivatives) && this.minValue == other.minValue && this.maxValue == other.maxValue;
	}
	
	record Point(float location, DensityFunction value, float derivative) {
		public static final Codec<Point> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("location").forGetter(Point::location), 
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("value").forGetter(Point::value), 
			Codec.FLOAT.fieldOf("derivative").forGetter(Point::derivative)
		).apply(instance, Point::new));
	}
        
	static SplineFunction create(DensityFunction input, float[] points, List<DensityFunction> values, float[] derivatives) {
		validateSizes(points, values, derivatives);
		int minIndex = points.length - 1;
		double pointMin = Double.POSITIVE_INFINITY;
		double pointMax = Double.NEGATIVE_INFINITY;
		double inputMin = input.minValue();
		double inputMax = input.maxValue();
		double l;
		double k;
		if (inputMin < points[0]) {
			k = linearExtend(inputMin, points, values.get(0).minValue(), derivatives, 0);
			l = linearExtend(inputMin, points, values.get(0).maxValue(), derivatives, 0);
			pointMin = Math.min(pointMin, Math.min(k, l));
			pointMax = Math.max(pointMax, Math.max(k, l));
		}
		if (inputMax > points[minIndex]) {
			k = linearExtend(inputMax, points, values.get(minIndex).minValue(), derivatives, minIndex);
			l = linearExtend(inputMax, points, values.get(minIndex).maxValue(), derivatives, minIndex);
			pointMin = Math.min(pointMin, Math.min(k, l));
			pointMax = Math.max(pointMax, Math.max(k, l));
		}
		for (DensityFunction cubicSpline : values) {
			pointMin = Math.min(pointMin, cubicSpline.minValue());
			pointMax = Math.max(pointMax, cubicSpline.maxValue());
		}
		for (int m = 0; m < minIndex; ++m) {
			l = points[m];
			double n = points[m + 1];
			double o = n - l;
			DensityFunction cubicSpline2 = values.get(m);
			DensityFunction cubicSpline3 = values.get(m + 1);
			double p = cubicSpline2.minValue();
			double q = cubicSpline2.maxValue();
			double r = cubicSpline3.minValue();
			double s = cubicSpline3.maxValue();
			float d0 = derivatives[m];
			float d1 = derivatives[m + 1];
			if (d0 == 0.0F && d1 == 0.0F) continue;
			double v = d0 * o;
			double w = d1 * o;
			double x = Math.min(p, r);
			double y = Math.max(q, s);
			double z = v - s + p;
			double aa = v - r + q;
			double ab = -w + r - q;
			double ac = -w + s - p;
			double ad = Math.min(z, ab);
			double ae = Math.max(aa, ac);
			pointMin = Math.min(pointMin, x + 0.25F * ad);
			pointMax = Math.max(pointMax, y + 0.25F * ae);
		}
		return new SplineFunction(input, points, values, derivatives, pointMin, pointMax);
	}

	private static double linearExtend(double f, float[] fs, double g, float[] gs, int i) {
		float h = gs[i];
		if (h == 0.0F) {
			return g;
		}
		return g + h * (f - fs[i]);
	}

	private static <I extends DensityFunction> void validateSizes(float[] fs, List<DensityFunction> list, float[] gs) {
		if (fs.length != list.size() || fs.length != gs.length) {
			throw new IllegalArgumentException("All lengths must be equal, got: " + fs.length + " " + list.size() + " " + gs.length);
		}
		if (fs.length == 0) {
			throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
		}
	}

	@Override
	public double compute(FunctionContext ctx) {
		double input = this.input.compute(ctx);
		int inputIndex = findIntervalStart(this.locations, input);
		int lastIndex = this.locations.length - 1;
		if (inputIndex < 0) {
			return linearExtend(input, this.locations, this.values.get(0).compute(ctx), this.derivatives, 0);
		}
		if (inputIndex == lastIndex) {
			return linearExtend(input, this.locations, this.values.get(lastIndex).compute(ctx), this.derivatives, lastIndex);
		}
		float l1 = this.locations[inputIndex];
		float l2 = this.locations[inputIndex + 1];
		double lerpAmount = (input - l1) / (l2 - l1);
		DensityFunction p1 = this.values.get(inputIndex);
		DensityFunction p2 = this.values.get(inputIndex + 1);
		float d1 = this.derivatives[inputIndex];
		float d2 = this.derivatives[inputIndex + 1];
		double fromValue = p1.compute(ctx);
		double toValue = p2.compute(ctx);
		double from = d1 * (l2 - l1) - (toValue - fromValue);
		double to = -d2 * (l2 - l1) + (toValue - fromValue);
		return Mth.lerp(lerpAmount, fromValue, toValue) + lerpAmount * (1.0F - lerpAmount) * Mth.lerp(lerpAmount, from, to);
	}
	
	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return create(this.input.mapAll(visitor), this.locations, this.values.stream().map(DensityFunction -> DensityFunction.mapAll(visitor)).toList(), this.derivatives);
	}

	@Override
	public KeyDispatchDataCodec<SplineFunction> codec() {
		return KeyDispatchDataCodec.of(CODEC);
	}
	
	private static int findIntervalStart(float[] fs, double f) {
		return Mth.binarySearch(0, fs.length, i -> f < fs[i]) - 1;
	}

	public static Builder builder(DensityFunction densityFunction) {
		return new Builder(densityFunction);
	}
	
	public static class Builder {
		private final DensityFunction coordinate;
		private final FloatList points = new FloatArrayList();
		private final List<DensityFunction> values = Lists.newArrayList();
		private final FloatList derivatives = new FloatArrayList();

		protected Builder(DensityFunction coordinate) {
			this.coordinate = coordinate;
		}

		public Builder addPoint(float point, double value) {
			return this.addPoint(point, DensityFunctions.constant(value), 0.0F);
		}

		public Builder addPoint(float point, double value, float derivative) {
			return this.addPoint(point, DensityFunctions.constant(value), derivative);
		}

		public Builder addPoint(float point, DensityFunction value) {
			return this.addPoint(point, value, 0.0F);
		}

		public Builder addPoint(float point, DensityFunction value, float derivative) {
			if (!this.points.isEmpty() && point < this.points.getFloat(this.points.size() - 1)) {
				throw new IllegalArgumentException("Please register points in ascending order");
			}
			this.points.add(point);
			this.values.add(value);
			this.derivatives.add(derivative);
			return this;
		}

		public DensityFunction build() {
			if (this.points.isEmpty()) {
				throw new IllegalStateException("No elements added");
			}
			return create(this.coordinate, this.points.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
		}
	}
}
