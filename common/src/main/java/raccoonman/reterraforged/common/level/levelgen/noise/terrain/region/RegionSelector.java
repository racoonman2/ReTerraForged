package raccoonman.reterraforged.common.level.levelgen.noise.terrain.region;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record RegionSelector(Noise selector, List<RegionSelector.Region> regions) implements Noise {
	public static final Codec<RegionSelector> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("selector").forGetter(RegionSelector::selector),
		RegionSelector.Region.CODEC.listOf().fieldOf("regions").forGetter(RegionSelector::regions)
	).apply(instance, RegionSelector::new));
	
	public RegionSelector {
		regions = ImmutableList.copyOf(getWeighted(regions));
	}
	
	@Override
	public Codec<RegionSelector> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        int index = NoiseUtil.round(this.selector.compute(x, y, seed) * (this.regions.size() - 1));
        return this.regions.get(index).variance().compute(x, y, seed);
	}
	
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new RegionSelector(this.selector.mapAll(visitor), this.regions.stream().map((region) -> {
			return new RegionSelector.Region(region.weight(), region.variance().mapAll(visitor));
		}).toList()));
	}
    
    private static List<RegionSelector.Region> getWeighted(List<RegionSelector.Region> modules) {
        float smallest = Float.MAX_VALUE;
        for (RegionSelector.Region region : modules) {
        	if (region.weight() == 0.0F) {
        		continue;
            }
        	smallest = Math.min(smallest, region.weight());
        }
        if (smallest == Float.MAX_VALUE) {
            return modules;
        }
        List<RegionSelector.Region> result = new LinkedList<>();
        for (RegionSelector.Region region : modules) {
            if (region.weight() == 0.0F) {
            	continue;
            }
            int count = Math.round(region.weight() / smallest);
            while (count-- > 0) {
                result.add(region);
            }
        }
        if (result.isEmpty()) {
            return modules;
        }
        return result;
    }
	
	public record Region(float weight, Noise variance) {
		public static final Codec<RegionSelector.Region> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("weight").forGetter(RegionSelector.Region::weight),
			Noise.HOLDER_HELPER_CODEC.fieldOf("variance").forGetter(RegionSelector.Region::variance)
		).apply(instance, RegionSelector.Region::new));
	}
}
