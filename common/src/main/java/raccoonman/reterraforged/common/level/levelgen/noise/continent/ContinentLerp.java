package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.common.level.levelgen.continent.ContinentPoints;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public record ContinentLerp(Holder<Noise> continent, Holder<Noise> land, Holder<Noise> ocean, float maxOcean, float oceanThreshold, float beachThreshold, float beachSize) implements Noise {
	public static final Codec<ContinentLerp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.CODEC.fieldOf("continent").forGetter(ContinentLerp::continent),
		Noise.CODEC.fieldOf("land").forGetter(ContinentLerp::land),
		Noise.CODEC.fieldOf("ocean").forGetter(ContinentLerp::ocean),
		Codec.FLOAT.fieldOf("min_height").forGetter(ContinentLerp::maxOcean),
		Codec.FLOAT.fieldOf("ocean_threshold").forGetter(ContinentLerp::oceanThreshold),
		Codec.FLOAT.fieldOf("beach_threshold").forGetter(ContinentLerp::beachThreshold),
		Codec.FLOAT.fieldOf("beach_size").forGetter(ContinentLerp::beachSize)
	).apply(instance, ContinentLerp::new));

	@Override
	public Codec<ContinentLerp> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		float continent = this.continent.value().getValue(x, y, seed);
        if (continent < this.oceanThreshold) {
        	return this.getOcean(x, y, continent, seed);
        } else if (continent < this.beachThreshold) {
        	return this.getBlend(x, y, continent, seed);
        } else {
        	return this.getInland(x, y, continent, seed);
        }
    }

    private float getOcean(float x, float z, float continent, int seed) {
        float rawNoise = this.ocean.value().getValue(x, z, seed);

        //sample.heightNoise = this.levels.toDepthNoise(rawNoise);
        return rawNoise;
    }

    private float getInland(float x, float z, float continent, int seed) {
        float heightNoise = this.land.value().getValue(x, z, seed) ;

//        sample.heightNoise = this.levels.toHeightNoise(sample.baseNoise, heightNoise);
        return heightNoise;
    }

    private float getBlend(float x, float z, float continent, int seed) {
    	if (continent < this.beachThreshold) {
            float lowerRaw = this.ocean.value().getValue(x, z, seed);
            float lower = lowerRaw;//this.levels.toDepthNoise(lowerRaw);
            float alpha = (continent - this.oceanThreshold) / (this.beachSize - this.oceanThreshold);
            
            return NoiseUtil.lerp(lower, this.maxOcean, alpha);
        } else if (continent < ContinentPoints.COAST) {
            float upperRaw = this.land.value().getValue(x, z, seed);
            float upper = upperRaw;//this.levels.toHeightNoise(sample.baseNoise, upperRaw);

            float alpha = (continent - this.beachSize) / (this.beachThreshold - this.beachSize);

            return NoiseUtil.lerp(this.maxOcean, upper, alpha);
        }
		return seed;
    }
}
