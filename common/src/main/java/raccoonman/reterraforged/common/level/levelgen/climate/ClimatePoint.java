package raccoonman.reterraforged.common.level.levelgen.climate;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseTree;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseTree.Point;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.DeferredHolder;
import raccoonman.reterraforged.common.util.storage.WeightMap;

// TODO don't have separate fields for tag and climate
public record ClimatePoint(Optional<TagKey<Climate>> tag, Holder<Climate> climate, NoiseTree.Parameter temperature, NoiseTree.Parameter moisture, NoiseTree.Parameter continentalness, NoiseTree.Parameter height, NoiseTree.Parameter river, NoiseTree.Parameter depth) implements Point<Holder<Climate>> {
	public static final Codec<ClimatePoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryOps.retrieveGetter(RTFRegistries.CLIMATE),
		ExtraCodecs.xor(TagKey.hashedCodec(RTFRegistries.CLIMATE), Climate.CODEC).fieldOf("climate").forGetter((point) -> {
			return point.tag.map((tag) -> {
				return Either.<TagKey<Climate>, Holder<Climate>>left(tag);
			}).orElse(Either.right(point.climate));
		}), 
		NoiseTree.Parameter.CODEC.fieldOf("temperature").forGetter(ClimatePoint::temperature), 
		NoiseTree.Parameter.CODEC.fieldOf("moisture").forGetter(ClimatePoint::moisture), 
		NoiseTree.Parameter.CODEC.fieldOf("continentalness").forGetter(ClimatePoint::continentalness), 
		NoiseTree.Parameter.CODEC.fieldOf("height").forGetter(ClimatePoint::height), 
		NoiseTree.Parameter.CODEC.fieldOf("river").forGetter(ClimatePoint::river),
		NoiseTree.Parameter.CODEC.fieldOf("depth").forGetter(ClimatePoint::depth)
	).apply(instance, ClimatePoint::of));

	@Override
	public Holder<Climate> value() {
		return this.climate;
	}

	@Override
	public List<NoiseTree.Parameter> parameterSpace() {
		return ImmutableList.of(this.temperature, this.moisture, this.continentalness, this.height, this.river, this.depth);
	}

	public static ClimatePoint of(HolderGetter<Climate> climates, Either<TagKey<Climate>, Holder<Climate>> climate, float temperature, float moisture, float continentalness, float height, float river, float depth) {
		return new ClimatePoint(climate.left(), mergeClimates(climates, climate), NoiseTree.Parameter.point(temperature), NoiseTree.Parameter.point(moisture), NoiseTree.Parameter.point(continentalness), NoiseTree.Parameter.point(height), NoiseTree.Parameter.point(river), NoiseTree.Parameter.point(depth));
	}

	public static ClimatePoint of(HolderGetter<Climate> climates, Either<TagKey<Climate>, Holder<Climate>> climate, NoiseTree.Parameter temperature, NoiseTree.Parameter moisture, NoiseTree.Parameter continentalness, NoiseTree.Parameter height, NoiseTree.Parameter river, NoiseTree.Parameter depth) {
		return new ClimatePoint(climate.left(), mergeClimates(climates, climate), temperature, moisture, continentalness, height, river, depth);
	}
	
	private static Holder<Climate> mergeClimates(HolderGetter<Climate> climates, Either<TagKey<Climate>, Holder<Climate>> climate) {
		return climate.right().orElseGet(() -> {
			return new DeferredHolder<>(() -> {
				TagKey<Climate> tag = climate.left().get();
				HolderSet<Climate> set = climates.getOrThrow(tag);
				WeightMap.Builder<Holder<Biome>> biomes = new WeightMap.Builder<>();
				for(Holder<Climate> holder : set) {
					biomes.addAll(holder.value().biomes());
				}
				return Holder.direct(new Climate(biomes.build()));
			});
		});
	}
}