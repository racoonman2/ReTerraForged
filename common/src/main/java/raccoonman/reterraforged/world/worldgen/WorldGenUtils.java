package raccoonman.reterraforged.world.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;

public class WorldGenUtils {

	public static BlockColumn createColumn(ChunkAccess chunk, BlockPos.MutableBlockPos cursor) {
		return new BlockColumn() {

            @Override
            public BlockState getBlock(int y) {
                return chunk.getBlockState(cursor.setY(y));
            }

            @Override
            public void setBlock(int y, BlockState state) {
                LevelHeightAccessor levelHeightAccessor = chunk.getHeightAccessorForGeneration();
                if (y >= levelHeightAccessor.getMinBuildHeight() && y < levelHeightAccessor.getMaxBuildHeight()) {
                    chunk.setBlockState(cursor.setY(y), state, false);
                }
            }

            @Override
            public String toString() {
                return "ChunkBlockColumn " + chunk.getPos();
            }
        };
	}
}
