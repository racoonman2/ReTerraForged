package raccoonman.reterraforged.common.level.levelgen.noise;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;

public record ThresholdList(Noise selector, List<ThresholdList.Point> points) implements Noise {
	public static final Codec<ThresholdList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("selector").forGetter(ThresholdList::selector),
		ExtraCodecs.nonEmptyList(ThresholdList.Point.CODEC.listOf()).fieldOf("points").forGetter(ThresholdList::points)
	).apply(instance, ThresholdList::new));

	public ThresholdList {
		Validate.notEmpty(points);
		
		points = ImmutableList.copyOf(points);
	}
	
	@Override
	public Codec<ThresholdList> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		float selector = this.selector.compute(x, y, seed);
		
		for(int i = 0; i < this.points.size(); i++) {
			ThresholdList.Point point = this.points.get(i);
			if(selector < point.threshold().compute(x, y, seed)) {
	            return point.noise().compute(x, y, seed);
			}
		}
		
		return this.points.get(this.points.size() - 1).noise().compute(x, y, seed);
	}
	
	public record Point(Noise noise, Noise threshold) {
		public static final Codec<ThresholdList.Point> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Noise.HOLDER_HELPER_CODEC.fieldOf("noise").forGetter(ThresholdList.Point::noise),
			Noise.HOLDER_HELPER_CODEC.fieldOf("threshold").forGetter(ThresholdList.Point::threshold)		
		).apply(instance, ThresholdList.Point::new));
	}
}
