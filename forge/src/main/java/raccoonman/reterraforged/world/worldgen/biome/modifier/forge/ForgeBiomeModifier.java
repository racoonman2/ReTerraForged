package raccoonman.reterraforged.world.worldgen.biome.modifier.forge;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifier;

public interface ForgeBiomeModifier extends BiomeModifier, net.minecraftforge.common.world.BiomeModifier {
	Codec<? extends ForgeBiomeModifier> codec();
}
