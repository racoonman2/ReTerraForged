package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

interface BaseContinent {
	Domain warp();
	
	float frequency();
	
	float offsetAlpha();
	
	DistanceFunction distanceFunc();
	
	float clampMin();
	
	float clampMax();
	
	float inlandPoint();
	
	Noise shape();
	
	default float compute(float x, float y, int seed, @Deprecated boolean single) {
		Domain warp = this.warp();
        float ox = warp.getOffsetX(x, y, seed);
        float oz = warp.getOffsetY(x, y, seed);
        float px = x + ox;
        float py = y + oz;
        float frequency = this.frequency();
        px *= frequency;
        py *= frequency;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        float centerX = px;
        float centerY = py;
        float edgeDistance = 999999.0F;
        float edgeDistance2 = 999999.0F;
        float offsetAlpha = this.offsetAlpha();
        DistanceFunction distanceFunc = this.distanceFunc();
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(cx, cy, seed);
                float cxf = cx + vec.x() * offsetAlpha;
                float cyf = cy + vec.y() * offsetAlpha;
                float distance = distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        
        if(single) {
	        long nearestCenter = this.getNearestCenter(0.0F, 0.0F, seed);
	        int cx = NoiseUtil.unpackLeft(nearestCenter);
	        int cz = NoiseUtil.unpackRight(nearestCenter);
	        
	        float continentX = (int) (centerX / frequency);
	        float continentZ = (int) (centerY / frequency);
	        if (continentX != cx || continentZ != cz) {
	            return 0.0F;
	        }
        }
        
        float edgeValue = this.cellEdgeValue(edgeDistance, edgeDistance2);
        return edgeValue *= this.getShape(x, y, edgeValue, seed);
	}

    private long getNearestCenter(float x, float z, int seed) {
        Domain warp = this.warp();
    	float ox = warp.getOffsetX(x, z, seed);
        float oz = warp.getOffsetY(x, z, seed);
        float px = x + ox;
        float py = z + oz;
        float frequency = this.frequency();
        px *= frequency;
        py *= frequency;
        float centerX = px;
        float centerY = py;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0F;
        float offsetAlpha = this.offsetAlpha();
        DistanceFunction distanceFunc = this.distanceFunc();
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float cxf = cx + vec.x() * offsetAlpha;
                float cyf = cy + vec.y() * offsetAlpha;
                float distance = distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                }
            }
        }
        int conX = (int) (centerX / frequency);
        int conZ = (int) (centerY / frequency);
        return NoiseUtil.pack(conX, conZ);
    }
	
	private float cellEdgeValue(float distance, float distance2) {
        EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        float clampMin = this.clampMin();
        float clampMax = this.clampMax();
        value = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        if (value <= clampMin) {
            return 0.0F;
        }
        if (value >= clampMax) {
            return 1.0F;
        }
        return (value - clampMin) / (clampMax - clampMin);
    }
	
	private float getShape(float x, float z, float edgeValue, int seed) {
		float inlandPoint = this.inlandPoint();
        if (edgeValue >= inlandPoint) {
            return 1.0F;
        }
        Noise shape = this.shape();
        float alpha = edgeValue / inlandPoint;
        return shape.compute(x, z, seed) * alpha;
    }
}
