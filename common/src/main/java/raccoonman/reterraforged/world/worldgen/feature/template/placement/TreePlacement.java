package raccoonman.reterraforged.world.worldgen.feature.template.placement;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import raccoonman.reterraforged.world.worldgen.feature.template.BlockUtils;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TreeContext;
import raccoonman.reterraforged.world.worldgen.feature.template.template.Dimensions;

record TreePlacement() implements TemplatePlacement<TreeContext> {
	public static final Codec<TreePlacement> CODEC = Codec.unit(TreePlacement::new);
	
    @Override
    public boolean canPlaceAt(LevelAccessor world, BlockPos pos, Dimensions dimensions) {
        return BlockUtils.isSoil(world, pos.below()) && BlockUtils.isClearOverhead(world, pos, dimensions.getSizeY(), BlockUtils::canTreeReplace);
    }

    @Override
    public boolean canReplaceAt(LevelAccessor world, BlockPos pos) {
        return BlockUtils.canTreeReplace(world, pos);
    }

	@Override
	public TreeContext createContext() {
		return new TreeContext();
	}

	@Override
	public Codec<TreePlacement> codec() {
		return CODEC;
	}
}
