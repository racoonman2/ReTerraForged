package raccoonman.reterraforged.common.level.levelgen.test.tile.chunk;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;

public interface ChunkWriter extends ChunkHolder {
    Cell genCell(int x, int z);
    
    default void generate(Cell.Visitor visitor) {
        for (int dz = 0; dz < 16; ++dz) {
            for (int dx = 0; dx < 16; ++dx) {
                visitor.visit(this.genCell(dx, dz), dx, dz);
            }
        }
    }
    
    default <T> void generate(T ctx, final Visitor<T> visitor) {
        int blockX = this.getBlockX();
        int blockZ = this.getBlockZ();
        for (int dz = 0; dz < 16; ++dz) {
            for (int dx = 0; dx < 16; ++dx) {
                visitor.visit(this.genCell(dx, dz), dx, dz, blockX + dx, blockZ + dz, ctx);
            }
        }
    }
    
    public interface Visitor<T> {
        void visit(Cell cell, int x0, int z0, int x1, int z1, T ctx);
    }
}
