package raccoonman.reterraforged.world.worldgen.feature.template.decorator;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateContext;

public record TreeContext(Set<BlockPos> logs, Set<BlockPos> leaves) implements TemplateContext {

	public TreeContext() {
		this(new HashSet<>(), new HashSet<>());
	}
	
	@Override
	public void recordState(BlockPos pos, BlockState state) {
		if (state.is(BlockTags.LOGS)) {
			this.logs.add(pos);
			return;
		}
		
		if (state.is(BlockTags.LEAVES)) {
			this.leaves.add(pos);
			return;
		}
	}
}