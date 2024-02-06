package raccoonman.reterraforged.world.worldgen.surface.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

class SteepnessCondition extends ThresholdCondition {
	
	public SteepnessCondition(Context context, Noise threshold, Noise variance) {
		super(context, threshold, variance);
	}

	@Override
	protected float sample(Cell cell) {
		return cell.gradient;
	}
	
	public record Source(Holder<Noise> threshold, Holder<Noise> variance) implements SurfaceRules.ConditionSource {
		public static final Codec<Source> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Noise.CODEC.fieldOf("threshold").forGetter(Source::threshold),
			Noise.CODEC.fieldOf("variance").forGetter(Source::variance)
		).apply(instance, Source::new));

		@Override
		public SteepnessCondition apply(Context ctx) {
			return new SteepnessCondition(ctx, this.threshold.value(), this.variance.value());
		}

		@Override
		public KeyDispatchDataCodec<Source> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
