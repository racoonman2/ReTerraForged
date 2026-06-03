package raccoonman.reterraforged.world.worldgen.feature;

import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class PerBiomeFeature extends Feature<PerBiomeFeature.Config> {

	public PerBiomeFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> ctx) {
		WorldGenLevel level = ctx.level();
		ChunkGenerator generator = ctx.chunkGenerator();
		RandomSource random = ctx.random();
		BlockPos pos = ctx.origin();
		Config config = ctx.config();
		Holder<Biome> biome = level.getBiome(pos);
		Optional<ResourceKey<Biome>> key = biome.unwrapKey();
		if(key.isPresent()) {
			return config.getFeature(key.get()).value().place(level, generator, random, pos);
		}
		return false;
	}

	public record Config(Map<ResourceKey<Biome>, Holder<ConfiguredFeature<?, ?>>> features, Holder<ConfiguredFeature<?, ?>> fallback) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(ResourceKey.codec(Registries.BIOME), ConfiguredFeature.CODEC).fieldOf("features").forGetter(Config::features),
			ConfiguredFeature.CODEC.fieldOf("fallback").forGetter(Config::fallback)
		).apply(instance, Config::new));
		
		public Holder<ConfiguredFeature<?, ?>> getFeature(ResourceKey<Biome> key) {
			return this.features.getOrDefault(key, this.fallback);
		}
	}
}
