package raccoonman.reterraforged.world.worldgen.surface.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public class NoiseCondition extends SurfaceRules.LazyXZCondition {
	private Noise noise;
	private float threshold;
	
	private NoiseCondition(Context context, Noise noise, float threshold) {
		super(context);
		
		this.noise = noise;
		this.threshold = threshold;
	}

	@Override
	protected boolean compute() {
		return this.noise.compute(this.context.blockX, this.context.blockZ, 0) > this.threshold;
	}
	
	public record Source(Holder<Noise> noise, float threshold) implements SurfaceRules.ConditionSource {
		public static final Codec<Source> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Noise.CODEC.fieldOf("noise").forGetter(Source::noise),
			Codec.FLOAT.fieldOf("threshold").forGetter(Source::threshold)
		).apply(instance, Source::new));
		
		@Override
		public NoiseCondition apply(Context ctx) {
			return new NoiseCondition(ctx, this.noise.value(), this.threshold);
		}

		@Override
		public KeyDispatchDataCodec<Source> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
