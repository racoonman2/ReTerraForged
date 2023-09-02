package raccoonman.reterraforged.common.level.levelgen.noise.terrain.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public record RegionSelector(Noise selector, WeightMap<Noise> regions) implements Noise {
	public static final Codec<RegionSelector> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("selector").forGetter(RegionSelector::selector),
		WeightMap.codec(Noise.HOLDER_HELPER_CODEC).fieldOf("regions").forGetter(RegionSelector::regions)
	).apply(instance, RegionSelector::new));
	
	@Override
	public Codec<RegionSelector> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		return this.regions.getValue(this.selector.compute(x, y, seed)).compute(x, y, seed);
	}
}
