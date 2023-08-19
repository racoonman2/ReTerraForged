package raccoonman.reterraforged.common.level.levelgen.noise.river;

import raccoonman.reterraforged.common.level.levelgen.cell.CellPoint;
import raccoonman.reterraforged.common.level.levelgen.continent.river.NodeSample;
import raccoonman.reterraforged.common.level.levelgen.continent.river.RiverNode;
import raccoonman.reterraforged.common.level.levelgen.continent.river.RiverPieces;
import raccoonman.reterraforged.common.level.levelgen.continent.river.RiverSample;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.noise.util.Vec2i;
import raccoonman.reterraforged.common.util.MathUtil;
import raccoonman.reterraforged.common.util.pos.PosUtil;
import raccoonman.reterraforged.common.util.storage.LongCache;
import raccoonman.reterraforged.common.util.storage.LossyCache;
import raccoonman.reterraforged.common.util.storage.ObjectPool;

public class RiverPieceSampler {
    private static final Vec2i[] DIRS = { new Vec2i(1, 0), new Vec2i(0, 1), new Vec2i(-1, 0), new Vec2i(0, -1) };

    private static final int DIR_OFFSET = 20107;
    private static final int SIZE_A_OFFSET = 9803;
    private static final int SIZE_B_OFFSET = 28387;
    private static final int LAKE_CHANCE_OFFSET = 37171;
    private static final int RIVER_CACHE_SIZE = 1024;
 
