package raccoonman.reterraforged.world.worldgen.noise.warp;

import com.mojang.serialization.MapCodec;

import raccoonman.reterraforged.world.worldgen.noise.Noise.Visitor;
import raccoonman.reterraforged.world.worldgen.PosUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.DistanceFunction;

record WorleyCenter() implements Warp {
	public static final MapCodec<WorleyCenter> CODEC = MapCodec.unit(WorleyCenter::new);
	private static final ThreadLocal<Sample> LOCAL_SAMPLE = ThreadLocal.withInitial(Sample::new);

	@Override
	public float getOffsetX(float x, float z, int seed) {
		return 0;
	}

	@Override
	public float getOffsetZ(float x, float z, int seed) {
		return 0;
	}
	
	@Override
	public float getX(float x, float z, int seed) {
		Sample sample = LOCAL_SAMPLE.get().getAndUpdate(x, z, this, seed);
		return sample.regionX;
	}

	@Override
	public float getZ(float x, float z, int seed) {
		Sample sample = LOCAL_SAMPLE.get().getAndUpdate(x, z, this, seed);
		return sample.regionZ;
	}

	@Override
	public Warp mapAll(Visitor visitor) {
		return this;
	}

	@Override
	public MapCodec<WorleyCenter> codec() {
		return CODEC;
	}
	
	private void sample(Sample sample, float x, float z, int seed) {
        int xr = NoiseUtil.floor(x);
        int yr = NoiseUtil.floor(z);
        float centerX = x;
        float centerZ = z;
        float edgeDistance = 999999.0F;
        float edgeDistance2 = 999999.0F;
        DistanceFunction dist = DistanceFunction.EUCLIDEAN;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float cxf = cx + vec.x();
                float cyf = cy + vec.y();
                float distance = dist.apply(cxf - x, cyf - z);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = cxf;
                    centerZ = cyf;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        
        sample.regionX = centerX;
        sample.regionZ = centerZ;
	}
	
	private static class Sample {
		private long cachePos = Long.MAX_VALUE;
		public float regionX;
		public float regionZ;
		
		public Sample getAndUpdate(float cacheX, float cacheZ, WorleyCenter region, int seed) {
			long cachePos = PosUtil.packf(cacheX, cacheZ);
			if(this.cachePos != cachePos) {
				region.sample(this, cacheX, cacheZ, seed);
				this.cachePos = cachePos;
			}
			return this;
		}
	}
}