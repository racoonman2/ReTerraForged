package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;
import raccoonman.reterraforged.world.worldgen.noise.Noise;

@Deprecated(forRemoval = true)
public record NoiseFunction(Noise noise, int seed) implements MappedFunction, SimpleFunction {

	@Override
	public double compute(FunctionContext ctx) {
		return this.noise.compute(ctx.blockX(), ctx.blockZ(), this.seed);
	}

	@Override
	public double minValue() {
		return this.noise.minValue();
	}

	@Override
	public double maxValue() {
		return this.noise.maxValue();
	}
	
	public record Marker(Holder<Noise> noise) implements MappedFunction.Marker {
		public static final MapCodec<NoiseFunction.Marker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Noise.CODEC.fieldOf("noise").forGetter(NoiseFunction.Marker::noise)
		).apply(instance, NoiseFunction.Marker::new));

		@Override
		public KeyDispatchDataCodec<NoiseFunction.Marker> codec() {
			return KeyDispatchDataCodec.of(CODEC);
		}

		@Override
		public DensityFunction mapAll(DensityFunction.Visitor visitor) {
			return visitor.apply(visitor instanceof Noise.Visitor noiseVisitor ? new Marker(Holder.direct(this.noise.value().mapAll(noiseVisitor))) : this);
		}
	}
}
