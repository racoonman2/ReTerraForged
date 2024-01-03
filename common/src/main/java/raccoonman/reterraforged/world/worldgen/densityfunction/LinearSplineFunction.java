package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public record LinearSplineFunction(DensityFunction input, List<Pair<Double, DensityFunction>> points, double minValue, double maxValue) implements DensityFunction {
	public static final Codec<LinearSplineFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(LinearSplineFunction::input),
		ExtraCodecs.nonEmptyList(Codec.pair(Codec.DOUBLE, DensityFunction.HOLDER_HELPER_CODEC).listOf()).fieldOf("points").forGetter(LinearSplineFunction::points)
	).apply(instance, LinearSplineFunction::new));
	
	public LinearSplineFunction(DensityFunction input, List<Pair<Double, DensityFunction>> points) {
		this(input, points, min(points), max(points));
	}
	
	@Override
	public double compute(FunctionContext ctx) {
		double input = this.input.compute(ctx);
		int pointCount = this.points.size();
		
		Pair<Double, DensityFunction> first = this.points.get(0);
		if(input <= first.getFirst()) {
			return first.getSecond().compute(ctx);
		}

		Pair<Double, DensityFunction> last = this.points.get(pointCount - 1);
		if(input >= last.getFirst()) {
			return last.getSecond().compute(ctx);
		}
		
		int index = Mth.binarySearch(0, pointCount, i -> input < this.points.get(i).getFirst()) - 1;
		Pair<Double, DensityFunction> start = this.points.get(index);
		Pair<Double, DensityFunction> end = this.points.get(index + 1);
		double min = start.getFirst();
		double max = end.getFirst();
		double from = start.getSecond().compute(ctx);
		double to = end.getSecond().compute(ctx);
		
		double lerp = NoiseUtil.map(input, 0.0D, 1.0D, min, max);
		lerp = NoiseUtil.clamp(lerp, 0.0D, 1.0D);
		return NoiseUtil.lerp(from, to, lerp);
	}

	@Override
	public void fillArray(double[] array, ContextProvider ctxProvider) {
		ctxProvider.fillAllDirectly(array, this);
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new LinearSplineFunction(this.input.mapAll(visitor), this.points.stream().map((point) -> {
			return Pair.of(point.getFirst(), visitor.apply(point.getSecond()));
		}).toList()));
	}

	@Override
	public KeyDispatchDataCodec<LinearSplineFunction> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}

	public static LinearSplineFunction.Builder builder(DensityFunction input) {
		return new LinearSplineFunction.Builder(input);
	}
	
	private static float min(List<Pair<Double, DensityFunction>> points) {
		return (float) points.stream().map(Pair::getSecond).mapToDouble(DensityFunction::minValue).min().orElseThrow();
	}
	
	private static float max(List<Pair<Double, DensityFunction>> points) {
		return (float) points.stream().map(Pair::getSecond).mapToDouble(DensityFunction::maxValue).max().orElseThrow();
	}
	
	public static class Builder {
		private DensityFunction input;
		private List<Pair<Double, DensityFunction>> points;
		
		public Builder(DensityFunction input) {
			this.input = input;
			this.points = new ArrayList<>();
		}
		
		public Builder addPoint(double point, double value) {
			return this.addPoint(point, DensityFunctions.constant(value));
		}

		public Builder addPoint(double point, DensityFunction value) {
			this.points.add(Pair.of(point, value));
			return this;
		}
		
		public LinearSplineFunction build() {
			return new LinearSplineFunction(this.input, ImmutableList.copyOf(this.points));
		}
	}
}
