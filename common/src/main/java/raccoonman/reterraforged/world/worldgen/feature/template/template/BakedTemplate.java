package raccoonman.reterraforged.world.worldgen.feature.template.template;

import java.util.stream.Stream;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public class BakedTemplate extends BakedTransform<BlockInfo[]> {

    public BakedTemplate(BlockInfo[] value) {
        super(BlockInfo[][]::new, value);
    }

    @Override
    protected BlockInfo[] apply(Mirror mirror, Rotation rotation, BlockInfo[] value) {
        return Stream.of(value).map(block -> block.transform(mirror, rotation)).toArray(BlockInfo[]::new);
    }
}