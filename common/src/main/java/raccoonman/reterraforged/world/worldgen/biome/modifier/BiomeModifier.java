package raccoonman.reterraforged.world.worldgen.biome.modifier;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import raccoonman.reterraforged.registries.RTFBuiltInRegistries;

public interface BiomeModifier {
    public static final Codec<BiomeModifier> CODEC = RTFBuiltInRegistries.BIOME_MODIFIER_TYPE.byNameCodec().dispatch(BiomeModifier::codec, Function.identity());
    
	Optional<HolderSet<Biome>> biomes();
	
	Codec<? extends BiomeModifier> codec();
}
