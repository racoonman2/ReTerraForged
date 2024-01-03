package raccoonman.reterraforged.world.worldgen.feature.template.decorator;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import raccoonman.reterraforged.world.worldgen.feature.template.template.TemplateContext;

public class TreeContext implements TemplateContext {
	private Set<BlockPos> logs;
	private Set<BlockPos> leaves;

	public Set<BlockPos> logs() {
		if (this.logs != null) {
			return this.logs;
		}
		return ImmutableSet.of();
	}

	public Set<BlockPos> leaves() {
		if (this.leaves != null) {
			return this.leaves;
		}
		return ImmutableSet.of();
	}

	@Override
	public void recordState(BlockPos pos, BlockState state) {
		if (state.is(BlockTags.LEAVES)) {
			this.leaves = safeAdd(this.leaves, pos);
			return;
		}

		if (state.is(BlockTags.LOGS)) {
			this.logs = safeAdd(this.logs, pos);
		}
	}

	private static Set<BlockPos> safeAdd(Set<BlockPos> list, BlockPos pos) {
		if (list == null) {
			list = new HashSet<>();
		}
		list.add(pos.immutable());
		return list;
	}
}