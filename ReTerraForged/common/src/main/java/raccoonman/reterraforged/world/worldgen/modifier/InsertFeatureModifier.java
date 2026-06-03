package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.registry.RegistryFilter;

public record InsertFeatureModifier(RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter, ResourceKey<PlacedFeature> target, HolderSet<PlacedFeature> additions) implements BiomeModifier {
	public static final MapCodec<InsertFeatureModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> BiomeModifier.addBiomeModifierFields(instance).and(instance.group(
		ResourceKey.codec(Registries.PLACED_FEATURE).fieldOf("target").forGetter(InsertFeatureModifier::target),
		PlacedFeature.LIST_CODEC.fieldOf("additions").forGetter(InsertFeatureModifier::additions)
	)).apply(instance, InsertFeatureModifier::new));

	@Override
	public void apply(Holder<Biome> holder) {
		Biome biome = holder.value();
		BiomeGenerationSettings generationSettings = biome.getGenerationSettings();
		List<HolderSet<PlacedFeature>> biomeFeatures = new ArrayList<>(generationSettings.features());

		for(int step = 0; step < biomeFeatures.size(); step++) {
			HolderSet<PlacedFeature> features = biomeFeatures.get(step);
			List<Holder<PlacedFeature>> newFeatures = new ArrayList<>();
			for(Holder<PlacedFeature> feature : features) {
				newFeatures.add(feature);
				feature.unwrapKey().filter((key) -> key.equals(this.target)).ifPresent((v) -> {
					for(Holder<PlacedFeature> addition : this.additions) {
						newFeatures.add(addition);
					}
				});
			}
			biomeFeatures.set(step, HolderSet.direct(newFeatures));
		}
		
		BiomeModifier.updateFeatures(generationSettings, biomeFeatures);
	}

	@Override
	public MapCodec<InsertFeatureModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
