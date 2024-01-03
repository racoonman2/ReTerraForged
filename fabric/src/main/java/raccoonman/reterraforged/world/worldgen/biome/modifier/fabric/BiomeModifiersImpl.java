package raccoonman.reterraforged.world.worldgen.biome.modifier.fabric;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registries.RTFBuiltInRegistries;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifier;
import raccoonman.reterraforged.world.worldgen.biome.modifier.Order;

public class BiomeModifiersImpl {
	
	public static void bootstrap() {
		register("add", AddModifier.CODEC);
	}
	
	public static BiomeModifier add(Order order, GenerationStep.Decoration step, Optional<HolderSet<Biome>> biomes, HolderSet<PlacedFeature> features) {
		return new AddModifier(order, step, biomes, features);
	}
	
	public static void register(String name, Codec<? extends BiomeModifier> value) {
		RegistryUtil.register(RTFBuiltInRegistries.BIOME_MODIFIER_TYPE, name, value);
	}
}
