package raccoonman.reterraforged.common.level.levelgen.surface.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.LazyXZCondition;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;

// maybe cache this along the xz axis?
public record DensityThresholdCondition(Holder<DensityFunction> function, double threshold) implements SurfaceRules.ConditionSource {
	public static final Codec<DensityThresholdCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("function").forGetter(DensityThresholdCondition::function),
		Codec.DOUBLE.fieldOf("threshold").forGetter(DensityThresholdCondition::threshold)
	).apply(instance, DensityThresholdCondition::new));
	
	@Override
	public Condition apply(Context surfaceContext) {
		if((Object) surfaceContext.randomState instanceof RandomStateExtension randomStateExt) {		
			DensityFunction function = randomStateExt.cache(this.function.value(), surfaceContext.noiseChunk);
			return new Condition(surfaceContext, function);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<DensityThresholdCondition> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	private class Condition extends LazyXZCondition {
		private Context surfaceContext;
		private DensityFunction function;
		private MutableFunctionContext context;
		
		public Condition(Context surfaceContext, DensityFunction function) {
			this(surfaceContext, function, new MutableFunctionContext());
		}
		
		public Condition(Context surfaceContext, DensityFunction function, MutableFunctionContext context) {
			super(surfaceContext);
			this.surfaceContext = surfaceContext;
			this.function = function;
			this.context = context;
		}
		
		@Override
		protected boolean compute() {
			this.context.blockX = this.surfaceContext.blockX;
			this.context.blockY = this.surfaceContext.blockY;
			this.context.blockZ = this.surfaceContext.blockZ;
			return this.function.compute(this.context) > DensityThresholdCondition.this.threshold;
		}
	}
}
