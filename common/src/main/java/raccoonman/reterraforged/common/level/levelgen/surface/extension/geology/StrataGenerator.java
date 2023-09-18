package raccoonman.reterraforged.common.level.levelgen.surface.extension.geology;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record StrataGenerator(List<Layer> layers, int iterationCount) {
	public static final Codec<StrataGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Layer.CODEC.listOf().fieldOf("layers").forGetter(StrataGenerator::layers),
		Codec.INT.fieldOf("iteration_count").forGetter(StrataGenerator::iterationCount)
	).apply(instance, StrataGenerator::new));
	
	public List<Strata> generate() {
    	List<Strata> strata = new ArrayList<>();
        for (int i = 0; i < this.iterationCount; i++) {
        	for(Layer layerGenerator : this.layers) {
        		strata.add(layerGenerator.generate(i));
        	}
        }
		return ImmutableList.copyOf(strata);
	}
	
	//TODO don't take a seed parameter here
	//can we use RandomSource for this?
	public record Layer(TagKey<Block> materials, int seed, int attempts, Noise depth, int minLayerCount, int maxLayerCount, float minDepth, float maxDepth) {
		public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TagKey.hashedCodec(Registries.BLOCK).fieldOf("materials").forGetter(Layer::materials),
			Codec.INT.fieldOf("seed").forGetter(Layer::seed),
			Codec.INT.fieldOf("attempts").forGetter(Layer::attempts),
			Noise.HOLDER_HELPER_CODEC.fieldOf("depth").forGetter(Layer::depth),
			Codec.INT.fieldOf("min_layer_count").forGetter(Layer::minLayerCount),
			Codec.INT.fieldOf("max_layer_count").forGetter(Layer::maxLayerCount),
			Codec.FLOAT.fieldOf("min_depth").forGetter(Layer::minDepth),
			Codec.FLOAT.fieldOf("max_depth").forGetter(Layer::maxDepth)
		).apply(instance, Layer::new));
		
		public Strata generate(int seedOffset) {
			List<Stratum> stratum = new LinkedList<>();
			HolderSet<Block> materials = BuiltInRegistries.BLOCK.getTag(this.materials).orElseThrow();
			int seed = this.seed + seedOffset;
	        Random random = new Random(seed);
	        int lastIndex = -1;
	        int layerCount = lerpLayerCount(random.nextFloat(), this.minLayerCount, this.maxLayerCount);
	        int materialCount = materials.size();
	        for (int i = 0; i < layerCount; i++) {
	            int attempts = this.attempts;
	            int index = random.nextInt(materialCount);
	            while (--attempts >= 0 && index == lastIndex) {
	                index = random.nextInt(materialCount);
	            }
	            if (index != lastIndex) {
	                lastIndex = index;
	                BlockState material = materials.get(index).value().defaultBlockState();
	                float depth = lerpDepth(random.nextFloat(), this.minDepth, this.maxDepth);
	                Noise module = this.depth.shift(seed).scale(depth);
	                stratum.add(new Stratum(material, new NoiseWrapper.Marker(module)));
	            }
	        }
	        return new Strata(stratum);
		}

	    private static int lerpLayerCount(float value, int minLayers, int maxLayers) {
	        int range = maxLayers - minLayers;
	        return minLayers + NoiseUtil.round(value * range);
	    }

	    private static float lerpDepth(float value, float minDepth, float maxDepth) {
	        float range = maxDepth - minDepth;
	        return minDepth + value * range;
	    }
	}
}
