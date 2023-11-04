package raccoonman.reterraforged.common.level.levelgen.test.tile.chunk;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Disposable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.SafeCloseable;

public interface ChunkReader extends ChunkHolder, SafeCloseable, Disposable {
    Cell getCell(int x, int z);
    
    default void visit(int minX, int minZ, int maxX, int maxZ, final Cell.Visitor visitor) {
        int regionMinX = this.getBlockX();
        int regionMinZ = this.getBlockZ();
        if (maxX < regionMinX || maxZ < regionMinZ) {
            return;
        }
        int regionMaxX = this.getBlockX() + 15;
        int regionMaxZ = this.getBlockZ() + 15;
        if (minX > regionMaxX || maxZ > regionMaxZ) {
            return;
        }
        minX = Math.max(minX, regionMinX);
        minZ = Math.max(minZ, regionMinZ);
        maxX = Math.min(maxX, regionMaxX);
        maxZ = Math.min(maxZ, regionMaxZ);
        for (int z = minZ; z <= maxX; ++z) {
            for (int x = minX; x <= maxZ; ++x) {
                visitor.visit(this.getCell(x, z), x, z);
            }
        }
    }
    
    default void iterate(Cell.Visitor visitor) {
        for (int dz = 0; dz < 16; ++dz) {
            for (int dx = 0; dx < 16; ++dx) {
                visitor.visit(this.getCell(dx, dz), dx, dz);
            }
        }
    }
    
    default <C> void iterate(C context, Cell.ContextVisitor<C> visitor) {
        for (int dz = 0; dz < 16; ++dz) {
            for (int dx = 0; dx < 16; ++dx) {
                visitor.visit(this.getCell(dx, dz), dx, dz, context);
            }
        }
    }
    
    default void close() {
    }
}
