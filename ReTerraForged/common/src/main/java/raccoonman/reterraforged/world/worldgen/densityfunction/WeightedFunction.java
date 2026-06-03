package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public class WeightedFunction implements MappedFunction, SimpleFunction {
	private DensityFunction input;
	private DensityFunction[] entries;
	
	public WeightedFunction(DensityFunction input, DensityFunction[] entries) {
		this.input = input;
		this.entries = entries;
	}
	
	@Override
	public double compute(FunctionContext ctx) {
		double input = this.input.compute(ctx);
		int index = NoiseUtil.round(input * (this.entries.length - 1));
		return this.entries[index].compute(ctx);
	}

	@Override
	public DensityFunction mapAll(DensityFunction.Visitor visitor) {
		return visitor.apply(new WeightedFunction(this.input.mapAll(visitor), Arrays.stream(this.entries).map((entry) -> entry.mapAll(visitor)).toArray(DensityFunction[]::new)));
	}

	@Override
	public double minValue() {
		return 0.0F;
	}

	@Override
	public double maxValue() {
		return 1.0F;
	}
	
	public record Marker(DensityFunction input, List<WeightedFunction.Entry> entries) implements MappedFunction.Marker {
		public static final MapCodec<WeightedFunction.Marker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(WeightedFunction.Marker::input),
			WeightedFunction.Entry.CODEC.listOf().fieldOf("entries").forGetter(WeightedFunction.Marker::entries)
		).apply(instance, WeightedFunction.Marker::new));

		public DensityFunction[] bake() {
			float smallest = Float.MAX_VALUE;
			for(WeightedFunction.Entry entry : this.entries) {
				float weight = entry.weight();
				if(weight == 0.0F) {
					continue;
				}
				smallest = Math.min(smallest, weight);
			}
			
			if(smallest == Float.MAX_VALUE) {
				return this.entries.stream().map(WeightedFunction.Entry::function).toArray(DensityFunction[]::new);
			}
			
			List<DensityFunction> result = new LinkedList<>();
			for(WeightedFunction.Entry entry : this.entries) {
				float weight = entry.weight();
				if(weight == 0.0F) {
					continue;
				}
				
				int count = Math.round(weight / smallest);
				DensityFunction function = entry.function();
				while(count-- > 0) {
					result.add(function);
				}
			}
			if(result.isEmpty()) {
				return this.entries.stream().map(WeightedFunction.Entry::function).toArray(DensityFunction[]::new);
			}
			return result.toArray(DensityFunction[]::new);
		}
		
		@Override
		public DensityFunction mapAll(DensityFunction.Visitor visitor) {
			return visitor.apply(new WeightedFunction.Marker(this.input.mapAll(visitor), this.entries.stream().map((entry) -> entry.mapAll(visitor)).toList()));
		}
		
		@Override
		public KeyDispatchDataCodec<WeightedFunction.Marker> codec() {
			return KeyDispatchDataCodec.of(CODEC);
		}
	}
	
	public record Entry(float weight, DensityFunction function) {
		public static final Codec<WeightedFunction.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("weight").forGetter(WeightedFunction.Entry::weight),
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(WeightedFunction.Entry::function)
		).apply(instance, WeightedFunction.Entry::new));
		
		public WeightedFunction.Entry mapAll(DensityFunction.Visitor visitor) {
			return new WeightedFunction.Entry(this.weight, this.function.mapAll(visitor));
		}
	}
}
