package raccoonman.reterraforged.world.worldgen.biome.modifier.fabric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

record ReplaceModifier(GenerationStep.Decoration step, Optional<HolderSet<Biome>> biomes, Map<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> replacements) implements FabricBiomeModifier {
	public static final Codec<ReplaceModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(ReplaceModifier::step),
		Biome.LIST_CODEC.optionalFieldOf("biomes").forGetter(ReplaceModifier::biomes),
		Codec.unboundedMap(ResourceKey.codec(Registries.PLACED_FEATURE), PlacedFeature.CODEC).fieldOf("replacements").forGetter(ReplaceModifier::replacements)
	).apply(instance, ReplaceModifier::new));

	@Override
	public void apply(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
		if(this.biomes.isPresent() && !this.biomes.get().contains(selectionContext.getBiomeRegistryEntry())) {
			return;
		}
		
		BiomeGenerationSettings generationSettings = selectionContext.getBiome().getGenerationSettings();
		List<HolderSet<PlacedFeature>> featureSteps = generationSettings.features();
		int index = this.step.ordinal();

		while (index >= featureSteps.size()) {
			featureSteps.add(HolderSet.direct());
		}

		featureSteps.set(index, this.replace(featureSteps.get(index)));
		
		this.rebuildFlowerFeatures(generationSettings);
	}
	
	private HolderSet<PlacedFeature> replace(HolderSet<PlacedFeature> features) {
		List<Holder<PlacedFeature>> newList = new ArrayList<>(features.stream().toList());
		newList.replaceAll((f) -> {
			return f.unwrapKey().map(this.replacements::get).orElse(f);
		});
		return HolderSet.direct(newList);
	}

	@Override
	public Codec<ReplaceModifier> codec() {
		return CODEC;
	}
}
