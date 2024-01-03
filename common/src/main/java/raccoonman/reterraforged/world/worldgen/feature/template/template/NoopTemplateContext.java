package raccoonman.reterraforged.world.worldgen.feature.template.template;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record NoopTemplateContext() implements TemplateContext {

	@Override
	public void recordState(BlockPos pos, BlockState state) {
	}
}
