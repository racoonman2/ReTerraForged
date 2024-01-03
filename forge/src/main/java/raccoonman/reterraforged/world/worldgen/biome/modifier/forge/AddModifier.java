package raccoonman.reterraforged.world.worldgen.biome.modifier.forge;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import raccoonman.reterraforged.world.worldgen.biome.modifier.Order;

record AddModifier(Order order, GenerationStep.Decoration step, Optional<HolderSet<Biome>> biomes, HolderSet<PlacedFeature> features) implements ForgeBiomeModifier {
	public static final Codec<AddModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Order.CODEC.fieldOf("order").forGetter(AddModifier::order),
		GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddModifier::step),
		Biome.LIST_CODEC.optionalFieldOf("biomes").forGetter(AddModifier::biomes),
		PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddModifier::features)
	).apply(instance, AddModifier::new));
	
	@Override
	public void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder) {
		builder.getGenerationSettings().getFeatures(step);
		
	}

	@Override
	public Codec<AddModifier> codec() {
		return CODEC;
	}
}
