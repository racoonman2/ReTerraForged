package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record ClimateRegionOffset(Noise edge, Noise region, Noise offsetX, Noise offsetY, float edgeBlend, float offsetDistance) implements Noise {
	public static final Codec<ClimateRegionOffset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("edge").forGetter(ClimateRegionOffset::edge),
		Noise.HOLDER_HELPER_CODEC.fieldOf("region").forGetter(ClimateRegionOffset::region),
		Noise.HOLDER_HELPER_CODEC.fieldOf("offset_x").forGetter(ClimateRegionOffset::offsetX),
		Noise.HOLDER_HELPER_CODEC.fieldOf("offset_y").forGetter(ClimateRegionOffset::offsetY),
		Codec.FLOAT.fieldOf("edge_blend").forGetter(ClimateRegionOffset::edgeBlend),
		Codec.FLOAT.fieldOf("offset_distance").forGetter(ClimateRegionOffset::offsetDistance)
	).apply(instance, ClimateRegionOffset::new));
	
	@Override
	public Codec<ClimateRegionOffset> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		float edge = this.edge.compute(x, y, seed);
		float sx = x;
		float sy = y;
		if(edge < this.edgeBlend) {
			float modifier = 1.0F - NoiseUtil.map(edge, 0.0F, this.edgeBlend, this.edgeBlend);
	        float distance = this.offsetDistance * modifier;
	        sx += this.offsetX.compute(x, y, seed) * distance;
	        sy += this.offsetY.compute(x, y, seed) * distance;
		}
		return this.region.compute(sx, sy, seed);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new ClimateRegionOffset(this.edge.mapAll(visitor), this.region.mapAll(visitor), this.offsetX.mapAll(visitor), this.offsetY.mapAll(visitor), this.edgeBlend, this.offsetDistance));
	}
}
