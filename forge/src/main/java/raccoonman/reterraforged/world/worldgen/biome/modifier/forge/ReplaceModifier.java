package raccoonman.reterraforged.world.worldgen.biome.modifier.forge;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import raccoonman.reterraforged.forge.mixin.MixinBiomeGenerationSettingsPlainsBuilder;

record ReplaceModifier(GenerationStep.Decoration step, Optional<HolderSet<Biome>> biomes, Map<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> replacements) implements ForgeBiomeModifier {
	public static final Codec<ReplaceModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(ReplaceModifier::step),
		Biome.LIST_CODEC.optionalFieldOf("biomes").forGetter(ReplaceModifier::biomes),
		Codec.unboundedMap(ResourceKey.codec(Registries.PLACED_FEATURE), PlacedFeature.CODEC).fieldOf("replacements").forGetter(ReplaceModifier::replacements)
	).apply(instance, ReplaceModifier::new));
	
	@Override
	public void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder) {
		if(phase == Phase.AFTER_EVERYTHING) {
			if(builder.getGenerationSettings() instanceof MixinBiomeGenerationSettingsPlainsBuilder builderAccessor) {
				if(this.biomes.isPresent() && !this.biomes.get().contains(biome)) {
					return;
				}
				
				List<List<Holder<PlacedFeature>>> featureSteps = builderAccessor.getFeatures();
				int index = this.step.ordinal();
	
				while (index >= featureSteps.size()) {
					featureSteps.add(Collections.emptyList());
				}

				featureSteps.get(index).replaceAll((f) -> {
					return f.unwrapKey().map(this.replacements::get).orElse(f);
				});
			} else {
				throw new IllegalStateException();
			}
		}
	}

	@Override
	public Codec<ReplaceModifier> codec() {
		return CODEC;
	}
}
