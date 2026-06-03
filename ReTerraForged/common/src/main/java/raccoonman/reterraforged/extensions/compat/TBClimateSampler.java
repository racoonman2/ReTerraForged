package raccoonman.reterraforged.extensions.compat;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

@Deprecated
public interface TBClimateSampler {
	void setUniqueness(DensityFunction function);
	
	@Nullable
	DensityFunction getUniqueness();
	
	public static void sample(Climate.Sampler sampler, Climate.TargetPoint point, FunctionContext ctx) {
		if((Object) sampler instanceof TBClimateSampler tbClimateSampler && (Object) point instanceof TBTargetPoint tbPoint) {
			DensityFunction uniqueness = tbClimateSampler.getUniqueness();
			if(uniqueness != null) {
				tbPoint.setUniqueness(uniqueness.compute(ctx));
			}
		}
	}
	
	public static void copy(Climate.Sampler source, Climate.Sampler dest) {
		if((Object) source instanceof TBClimateSampler tbSource && (Object) dest instanceof TBClimateSampler tbDest) {
			tbDest.setUniqueness(tbSource.getUniqueness());
		}
	}
}