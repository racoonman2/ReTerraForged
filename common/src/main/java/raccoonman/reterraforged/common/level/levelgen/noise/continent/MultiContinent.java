package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public record MultiContinent(Domain warp, float frequency, float offsetAlpha, DistanceFunction distanceFunc, float clampMin, float clampMax, float inlandPoint, Noise shape) implements Noise, BaseContinent {
	public static final Codec<MultiContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("warp").forGetter(MultiContinent::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(MultiContinent::frequency),
		Codec.FLOAT.fieldOf("offset_alpha").forGetter(MultiContinent::offsetAlpha),
		DistanceFunction.CODEC.fieldOf("distance_func").forGetter(MultiContinent::distanceFunc),
		Codec.FLOAT.fieldOf("clamp_min").forGetter(MultiContinent::clampMin),
		Codec.FLOAT.fieldOf("clamp_max").forGetter(MultiContinent::clampMax),
		Codec.FLOAT.fieldOf("inland_point").forGetter(MultiContinent::inlandPoint),
		Noise.HOLDER_HELPER_CODEC.fieldOf("shape").forGetter(MultiContinent::shape)
	).apply(instance, MultiContinent::new));

	@Override
    public float compute(float x, float y, int seed) {
		return BaseContinent.super.compute(x, y, seed, false);
    }
	
	@Override
	public Codec<MultiContinent> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new MultiContinent(this.warp, this.frequency, this.offsetAlpha, this.distanceFunc, this.clampMin, this.clampMax, this.inlandPoint, this.shape.mapAll(visitor)));
	}
}
