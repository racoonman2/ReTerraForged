package raccoonman.reterraforged.world.worldgen.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFRandomState;

class DensityFilter extends PlacementFilter {
	public static final MapCodec<DensityFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("density_function").forGetter((filter) -> filter.function),
		Codec.DOUBLE.fieldOf("threshold").forGetter((filter) -> filter.threshold)
	).apply(instance, DensityFilter::new));

	private Holder<DensityFunction> function;
	private double threshold;
	
	public DensityFilter(Holder<DensityFunction> function, double threshold) {
		this.function = function;
		this.threshold = threshold;
	}
	
	@Override
	protected boolean shouldPlace(PlacementContext placementCtx, RandomSource rand, BlockPos pos) {
		RandomState randomState = placementCtx.getLevel().getLevel().getChunkSource().randomState();
		RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);
		
		DensityFunction function = this.function.value();
		function = function.mapAll(rtfRandomState.globalFunctionVisitor());

		int blockX = pos.getX();
		int blockZ = pos.getZ();
		FunctionContext functionCtx = new DensityFunction.SinglePointContext(blockX, 0, blockZ);
		return function.compute(functionCtx) > this.threshold;
	}
	
	@Override
	public PlacementModifierType<DensityFilter> type() {
		return RTFPlacementModifiers.DENSITY_FILTER;
	}
}
