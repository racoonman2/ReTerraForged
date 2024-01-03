package raccoonman.reterraforged.world.worldgen.feature.template.template;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public record BlockInfo(BlockPos pos, BlockState state) {
	
    public BlockInfo transform(Mirror mirror, Rotation rotation) {
        BlockPos pos = FeatureTemplate.transform(this.pos, mirror, rotation);
        BlockState state = this.state.mirror(mirror).rotate(rotation);
        return new BlockInfo(pos, state);
    }

    @Override
    public String toString() {
        return this.state.toString();
    }
}
