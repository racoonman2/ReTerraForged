package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

//TODO don't hardcode min/max values
public record Ridges(DensityFunction source) implements DensityFunction.SimpleFunction {
	public static final Codec<Ridges> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(Ridges::source)
	).apply(instance, Ridges::new));
	

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(new Ridges(this.source.mapAll(visitor)));
    }
	
	@Override
	public double compute(FunctionContext ctx) {
		return 1.0D - NoiseUtil.map((float) this.source.compute(ctx), -1.0F, 1.0F);
	}

	@Override
	public double minValue() {
		return -1.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}

	@Override
	public KeyDispatchDataCodec<Ridges> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}
