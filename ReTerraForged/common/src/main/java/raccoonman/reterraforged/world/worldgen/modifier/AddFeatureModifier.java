package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.registry.RegistryFilter;

public record AddFeatureModifier(RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter, InsertionOrder insertionOrder, Map<GenerationStep.Decoration, HolderSet<PlacedFeature>> features) implements BiomeModifier {
	public static final MapCodec<AddFeatureModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> BiomeModifier.addBiomeModifierFields(instance).and(instance.group(
		InsertionOrder.CODEC.fieldOf("insertion_order").forGetter(AddFeatureModifier::insertionOrder),
		Codec.unboundedMap(GenerationStep.Decoration.CODEC, PlacedFeature.LIST_CODEC).fieldOf("features").forGetter(AddFeatureModifier::features)
	)).apply(instance, AddFeatureModifier::new));
	
	@Override
	public void apply(Holder<Biome> holder) {
		Biome biome = holder.value();
		Map<Integer, HolderSet<PlacedFeature>> featuresToAdd = this.features.entrySet()
			.stream()
			.sorted(Comparator.comparingInt((entry) -> entry.getKey().ordinal()))
			.collect(Collectors.toMap((entry) -> entry.getKey().ordinal(), Entry::getValue));
		BiomeGenerationSettings generationSettings = biome.getGenerationSettings();
		List<HolderSet<PlacedFeature>> biomeFeatures = new ArrayList<>(generationSettings.features());
			
		for(Entry<Integer, HolderSet<PlacedFeature>> entry : featuresToAdd.entrySet()) {
			int index = entry.getKey();
				
			while(biomeFeatures.size() <= index) {
				biomeFeatures.add(HolderSet.direct());
			}
				
			HolderSet<PlacedFeature> features = entry.getValue();
			biomeFeatures.set(index, HolderSet.direct(this.insertionOrder.addAll(biomeFeatures.get(index).stream().toList(), features)));
		}
		BiomeModifier.updateFeatures(generationSettings, biomeFeatures);
	}

	@Override
	public MapCodec<AddFeatureModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
