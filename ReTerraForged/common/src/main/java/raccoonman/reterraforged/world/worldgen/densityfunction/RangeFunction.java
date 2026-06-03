package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record RangeFunction(DensityFunction input, double from, double to, boolean exclusive) implements DensityFunction.SimpleFunction {
	public static final MapCodec<RangeFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(RangeFunction::input),
		Codec.DOUBLE.fieldOf("from").forGetter(RangeFunction::from),
		Codec.DOUBLE.fieldOf("to").forGetter(RangeFunction::to),
		Codec.BOOL.fieldOf("exclusive").forGetter(RangeFunction::exclusive)
	).apply(instance, RangeFunction::new));
	
	@Override
	public double compute(FunctionContext context) {
		double value = this.input.compute(context);
		
		double max = this.exclusive ? 0.0D : 1.0D;
		double range = Math.abs(max - this.from);
        if (this.from < this.to) {
            if (value <= this.from) {
                return 0.0D;
            }
            if (value >= this.to) {
                return max;
            }
            return (value - this.from) / range;
        } else if (this.from > this.to) {
            if (value <= this.to) {
                return max;
            }
            if (value >= this.from) {
                return 0.0D;
            }
            return 1.0D - ((value - this.to) / range);
        }
        return 0.0D;
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new RangeFunction(this.input.mapAll(visitor), this.from, this.to, this.exclusive));
	}

	@Override
	public KeyDispatchDataCodec<RangeFunction> codec() {
		return KeyDispatchDataCodec.of(CODEC);
	}
}
