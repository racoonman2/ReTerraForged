package raccoonman.reterraforged.world.worldgen.feature.template.template;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public class BakedDimensions extends BakedTransform<Dimensions> {

    public BakedDimensions(Dimensions value) {
        super(Dimensions[]::new, value);
    }

    @Override
    protected Dimensions apply(Mirror mirror, Rotation rotation, Dimensions value) {
        Vec3i min = FeatureTemplate.transform(value.min(), mirror, rotation);
        Vec3i max = FeatureTemplate.transform(value.max(), mirror, rotation);
        return compile(min, max);
    }

    public static Dimensions compile(Vec3i min, Vec3i max) {
        int minX = Math.min(min.getX(), max.getX());
        int minY = Math.min(min.getY(), max.getY());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxX = Math.max(min.getX(), max.getX());
        int maxY = Math.max(min.getY(), max.getY());
        int maxZ = Math.max(min.getZ(), max.getZ());
        return new Dimensions(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }
}
