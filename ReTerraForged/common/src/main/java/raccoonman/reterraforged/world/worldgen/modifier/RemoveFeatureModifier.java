package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.registry.RegistryFilter;

public record RemoveFeatureModifier(RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter, HolderSet<PlacedFeature> features) implements BiomeModifier {
	public static final MapCodec<RemoveFeatureModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> BiomeModifier.addBiomeModifierFields(instance).and(
		PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(RemoveFeatureModifier::features)).apply(instance, RemoveFeatureModifier::new)
	);
	
	@Override
	public void apply(Holder<Biome> holder) {
		Biome biome = holder.value();
		BiomeGenerationSettings generationSettings = biome.getGenerationSettings();
		List<HolderSet<PlacedFeature>> biomeFeatures = generationSettings.features()
			.stream()
			.map((input) -> {
				HolderSet<PlacedFeature> filtered = HolderSet.direct(input.stream().filter((feature) -> {
					return !this.features.contains(feature);
				}).toList());
				return filtered;
			})
			.toList();
		BiomeModifier.updateFeatures(generationSettings, biomeFeatures);
	}

	@Override
	public MapCodec<RemoveFeatureModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
