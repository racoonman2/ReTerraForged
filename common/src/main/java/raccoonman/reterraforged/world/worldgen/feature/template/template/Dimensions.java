package raccoonman.reterraforged.world.worldgen.feature.template.template;

import net.minecraft.core.BlockPos;

public record Dimensions(BlockPos min, BlockPos max) {
	
    public int getSizeX() {
        return this.max.getX() - this.min.getX();
    }

    public int getSizeY() {
        return this.max.getY() - this.min.getY();
    }

    public int getSizeZ() {
        return this.max.getY() - this.min.getY();
    }
}