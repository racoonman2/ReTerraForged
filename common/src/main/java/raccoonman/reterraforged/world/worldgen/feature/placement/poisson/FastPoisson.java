package raccoonman.reterraforged.world.worldgen.feature.placement.poisson;

import java.util.Random;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

public class FastPoisson {
    public static final ThreadLocal<FastPoisson> LOCAL_POISSON = ThreadLocal.withInitial(FastPoisson::new);
    private LongList chunk;
    private LongIterSet region;
    
    public FastPoisson() {
        this.chunk = new LongArrayList();
        this.region = new LongIterSet();
    }
    
    public <Ctx> void visit(int seed, int chunkX, int chunkZ, Random random, FastPoissonContext context, Ctx ctx, Visitor<Ctx> visitor) {
        this.chunk.clear();
        this.region.clear();
        visit(seed, chunkX, chunkZ, random, context, this.region, this.chunk, ctx, visitor);
    }
    
    public static <Ctx> void visit(int seed, int chunkX, int chunkZ, Random random, FastPoissonContext context, LongIterSet region, LongList chunk, Ctx ctx, Visitor<Ctx> visitor) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        collectPoints(seed, startX, startZ, context, region, chunk);
        region.shuffle(random);
        LongLists.shuffle(chunk, random);
        visitPoints(startX, startZ, region, chunk, context, ctx, visitor);
    }
    
    private static void collectPoints(int seed, int startX, int startZ, FastPoissonContext context, LongIterSet region, LongList chunk) {
        int halfRadius = context.radius() / 2;
        int quarterRadius = context.radius() / 4;
        int min = -halfRadius;
        int max = 15 + halfRadius;
        int cullX = startX - quarterRadius;
        int cullZ = startZ - quarterRadius;
        for (int dz = min; dz <= max; ++dz) {
            for (int dx = min; dx <= max; ++dx) {
                int x = startX + dx;
                int z = startZ + dz;
                long point = getPoint(seed, x, z, context);
                int px = PosUtil.unpackLeft(point);
                int pz = PosUtil.unpackRight(point);
                if (px >= cullX) {
                    if (pz >= cullZ) {
                        if (region.add(point) && inChunkBoundsLow(px, pz, startX, startZ, -1)) {
                            chunk.add(point);
                        }
                    }
                }
            }
        }
    }
    
    private static <Ctx> void visitPoints(int startX, int startZ, LongIterSet region, LongList chunk, FastPoissonContext context, Ctx ctx, Visitor<Ctx> visitor) {
        int radius2 = context.radiusSq();
        int halfRadius = context.radius() / 2;
        for (int i = 0; i < chunk.size(); ++i) {
            long point = chunk.getLong(i);
            int px = PosUtil.unpackLeft(point);
            int pz = PosUtil.unpackRight(point);
            if (region.contains(point)) {
                float noise = context.density().compute(px, pz, 0);
                float radius2f = radius2 * noise;
                if (checkNeighbours(startX, startZ, point, px, pz, halfRadius, radius2f, region)) {
                    visitor.visit(px, pz, ctx);
                }
            }
        }
    }
    
    private static boolean checkNeighbours(int startX, int startZ, long point, int x, int z, int halfRadius, float radius2, LongIterSet region) {
        region.reset();
        int boundHigh = 16 + halfRadius;
        while (region.hasNext()) {
            long neighbour = region.nextLong();
            if (neighbour == Long.MAX_VALUE) {
                return false;
            }
            if (point == neighbour) {
                continue;
            }
            int px = PosUtil.unpackLeft(neighbour);
            int pz = PosUtil.unpackRight(neighbour);
            if (dist2(x, z, px, pz) > radius2) {
                continue;
            }
            if (!inChunkBoundsHigh(px, pz, startX, startZ, boundHigh)) {
                return false;
            }
            region.remove();
        }
        return true;
    }
    
    private static long getPoint(int seed, float x, float z, FastPoissonContext context) {
        x *= context.frequency();
        z *= context.frequency();
        int cellX = NoiseUtil.floor(x);
        int cellZ = NoiseUtil.floor(z);
        Vec2f vec = NoiseUtil.cell(seed, cellX, cellZ);
        int px = NoiseUtil.floor((cellX + context.pad() + vec.x() * context.jitter()) * context.scale());
        int pz = NoiseUtil.floor((cellZ + context.pad() + vec.y() * context.jitter()) * context.scale());
        return PosUtil.pack(px, pz);
    }
    
    private static boolean inChunkBoundsLow(int px, int pz, int startX, int startZ, int min) {
        int dx = px - startX;
        int dz = pz - startZ;
        return dx > min && dx < 16 && dz > min && dz < 16;
    }
    
    private static boolean inChunkBoundsHigh(int px, int pz, int startX, int startZ, int max) {
        int dx = px - startX;
        int dz = pz - startZ;
        return dx > -1 && dx < max && dz > -1 && dz < max;
    }
    
    private static int dist2(int ax, int az, int bx, int bz) {
        int dx = ax - bx;
        int dz = az - bz;
        return dx * dx + dz * dz;
    }
    
    public interface Visitor<Ctx> {
        void visit(int x, int z, Ctx ctx);
    }
}
