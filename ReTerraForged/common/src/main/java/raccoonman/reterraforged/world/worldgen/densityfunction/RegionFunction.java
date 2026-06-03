package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warp;

public class RegionFunction extends MultiFunction {
	public static final MapCodec<RegionFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("cache_id").forGetter(RegionFunction::cacheId),
		Codec.INT.fieldOf("field_index").forGetter(RegionFunction::fieldIndex),
		Warp.CODEC.fieldOf("warp").forGetter(RegionFunction::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(RegionFunction::frequency)
	).apply(instance, RegionFunction::new));

	public static final int VALUE = 0;
	public static final int EDGE = 1;
	
	@Deprecated
	private static final int SEED = 0;
	
	private Holder<Warp> warp;
	private float frequency;

	public RegionFunction(ResourceLocation cacheId, int fieldIndex, Holder<Warp> warp, float frequency) {
		this(cacheId, fieldIndex, null, warp, frequency);
	}
	
	public RegionFunction(ResourceLocation cacheId, int fieldIndex, MultiFunction.Data cachedData, Holder<Warp> warp, float frequency) {
		super(cacheId, 2, fieldIndex, cachedData);
		this.warp = warp;
		this.frequency = frequency;
	}
	
	public Holder<Warp> warp() {
		return this.warp;
	}
	
	public float frequency() {
		return this.frequency;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.cacheId, this.fieldIndex, this.warp, this.frequency);
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof RegionFunction region && region.cacheId.equals(this.cacheId) && region.fieldIndex == this.fieldIndex && region.warp.equals(this.warp) && this.frequency == this.frequency;
	}
	
	@Override
	public void compute(FunctionContext ctx, float[] fields) {
		int blockX = ctx.blockX();
		int blockZ = ctx.blockZ();
		Warp warp = this.warp.value();
        float ox = warp.getOffsetX(blockX, blockZ, 0);
        float oz = warp.getOffsetZ(blockX, blockZ, 0);
        float px = blockX + ox;
        float py = blockZ + oz;
        px *= this.frequency;
        py *= this.frequency;
        int cellX = 0;
        int cellY = 0;
        int xi = NoiseUtil.floor(px);
        int yi = NoiseUtil.floor(py);
        float edgeDistance = Float.MAX_VALUE;
        float edgeDistance2 = Float.MAX_VALUE;
        DistanceFunction dist = DistanceFunction.NATURAL;
        for(int dy = -1; dy <= 1; dy++) {
            for(int dx = -1; dx <= 1; dx++) {
                int cx = xi + dx;
                int cy = yi + dy;
                Vec2f vec = NoiseUtil.cell(SEED, cx, cy);
                float vecX = cx + vec.x() * 0.7F;
                float vecY = cy + vec.y() * 0.7F;
                float distance = dist.apply(vecX - px, vecY - py);
                if(distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    cellX = cx;
                    cellY = cy;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        fields[VALUE] = this.cellValue(SEED, cellX, cellY);
        fields[EDGE] = this.edgeValue(edgeDistance, edgeDistance2);
	}

	@Override
	public RegionFunction withData(MultiFunction.Data data) {
		return new RegionFunction(this.cacheId, this.fieldIndex, data, this.warp, this.frequency);
	}
	
    private float cellValue(int seed, int cellX, int cellY) {
        float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }
    
    private float edgeValue(float distance, float distance2) {
        EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        float edgeValue = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        edgeValue = NoiseUtil.pow(edgeValue, 1.5F);
        if(edgeValue < 0.0F) {
            return 0.0F;
        }
        if(edgeValue > 0.5F) {
            return 1.0F;
        }
        return edgeValue / 0.5F;
    }

	@Override
	public KeyDispatchDataCodec<RegionFunction> codec() {
		return KeyDispatchDataCodec.of(CODEC);
	}
}
