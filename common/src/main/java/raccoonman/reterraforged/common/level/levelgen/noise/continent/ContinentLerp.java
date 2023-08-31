package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record ContinentLerp(Noise continent, Noise landNoise, Noise oceanNoise, float minHeight, float oceanMax, float beachMax, float coastMax) implements Noise {
	public static final Codec<ContinentLerp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("continent").forGetter(ContinentLerp::continent),
		Noise.HOLDER_HELPER_CODEC.fieldOf("land").forGetter(ContinentLerp::landNoise),
		Noise.HOLDER_HELPER_CODEC.fieldOf("ocean").forGetter(ContinentLerp::oceanNoise),
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
        	return this.oceanNoise.getValue(x, y, seed);
        } else if (continent < this.beachMax) {
        	float lower = this.oceanNoise.getValue(x, y, seed);
        	float alpha = (continent - this.oceanMax) / (this.beachMax - this.oceanMax);
        	return NoiseUtil.lerp(lower, this.minHeight, alpha);
        } else if (continent < this.coastMax) {
            float upper = this.landNoise.getValue(x, y, seed);
            float alpha = (continent - this.beachMax) / (this.coastMax - this.beachMax);
            return NoiseUtil.lerp(this.minHeight, upper, alpha);
        } else {
        	return this.landNoise.getValue(x, y, seed);
        }
    }
}
