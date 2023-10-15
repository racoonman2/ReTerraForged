package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;

public record ContinentLerper(Noise continentEdge, Noise deepOcean, Noise shallowOcean, Noise coast, Interpolation oceanInterpolation, Noise land, Interpolation interpolation, float deepOceanPoint, float shallowOceanPoint, float coastPoint, float inlandPoint) implements Noise {
	public static final Codec<ContinentLerper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("continent_edge").forGetter(ContinentLerper::continentEdge),
		Noise.HOLDER_HELPER_CODEC.fieldOf("deep_ocean").forGetter(ContinentLerper::deepOcean),
		Noise.HOLDER_HELPER_CODEC.fieldOf("shallow_ocean").forGetter(ContinentLerper::shallowOcean),
		Noise.HOLDER_HELPER_CODEC.fieldOf("coast").forGetter(ContinentLerper::coast),
		Interpolation.CODEC.fieldOf("ocean_interpolation").forGetter(ContinentLerper::oceanInterpolation),
		Noise.HOLDER_HELPER_CODEC.fieldOf("land").forGetter(ContinentLerper::land),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(ContinentLerper::interpolation),
		Codec.FLOAT.fieldOf("deep_ocean_point").forGetter(ContinentLerper::deepOceanPoint),
		Codec.FLOAT.fieldOf("shallow_ocean_point").forGetter(ContinentLerper::shallowOceanPoint),
		Codec.FLOAT.fieldOf("coast_point").forGetter(ContinentLerper::coastPoint),
		Codec.FLOAT.fieldOf("inland_point").forGetter(ContinentLerper::inlandPoint)
	).apply(instance, ContinentLerper::new));
	
	@Override
	public float compute(float x, float y, int seed) {
		float continentEdge = this.continentEdge.compute(x, y, seed);
        if (continentEdge < this.shallowOceanPoint) {
        	return this.lerpOcean(x, y, seed, continentEdge);
        }
        if (continentEdge > this.inlandPoint) {
        	return this.land.compute(x, y, seed);
        }
        float alpha = this.interpolation.apply((continentEdge - this.shallowOceanPoint) / (this.inlandPoint - this.shallowOceanPoint));
        float lowerVal = this.lerpOcean(x, y, seed, continentEdge);
        float upperVal = this.land.compute(x, y, seed);
        return NoiseUtil.lerp(lowerVal, upperVal, alpha);
	}

	@Override
	public Codec<ContinentLerper> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new ContinentLerper(this.continentEdge.mapAll(visitor), this.deepOcean.mapAll(visitor), this.shallowOcean.mapAll(visitor), this.coast.mapAll(visitor), this.oceanInterpolation, this.land.mapAll(visitor), this.interpolation, this.deepOceanPoint, this.shallowOceanPoint, this.coastPoint, this.inlandPoint));
	}
	
	private float lerpOcean(float x, float y, int seed, float continentEdge) {
        if (continentEdge < this.deepOceanPoint) {
        	return this.deepOcean.compute(x, y, seed);
        }
        if (continentEdge > this.coastPoint) {
        	return this.coast.compute(x, y, seed);
        }
        if (continentEdge < this.shallowOceanPoint) {
            float alpha = this.interpolation.apply((continentEdge - this.deepOceanPoint) / (this.shallowOceanPoint - this.deepOceanPoint));
            float lowerVal = this.deepOcean.compute(x, y, seed);
            float upperVal = this.shallowOcean.compute(x, y, seed);
            return NoiseUtil.lerp(lowerVal, upperVal, alpha);
        } else {
            float alpha = this.interpolation.apply((continentEdge - this.shallowOceanPoint) / (this.coastPoint - this.shallowOceanPoint));
            float lowerVal = this.shallowOcean.compute(x, y, seed);
            float upperVal = this.coast.compute(x, y, seed);
            return NoiseUtil.lerp(lowerVal, upperVal, alpha);
        }	
	}
}
