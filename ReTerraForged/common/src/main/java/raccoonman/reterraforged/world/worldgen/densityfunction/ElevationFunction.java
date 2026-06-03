package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;

public class ElevationFunction implements MappedFunction, SimpleFunction {
	private float scaledBase;
	private float scaler;
	
	public ElevationFunction(float base, float scaler) {
		this.scaledBase = base / scaler;
		this.scaler = scaler;
	}
	
	@Override
	public double compute(FunctionContext ctx) {
		float value = (float) ctx.blockY() / this.scaler;
        if (value <= this.scaledBase) {
            return 0.0D;
        }
        return (value - this.scaledBase) / (1.0D - this.scaledBase);
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}
	
	public record Marker(float scaler) implements MappedFunction.Marker {
		public static final MapCodec<ElevationFunction.Marker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("scaler").forGetter(ElevationFunction.Marker::scaler)
		).apply(instance, ElevationFunction.Marker::new));

		@Override
		public DensityFunction mapAll(DensityFunction.Visitor visitor) {
			return visitor.apply(this);
		}

		@Override
		public KeyDispatchDataCodec<ElevationFunction.Marker> codec() {
			return KeyDispatchDataCodec.of(CODEC);
		}
	}
}
