package raccoonman.reterraforged.common.level.levelgen.climate;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseTree;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseTree.Point;

public record ClimatePoint(Holder<Climate> climate, NoiseTree.Parameter temperature, NoiseTree.Parameter moisture, NoiseTree.Parameter continentalness, NoiseTree.Parameter height, NoiseTree.Parameter river, NoiseTree.Parameter depth) implements Point<Holder<Climate>> {
	public static final Codec<ClimatePoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Climate.CODEC.fieldOf("climate").forGetter(ClimatePoint::climate),
		NoiseTree.Parameter.CODEC.optionalFieldOf("temperature", NoiseTree.Parameter.ignore()).forGetter(ClimatePoint::temperature),
		NoiseTree.Parameter.CODEC.optionalFieldOf("moisture", NoiseTree.Parameter.ignore()).forGetter(ClimatePoint::moisture),
		NoiseTree.Parameter.CODEC.optionalFieldOf("continentalness", NoiseTree.Parameter.ignore()).forGetter(ClimatePoint::continentalness),
		NoiseTree.Parameter.CODEC.optionalFieldOf("river", NoiseTree.Parameter.ignore()).forGetter(ClimatePoint::river),
		NoiseTree.Parameter.CODEC.optionalFieldOf("depth", NoiseTree.Parameter.ignore()).forGetter(ClimatePoint::depth),
		NoiseTree.Parameter.CODEC.optionalFieldOf("weirdness", NoiseTree.Parameter.ignore()).forGetter(ClimatePoint::height)
	).apply(instance, ClimatePoint::new));
	
	@Override
	public Holder<Climate> value() {
		return this.climate;
	}

	@Override
	public List<NoiseTree.Parameter> parameterSpace() {
		return ImmutableList.of(this.temperature, this.moisture, this.continentalness, this.height, this.river, this.depth);
	}

	public static ClimatePoint of(Holder<Climate> climate, float temperature, float moisture, float continentalness, float height, float river, float depth) {
		return new ClimatePoint(climate, NoiseTree.Parameter.point(temperature), NoiseTree.Parameter.point(moisture), NoiseTree.Parameter.point(continentalness), NoiseTree.Parameter.point(height), NoiseTree.Parameter.point(river), NoiseTree.Parameter.point(depth));
	}

	public static ClimatePoint of(Holder<Climate> climate, NoiseTree.Parameter temperature, NoiseTree.Parameter moisture, NoiseTree.Parameter continentalness, NoiseTree.Parameter height, NoiseTree.Parameter river, NoiseTree.Parameter depth) {
		return new ClimatePoint(climate, temperature, moisture, continentalness, height, river, depth);
	}
}