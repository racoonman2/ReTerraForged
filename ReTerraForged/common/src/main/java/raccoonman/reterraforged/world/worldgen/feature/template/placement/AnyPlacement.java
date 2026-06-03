package raccoonman.reterraforged.world.worldgen.feature.template.placement;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import raccoonman.reterraforged.world.worldgen.feature.BlockUtils;
import raccoonman.reterraforged.world.worldgen.feature.template.Dimensions;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateContext;

record AnyPlacement() implements TemplatePlacement<AnyPlacement.Context> {
	public static final MapCodec<AnyPlacement> CODEC = MapCodec.unit(AnyPlacement::new);
	private static final Context CONTEXT = new Context();
	
	@Override
	public boolean canPlaceAt(LevelAccessor world, BlockPos pos, Dimensions dimensions) {
		return true;
	}

	@Override
	public boolean canReplaceAt(LevelAccessor world, BlockPos pos) {
		return !BlockUtils.isSolid(world, pos);
	}

	@Override
	public Context createContext() {
		return CONTEXT;
	}

	@Override
	public MapCodec<AnyPlacement> codec() {
		return CODEC;
	}
	
	public record Context() implements TemplateContext {
		
		@Override
		public void recordState(BlockPos pos, BlockState state) {
		}
	}
}
