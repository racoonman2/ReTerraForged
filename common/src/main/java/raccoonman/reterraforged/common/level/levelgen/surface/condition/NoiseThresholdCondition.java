package raccoonman.reterraforged.common.level.levelgen.surface.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.LazyXZCondition;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record NoiseThresholdCondition(Noise noise, float threshold, Comparison comparison) implements SurfaceRules.ConditionSource {
	public static final Codec<NoiseThresholdCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("noise").forGetter(NoiseThresholdCondition::noise),
		Codec.FLOAT.fieldOf("threshold").forGetter(NoiseThresholdCondition::threshold),
		Comparison.CODEC.fieldOf("comparison").forGetter(NoiseThresholdCondition::comparison)
	).apply(instance, NoiseThresholdCondition::new));
	
	@Override
	public Condition apply(Context ctx) {
		if((Object) ctx.randomState instanceof RandomStateExtension randomExt) {
			return new Condition(randomExt.shift(this.noise), ctx);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<NoiseThresholdCondition> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	private class Condition extends LazyXZCondition {
		private Noise noise;
		private Context surfaceContext;
		
		public Condition(Noise noise, Context surfaceContext) {
			super(surfaceContext);
			this.noise = noise;
			this.surfaceContext = surfaceContext;
		}

		@Override
		protected boolean compute() {
			//note: the noise gets shifted by the level seed so we can pass 0 here to the seed offset
			return NoiseThresholdCondition.this.comparison.compare(this.noise.compute(this.surfaceContext.blockX, this.surfaceContext.blockZ, 0), NoiseThresholdCondition.this.threshold);
		}
	}
	
	public enum Comparison implements StringRepresentable {
		LESS_THAN {
			
			@Override
			boolean compare(float input, float threshold) {
				return input < threshold;
			}
		},
		GREATER_THAN {
			
			@Override
			boolean compare(float input, float threshold) {
				return input > threshold;
			}
		},
		GREATER_THAN_EQ {
			
			@Override
			boolean compare(float input, float threshold) {
				return input >= threshold;
			}
		},
		LESS_THAN_EQ {
			
			@Override
			boolean compare(float input, float threshold) {
				return input <= threshold;
			}
		};
		
		public static final Codec<Comparison> CODEC = StringRepresentable.fromEnum(Comparison::values);
		
		abstract boolean compare(float input, float threshold);

		@Override
		public String getSerializedName() {
			return this.name();
		}
	}
}
