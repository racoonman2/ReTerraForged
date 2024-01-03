package raccoonman.reterraforged.world.worldgen.feature.template;

import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class BlockUtils {

    public static boolean isSoil(LevelAccessor world, BlockPos pos) {
        return TreeFeature.isDirt(world.getBlockState(pos));
    }

    public static boolean isLeavesOrLogs(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
    }

    public static boolean isVegetation(LevelAccessor world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.is(BlockTags.SAPLINGS) || state.is(BlockTags.FLOWERS) || state.is(Blocks.VINE);
    }

    public static boolean canTreeReplace(LevelAccessor world, BlockPos pos) {
        return TreeFeature.isAirOrLeaves(world, pos) || isVegetation(world, pos);
    }

    public static boolean isSolid(BlockGetter reader, BlockPos pos) {
        BlockState state = reader.getBlockState(pos);
        return isSolid(state, reader, pos);
    }

    public static boolean isSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.canOcclude() || !state.isPathfindable(reader, pos, PathComputationType.LAND);
    }

    public static boolean isSoilOrRock(LevelAccessor world, BlockPos pos) {
        BlockState block = world.getBlockState(pos);
        return TreeFeature.isDirt(block) || block.is(BlockTags.BASE_STONE_OVERWORLD);
    }

    public static boolean isClearOverhead(LevelAccessor world, BlockPos pos, int height, BiPredicate<LevelAccessor, BlockPos> predicate) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        // world.getMaxHeight ?
        int max = Math.min(world.getMaxBuildHeight() - 1, pos.getY() + height);
        for (int y = pos.getY(); y < max; y++) {
            mutable.set(pos.getX(), y, pos.getZ());
            if (!predicate.test(world, mutable)) {
                return false;
            }
        }
        return true;
    }
}
