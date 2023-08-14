package raccoonman.reterraforged.common.level.levelgen.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public record Climate(WeightMap<Holder<Biome>> biomes) {
	public static final Codec<Climate> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WeightMap.codec(Biome.CODEC).fieldOf("biomes").forGetter(Climate::biomes)
	).apply(instance, Climate::new));
	public static final Codec<Holder<Climate>> CODEC = RegistryFileCodec.create(RTFRegistries.CLIMATE, DIRECT_CODEC);
	public static final Codec<HolderSet<Climate>> LIST_CODEC = RegistryCodecs.homogeneousList(RTFRegistries.CLIMATE, DIRECT_CODEC);

	public Climate {
		if(biomes.isEmpty()) {
			throw new IllegalStateException("Climate biome list cannot be empty!");
		}
	}
}
