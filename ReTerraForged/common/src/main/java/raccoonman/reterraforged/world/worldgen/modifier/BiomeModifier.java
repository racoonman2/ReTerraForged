package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.mixin.accessors.BiomeGenerationSettingsAccessor;
import raccoonman.reterraforged.mixin.accessors.ChunkGeneratorAccessor;
import raccoonman.reterraforged.registry.RegistryFilter;

public interface BiomeModifier extends Modifier {
	RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter();
	
	void apply(Holder<Biome> holder);
	
	@Override
	default void applyModifier(RegistryAccess registryAccess) {
		registryAccess.lookup(Registries.BIOME)
			.stream()
			.flatMap(HolderLookup::listElements)
			.filter(this.filter())
			.forEach(this::apply);
		
		registryAccess.lookup(Registries.LEVEL_STEM)
			.stream()
			.flatMap(HolderLookup::listElements)
			.forEach((holder) -> {
				rebuildFeaturesPerStep(holder.value().generator());
			});
	}

	@Override
	default void applyModifier() {
		// NOOP
	}
	
	public static <P extends BiomeModifier> Products.P1<RecordCodecBuilder.Mu<P>, RegistryFilter<Holder<Biome>, HolderSet<Biome>>> addBiomeModifierFields(RecordCodecBuilder.Instance<P> codec) {
    	return codec.group(RegistryFilter.codec(Biome.LIST_CODEC).fieldOf("filter").forGetter(BiomeModifier::filter));
    }
	
	public static void updateFeatures(BiomeGenerationSettings generationSettings, List<HolderSet<PlacedFeature>> features) {
		if(generationSettings instanceof BiomeGenerationSettingsAccessor mixinGenerationSettings) {
			mixinGenerationSettings.setFeatures(features);
			mixinGenerationSettings.setFlowerFeatures(Suppliers.memoize(() -> features.stream().flatMap(HolderSet::stream).map(Holder::value).flatMap(PlacedFeature::getFeatures).filter(configuredFeature -> configuredFeature.feature() == Feature.FLOWER).collect(ImmutableList.toImmutableList())));
			mixinGenerationSettings.setFeatureSet(Suppliers.memoize(() -> features.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet())));
		}
	}
	
	private static void rebuildFeaturesPerStep(ChunkGenerator generator) {
		if(generator instanceof ChunkGeneratorAccessor accessor) {
			accessor.setFeaturesPerStep(
				Suppliers.memoize(() -> FeatureSorter.buildFeaturesPerStep(
					List.copyOf(generator.getBiomeSource().possibleBiomes()), 
					(holder) -> accessor.getGenerationSettingsGetter().apply(holder).features(), true
				))
			);
		}
	}
}
