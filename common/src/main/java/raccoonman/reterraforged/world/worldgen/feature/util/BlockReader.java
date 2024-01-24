package raccoonman.reterraforged.world.worldgen.feature.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

@Deprecated(forRemoval = true)
public class BlockReader implements BlockGetter {
	private BlockState state;
	
	public BlockReader setState(BlockState state) {
		this.state = state;
		return this;
	}
	
	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getMinBuildHeight() {
		return 0;
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos var1) {
        return null;
	}

	@Override
	public BlockState getBlockState(BlockPos var1) {
		return this.state;
	}

	@Override
	public FluidState getFluidState(BlockPos var1) {
		return Fluids.EMPTY.defaultFluidState();
	}
}
