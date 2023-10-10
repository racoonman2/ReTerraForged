package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record RegionSelector(Noise selector, List<Region> regions) implements Noise {
    public static final Codec<RegionSelector> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Noise.HOLDER_HELPER_CODEC.fieldOf("selector").forGetter(RegionSelector::selector),
    	Region.CODEC.listOf().fieldOf("regions").forGetter(RegionSelector::regions)
    ).apply(instance, RegionSelector::new));
	
	public RegionSelector {
        regions = ImmutableList.copyOf(sortByWeight(regions));
	}
    
    @Override
    public float compute(float x, float y, int seed) {
    	float identity = this.selector.compute(x, y, seed);
    	int index = NoiseUtil.round(identity * (this.regions.size() - 1));
        return this.regions.get(index).noise().compute(x, y, seed);
    }
    
    private static List<Region> sortByWeight(List<Region> regions) {
        float smallest = Float.MAX_VALUE;
        for (Region region : regions) {
        	float weight = region.weight();
        	if (weight == 0.0F) {
        		continue;
        	}
        	smallest = Math.min(smallest, weight);
        }
        if (smallest == Float.MAX_VALUE) {
            return regions;
        }
        List<Region> result = new LinkedList<>();
        for (Region region : regions) {
        	float weight = region.weight();
        	if (weight == 0.0F) {
        		continue;
        	}
        	int count = Math.round(weight / smallest);
            while (count-- > 0) {
                result.add(region);
            }
        }
        if (result.isEmpty()) {
            return regions;
        }
        return result;
    }

	@Override
	public Codec<RegionSelector> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new RegionSelector(this.selector.mapAll(visitor), this.regions.stream().map((region) -> region.mapAll(visitor)).toList()));
	}
    
    public record Region(float weight, Noise noise) {
    	public static final Codec<Region> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.FLOAT.fieldOf("weight").forGetter(Region::weight),
    		Noise.HOLDER_HELPER_CODEC.fieldOf("noise").forGetter(Region::noise)
    	).apply(instance, Region::new));
    	
    	public Region mapAll(Visitor visitor) {
			return new Region(this.weight, this.noise.mapAll(visitor));
    	}
    }
}
