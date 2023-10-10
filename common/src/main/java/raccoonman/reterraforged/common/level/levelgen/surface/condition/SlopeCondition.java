package raccoonman.reterraforged.common.level.levelgen.surface.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.LazyXZCondition;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;

public record SlopeCondition(Holder<DensityFunction> height, double threshold, float scaler, int radius) implements SurfaceRules.ConditionSource {
	public static final Codec<SlopeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("height").forGetter(SlopeCondition::height),
		Codec.DOUBLE.fieldOf("threshold").forGetter(SlopeCondition::threshold),
		Codec.FLOAT.fieldOf("scaler").forGetter(SlopeCondition::scaler),
		Codec.INT.fieldOf("radius").forGetter(SlopeCondition::radius)
	).apply(instance, SlopeCondition::new));

	@Override
	public Condition apply(Context ctx) {
		if((Object) ctx.randomState instanceof RandomStateExtension randomStateExt && (Object) ctx instanceof ContextExtension surfaceContextExt) {
			return new Condition(ctx, ctx.noiseChunk.wrap(randomStateExt.wrap(this.height.value())));
		} else {
			throw new IllegalStateException();
		}
	}
	
	@Override
	public KeyDispatchDataCodec<SlopeCondition> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	//TODO cache the slope value in the surface context maybe?
	private class Condition extends LazyXZCondition {
		private Context surfaceContext;
		private DensityFunction function;
		private MutableFunctionContext functionContext;
		
		public Condition(Context surfaceContext, DensityFunction function) {
			super(surfaceContext);
			this.surfaceContext = surfaceContext;
			this.function = function;
			this.functionContext = new MutableFunctionContext();
		}
		
		@Override
		protected boolean compute() {
			int startX = this.surfaceContext.blockX;
			int startZ = this.surfaceContext.blockZ;
			double heightDelta = 0.0D;
	        double height = this.function.compute(this.functionContext.at(startX, this.surfaceContext.blockY, startZ));
	        for (int dz = -1; dz <= 2; ++dz) {
	            for (int dx = -1; dx <= 2; ++dx) {
	                if (dx != 0 || dz != 0) {
	                    int x = startX + dx * SlopeCondition.this.radius;
	                    int z = startZ + dz * SlopeCondition.this.radius;
	                    heightDelta += Math.abs(height - this.function.compute(this.functionContext.at(x, this.surfaceContext.blockY, z))) / SlopeCondition.this.radius;
	                }
	            }
	        }
	        return Math.min(1.0D, heightDelta * SlopeCondition.this.scaler) > SlopeCondition.this.threshold;
		}
	}
}
