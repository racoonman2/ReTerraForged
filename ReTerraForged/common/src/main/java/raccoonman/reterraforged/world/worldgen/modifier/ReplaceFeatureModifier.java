package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
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

public record ReplaceFeatureModifier(RegistryFilter<Holder<Biome>, HolderSet<Biome>> filter, Map<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> replacements) implements BiomeModifier {
	public static final MapCodec<ReplaceFeatureModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> BiomeModifier.addBiomeModifierFields(instance).and(
		Codec.unboundedMap(ResourceKey.codec(Registries.PLACED_FEATURE), PlacedFeature.CODEC).fieldOf("replacements").forGetter(ReplaceFeatureModifier::replacements)
	).apply(instance, ReplaceFeatureModifier::new));
	
	@Override
	public void apply(Holder<Biome> holder) {
		Biome biome = holder.value();
		BiomeGenerationSettings generationSettings = biome.getGenerationSettings();
		List<HolderSet<PlacedFeature>> featuresByStage = new ArrayList<>(generationSettings.features());
		for(int stage = 0; stage < featuresByStage.size(); stage++) {
			List<Holder<PlacedFeature>> stageFeatures = Lists.newArrayList(featuresByStage.get(stage));
			for(int i = 0; i < stageFeatures.size(); i++) {
				Optional<ResourceKey<PlacedFeature>> key = stageFeatures.get(i).unwrapKey();
				if(key.isPresent()) {
					@Nullable
					Holder<PlacedFeature> replacement = this.replacements.get(key.get());
					if(replacement != null) {
						stageFeatures.set(i, replacement);
					}
				}
			}
			featuresByStage.set(stage, HolderSet.direct(stageFeatures));
		}
		BiomeModifier.updateFeatures(generationSettings, featuresByStage);
	}

	@Override
	public MapCodec<ReplaceFeatureModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
