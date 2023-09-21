package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public record SingleContinent(Domain warp, float frequency, float offsetAlpha, DistanceFunction distanceFunc, float clampMin, float clampMax, float inlandPoint, Noise shape) implements Noise, SimpleContinent {
	public static final Codec<SingleContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("warp").forGetter(SingleContinent::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(SingleContinent::frequency),
		Codec.FLOAT.fieldOf("offset_alpha").forGetter(SingleContinent::offsetAlpha),
		DistanceFunction.CODEC.fieldOf("distance_func").forGetter(SingleContinent::distanceFunc),
		Codec.FLOAT.fieldOf("clamp_min").forGetter(SingleContinent::clampMin),
		Codec.FLOAT.fieldOf("clamp_max").forGetter(SingleContinent::clampMax),
		Codec.FLOAT.fieldOf("inland_point").forGetter(SingleContinent::inlandPoint),
		Noise.HOLDER_HELPER_CODEC.fieldOf("shape").forGetter(SingleContinent::shape)
	).apply(instance, SingleContinent::new));

	@Override
    public float compute(float x, float y, int seed) {
		return SimpleContinent.super.getValue(x, y, seed, true);
    }
	
	@Override
	public Codec<SingleContinent> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new SingleContinent(this.warp, this.frequency, this.offsetAlpha, this.distanceFunc, this.clampMin, this.clampMax, this.inlandPoint, this.shape.mapAll(visitor)));
	}
}
