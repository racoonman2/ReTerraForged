package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public record ContinentLerp(Noise continent, Noise land, Noise ocean, float minHeight, float oceanMax, float beachMax, float coastMax) implements Noise {
	public static final Codec<ContinentLerp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("continent").forGetter(ContinentLerp::continent),
		Noise.DIRECT_CODEC.fieldOf("land").forGetter(ContinentLerp::land),
		Noise.DIRECT_CODEC.fieldOf("ocean").forGetter(ContinentLerp::ocean),
		Codec.FLOAT.fieldOf("min_height").forGetter(ContinentLerp::minHeight),
		Codec.FLOAT.fieldOf("ocean_max").forGetter(ContinentLerp::oceanMax),
		Codec.FLOAT.fieldOf("beach_max").forGetter(ContinentLerp::beachMax),
		Codec.FLOAT.fieldOf("coast_max").forGetter(ContinentLerp::coastMax)
	).apply(instance, ContinentLerp::new));

	@Override
	public Codec<ContinentLerp> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		float continent = this.continent.getValue(x, y, seed);
        if (continent < this.oceanMax) {
        	return this.ocean.getValue(x, y, seed);
        } else if (continent < this.beachMax) {
        	float lower = this.ocean.getValue(x, y, seed);
        	float alpha = (continent - this.oceanMax) / (this.beachMax - this.oceanMax);
        	return NoiseUtil.lerp(lower, this.minHeight, alpha);
        } else if (continent < this.coastMax) {
            float upper = this.land.getValue(x, y, seed);
            float alpha = (continent - this.beachMax) / (this.coastMax - this.beachMax);
            return NoiseUtil.lerp(this.minHeight, upper, alpha);
        } else {
        	return this.land.getValue(x, y, seed);
        }
    }
}