    private final ObjectPool<RiverPieces> pool = ObjectPool.forCacheSize(RIVER_CACHE_SIZE, RiverPieces::new);
    private final LongCache<RiverPieces> cache = LossyCache.concurrent(RIVER_CACHE_SIZE, RiverPieces[]::new, this.pool::restore);

//    public void sample(int seed, float x, float y, RiverSample sample) {
//        var centre = continent.getNearestCell(seed, x, y);
//        int centreX = PosUtil.unpackLeft(centre);
//        int centreY = PosUtil.unpackRight(centre);
//
//        // Note: Must adjust inputs AFTER getting nearest cell
//        x = continent.cellShape.adjustX(x);
//        y = continent.cellShape.adjustY(y);
//
//        int minX = centreX - 1;
//        int minY = centreY - 1;
//        int maxX = centreX + 1;
//        int maxY = centreY + 1;
//
//        RiverNode river = null;
//        RiverNode lake = null;
//
//        for (int cy = minY; cy <= maxY; cy++) {
//            for (int cx = minX; cx <= maxX; cx++) {
//                var pieces = getNodes(cx, cy, seed);
//
//                for (int i = 0; i < pieces.riverCount(); i++) {
//                    var node = pieces.river(i);
//                    river = sampleNode(x, y, node, river, sample.river());
//                }
//
//                for (int i = 0; i < pieces.lakeCount(); i++) {
//                    var node = pieces.lake(i);
//                    lake = sampleNode(x, y, node, lake, sample.lake());
//                }
//            }
//        }
//
//        recordNode(river, sample.river());
//        recordNode(lake, sample.lake());
//    }
//
//    private RiverNode sampleNode(float x, float y, RiverNode node, RiverNode nearest, NodeSample sample) {
//        float t = node.getProjection(x, y);
//        float d = node.getDistance2(x, y, t);
//
//        if (d < sample.distance) {
//            nearest = node;
//            sample.distance = d;
//            sample.projection = t;
//        }
//
//        return nearest;
//    }
//
//    private void recordNode(RiverNode node, NodeSample sample) {
//        if (node != null) {
//            float level = node.getHeight(sample.projection);
//            float radius = node.getRadius(sample.projection);
//            sample.distance = NoiseUtil.sqrt(sample.distance);
//            sample.position = radius;
//            sample.level = continent.shapeNoise.getBaseNoise(level);
//        } else {
//            sample.invalidate();
//        }
//    }
//
//    private RiverPieces getNodes(int x, int y, int seed) {
//        long index = PosUtil.pack(x, y);
//        return cache.computeIfAbsent(index, (k) -> this.computeNodes(k, seed));
//    }
//
//    private RiverPieces computeNodes(long index, int seed) {
//        int ax = PosUtil.unpackLeft(index);
//        int ay = PosUtil.unpackRight(index);
//
//        var a = continent.getCell(seed, ax, ay);
//        if (continent.shapeNoise.getThresholdValue(a) <= 0) return RiverPieces.NONE;
//
//        var min = a;
//        float minValue = getBaseValue(a);
//
//        float ah = getHeight(a.lowOctaveNoise, 0, 1);
//        float ar = getRadius(a.lowOctaveNoise, 0, 1);
//
//        boolean isSource = true;
//        var pieces = pool.take();
//        for (var dir : DIRS) {
//            int bx = ax + dir.x;
//            int by = ay + dir.y;
//            var b = continent.getCell(seed, bx, by);
//
//            float value = getBaseValue(b);
//
//            // Track the lowest neighbour as the candidate to connect A to
//            if (value <= minValue) {
//                min = b;
//                minValue = value;
//                continue;
//            }
//
//            if (value <= 0) continue;
//
//            // Check if B is higher and A is its lowest neighbour
//            if (connects(ax, ay, bx, by, value, seed)) {
//                float bh = getHeight(b.lowOctaveNoise, 0, 1);
//                float br = getRadius(b.lowOctaveNoise, 0, 1);
//                int hash = MathUtil.hash(seed + 827614, bx, by);
//
//                addRiverNodes(a, b, ah, bh, ar, br, hash, pieces, seed);
//
//                isSource = false;
//            }
//        }
//
//        // No lower-neighbour to connect to
//        if (min == a) {
//            return pieces;
//        }
//
//        // Cull tiny chode-rivers
//        if (isSource && pieces.riverCount() == 0 && minValue <= 0) {
//            pool.restore(pieces);
//            return RiverPieces.NONE;
//        }
//
//        float bh = getHeight(min.lowOctaveNoise, 0, 1);
//        float br = getRadius(min.lowOctaveNoise, 0, 1);
//        int hash = MathUtil.hash(seed + 827614, ax, ay);
//
//        addRiverNodes(a, min, ah, bh, ar, br, hash, pieces, seed);
//
//        if (isSource && hasLake(a, hash)) {
//            addLakeNodes(a, min, ah, hash, pieces, seed);
//        }
//
//        return pieces;
//    }
//
//    private void addRiverNodes(CellPoint a, CellPoint b, float ah, float bh, float ar, float br, int hash, RiverPieces pieces, int seed) {
//        // Mid-point on border between cell points A & B
//        float mx = (a.px + b.px) * 0.5f;
//        float my = (a.py + b.py) * 0.5f;
//        float mr = (ar + br) * 0.5f;
//        float mh = (ah + bh) * 0.5f;
//
//        // Mid-point between A & M
//        float cx = (a.px + mx) * 0.5f;
//        float cy = (a.py + my) * 0.5f;
//        float cr = (ar + mr) * 0.5f;
//        float ch = (ah + mh) * 0.5f;
//
//        // Normals to AC
//        float nx = -(cy - a.py);
//        float ny = (cx - a.px);
//
//        float dir = MathUtil.rand(seed + DIR_OFFSET, hash) < 0.5f ? -1f : 1f;
//        float amp0 = 0.7f + MathUtil.rand(seed + SIZE_A_OFFSET, hash) * 0.3f;
//        float amp1 = 0.7f + MathUtil.rand(seed + SIZE_B_OFFSET, hash) * 0.3f;
//
//        // Displace point C perpendicularly to AC
//        float displacement = 0.35f * dir * amp0;
//        cx += nx * displacement;
//        cy += ny * displacement;
//
//        float warpStrength = 0.275f * -dir * amp1;
//        float warp1 = warpStrength * NoiseUtil.map(a.noise, 0.4f, 0.6f, 0.2f);
//        float warp2 = -warpStrength * NoiseUtil.map(b.noise, 0.4f, 0.6f, 0.2f);
//
//        pieces.addRiver(new RiverNode(a.px, a.py, cx, cy, ah, ch, ar, cr, warp1));
//        pieces.addRiver(new RiverNode(cx, cy, mx, my, ch, mh, cr, mr, warp2));
//
//        // If B is an ocean cell then extend the connection all the way to prevent
//        // rivers stopping short at the coast/beach. Note: we must use the 'b.noise'
//        // here and not the low-octave 'b.noise()'.
//        if (b.noise < continent.threshold) {
//            pieces.addRiver(new RiverNode(mx, my, b.px, b.py, mh, bh, mr, br, warp1));
//        }
//    }
//
//    private void addLakeNodes(CellPoint a, CellPoint b, float ah, int hash, RiverPieces pieces, int seed) {
//        float size = (0.5f + MathUtil.rand(seed + SIZE_A_OFFSET, hash) * 0.5f) * 0.12f;
//
//        float dx = a.px - b.px;
//        float dy = a.py - b.py;
//        float cx = a.px + dx * size;
//        float cy = a.py + dy * size;
//
//        pieces.addLake(new RiverNode(a.px, a.py, cx, cy, ah, ah, 1, 1, 0));
//    }
//
//    private boolean connects(int ax, int ay, int bx, int by, float minValue, int seed) {
//        int minY = bx;
//        int minX = by;
//
//        for (var dir : DIRS) {
//            int cx = bx + dir.x;
//            int cy = by + dir.y;
//            var c = continent.getCell(seed, cx, cy);
//            float value = getBaseValue(c);
//
//            if (value < minValue) {
//                minX = cx;
//                minY = cy;
//                minValue = value;
//            }
//        }
//
//        return minX == ax && minY == ay;
//    }
//
//    private boolean hasLake(CellPoint cell, int hash) {
//        return MathUtil.rand(hash + LAKE_CHANCE_OFFSET) <= lakeDensity
//                || continent.shapeNoise.getBaseNoise(cell.lowOctaveNoise) < 0.25f;
//    }
//
//    private float getBaseValue(CellPoint point) {
//        return continent.shapeNoise.getThresholdValue(point) <= 0 ? 0 : point.lowOctaveNoise;
//    }
//
//    private float getHeight(float noise, float min, float max) {
//        return noise;
//    }
//
//    private float getRadius(float noise, float min, float max) {
//        float lower = 0.5f;
//        float upper = 0.7f;
//
//        noise = NoiseUtil.map(noise, lower, upper, (upper - lower));
//        noise = 1 - noise;
//
//        return noise;
//    }
}
