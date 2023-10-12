package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record YScale(DensityFunction height, int yOffset, int scaler, double unit) implements DensityFunction.SimpleFunction {
	public static final Codec<YScale> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("height").forGetter(YScale::height),
		Codec.INT.fieldOf("y_offset").forGetter(YScale::yOffset),
		Codec.INT.fieldOf("scaler").forGetter(YScale::scaler),
		Codec.DOUBLE.fieldOf("unit").forGetter(YScale::unit)
	).apply(instance, YScale::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		return (this.height.compute(ctx) * this.scaler + this.yOffset) * this.unit;
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new YScale(this.height.mapAll(visitor), this.yOffset, this.scaler, this.unit));
	}

	@Override
	public double minValue() {
		return -this.scaler * this.unit;
	}

	@Override
	public double maxValue() {
		return this.scaler * this.unit;
	}

	@Override
	public KeyDispatchDataCodec<YScale> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}
