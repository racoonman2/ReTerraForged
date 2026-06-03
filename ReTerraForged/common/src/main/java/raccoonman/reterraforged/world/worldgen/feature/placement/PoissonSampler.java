package raccoonman.reterraforged.world.worldgen.feature.placement;

import java.util.Random;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.world.worldgen.PosUtil;
import raccoonman.reterraforged.world.worldgen.densityfunction.MutableFunctionContext;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;

// maybe replace with raccoonman.reterraforged.world.worldgen.layer.terrain.PoissonDiskSampler?
public class PoissonSampler {
    private LongList chunk;
    private LongIterSet region;
    
    public PoissonSampler() {
        this.chunk = new LongArrayList();
        this.region = new LongIterSet();
    }
    
    public void sample(long seed, int chunkX, int chunkZ, Context context, Visitor visitor) {
    	Random random = new Random(seed);
    	
    	this.chunk.clear();
        this.region.clear();

        int startX = SectionPos.sectionToBlockCoord(chunkX);
        int startZ = SectionPos.sectionToBlockCoord(chunkZ);
        this.collectPoints((int) seed, startX, startZ, context);
        this.region.shuffle(random);
        LongLists.shuffle(this.chunk, random);
        this.visitPoints(startX, startZ, context, visitor);
    }
    
    private void collectPoints(int seed, int startX, int startZ, Context context) {
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
                        if (this.region.add(point) && inChunkBoundsLow(px, pz, startX, startZ, -1)) {
                        	this.chunk.add(point);
                        }
                    }
                }
            }
        }
    }
    
    private void visitPoints(int startX, int startZ, Context context, Visitor visitor) {
    	MutableFunctionContext ctx = new MutableFunctionContext();
    	
        int radius2 = context.radiusSq();
        int halfRadius = context.radius() / 2;
        for (int i = 0; i < this.chunk.size(); ++i) {
            long point = this.chunk.getLong(i);
            int px = PosUtil.unpackLeft(point);
            int pz = PosUtil.unpackRight(point);
            ctx.at(px, pz);
            if (this.region.contains(point)) {
                float noise = (float) context.density().compute(ctx);
                float radius2f = radius2 * noise;
                if (this.checkNeighbours(startX, startZ, point, px, pz, halfRadius, radius2f)) {
                    visitor.visit(px, pz);
                }
            }
        }
    }
    
    private boolean checkNeighbours(int startX, int startZ, long point, int x, int z, int halfRadius, float radius2) {
    	this.region.reset();
        int boundHigh = 16 + halfRadius;
        while (this.region.hasNext()) {
            long neighbour = this.region.nextLong();
            if (neighbour == Long.MAX_VALUE) {
                return false;
            }
            if (point == neighbour) {
                continue;
            }
            int px = PosUtil.unpackLeft(neighbour);
            int pz = PosUtil.unpackRight(neighbour);
            if (distSq(x, z, px, pz) > radius2) {
                continue;
            }
            if (!inChunkBoundsHigh(px, pz, startX, startZ, boundHigh)) {
                return false;
            }
            this.region.remove();
        }
        return true;
    }
    
    private static long getPoint(int seed, float x, float z, Context context) {
        x *= context.frequency();
        z *= context.frequency();
        int cellX = NoiseUtil.floor(x);
        int cellZ = NoiseUtil.floor(z);
        Vec2f vec = NoiseUtil.cell(seed, cellX, cellZ);
        int px = NoiseUtil.floor((cellX + context.padding() + vec.x() * context.jitter()) * context.scale());
        int pz = NoiseUtil.floor((cellZ + context.padding() + vec.y() * context.jitter()) * context.scale());
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
    
    private static int distSq(int ax, int az, int bx, int bz) {
        int dx = ax - bx;
        int dz = az - bz;
        return dx * dx + dz * dz;
    }
    
    public interface Visitor {
        void visit(int x, int z);
    }
    
    //TODO make density a DensityFunction
    public record Context(int radius, float jitter, float frequency, DensityFunction density) {

    	public Context {
        	frequency = Math.min(0.5F, frequency);
        	jitter = NoiseUtil.clamp(jitter, 0.0F, 1.0F);
        }
        
        public float scale() {
        	return 1.0F / this.frequency;
        }
        
        public int radiusSq() {
        	return this.radius * this.radius;
        }
        
        public float padding() {
        	return (1.0F - this.jitter) * 0.5F;
        }
    }
}
