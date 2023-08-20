package raccoonman.reterraforged.common.level.levelgen.noise.continent;

//TODO
public class SpawnOffset {
//	private static final int VALID_SPAWN_RADIUS = 3;
//    private static final int SPAWN_SEARCH_RADIUS = 100_000;
//    @Deprecated
//    private volatile Vec2f offset = null;

//
//	var offset = this.getWorldOffset(seed);
//	x += offset.x;
//	y += offset.y;
	
//    private Vec2f getWorldOffset(int seed) {
//        if (this.offset == null) {
//        	this.offset = this.computeWorldOffset(seed);
//        }
//        return this.offset;
//    }
//    private Vec2f computeWorldOffset(int seed) {
//        var iterator = new SpiralIterator(0, 0, 0, SPAWN_SEARCH_RADIUS);
//        var cell = new CellPoint();
//
//        while (iterator.hasNext()) {
//            long pos = iterator.next();
//            this.cellNoise.getUncached(seed, pos, 0, 0, cell);
//
//            if (this.getThresholdValue(cell) == 0) {
//                continue;
//            }
//
//            float px = cell.px;
//            float py = cell.py;
//            if (this.isValidSpawn(seed, pos, VALID_SPAWN_RADIUS, cell)) {
//                return new Vec2f(px, py);
//            }
//        }
//
//        return Vec2f.ZERO;
//    }
//
//    private boolean isValidSpawn(int seed, long pos, int radius, CellPoint cell) {
//        int radius2 = radius * radius;
//
//        for (int dy = -radius; dy <= radius; dy++) {
//            for (int dx = -radius; dx <= radius; dx++) {
//                int d2 = dx * dx + dy * dy;
//
//                if (dy < 1 || d2 >= radius2) continue;
//
//                this.cellNoise.getUncached(seed, pos, dx, dy, cell);
//
//                if (this.getThresholdValue(cell) == 0) {
//                    return false;
//                }
//            }
//        }
//
//        return true;
//    }
}
