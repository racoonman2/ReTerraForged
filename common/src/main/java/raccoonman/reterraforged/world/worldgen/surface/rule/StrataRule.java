package raccoonman.reterraforged.world.worldgen.surface.rule;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.surface.RTFSurfaceSystem;

public record StrataRule(ResourceLocation name, Holder<Noise> selector, List<Strata> strata, int iterations) implements SurfaceRules.RuleSource {
	public static final Codec<StrataRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("name").forGetter(StrataRule::name),
		Noise.CODEC.fieldOf("selector").forGetter(StrataRule::selector),
		Strata.CODEC.listOf().fieldOf("strata").forGetter(StrataRule::strata),
		Codec.INT.fieldOf("iterations").forGetter(StrataRule::iterations)
	).apply(instance, StrataRule::new));
	
	public StrataRule {
		strata = ImmutableList.copyOf(strata);
	}
	
	@Override
	public Rule apply(Context ctx) {
		if(ctx.system instanceof RTFSurfaceSystem rtfSurfaceSystem && (Object) ctx.randomState instanceof RTFRandomState rtfRandomState) {
			return new Rule(ctx, rtfRandomState.seed(this.selector.value()), rtfSurfaceSystem.getOrCreateStrata(this.name, this::generateStrata));
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<StrataRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	private List<List<Layer>> generateStrata(RandomSource random) {
        List<List<Layer>> layers = new ArrayList<>();
		for(int i = 0; i < this.iterations; i++) {
			List<Layer> layer = new ArrayList<>();
			for(Strata strata : this.strata) {
				layer.addAll(strata.generateLayers(random));
			}
			layers.add(layer);
		}
        return layers;
	}
	
	public record Strata(TagKey<Block> materials, Holder<Noise> noise, int attempts, int minLayers, int maxLayers, float minDepth, float maxDepth) {
		public static final Codec<Strata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TagKey.hashedCodec(Registries.BLOCK).fieldOf("materials").forGetter(Strata::materials),
			Noise.CODEC.fieldOf("noise").forGetter(Strata::noise),
			Codec.INT.fieldOf("attempts").forGetter(Strata::attempts),
			Codec.INT.fieldOf("min_layers").forGetter(Strata::minLayers),
			Codec.INT.fieldOf("max_layers").forGetter(Strata::maxLayers),
			Codec.FLOAT.fieldOf("min_depth").forGetter(Strata::minDepth),
			Codec.FLOAT.fieldOf("max_depth").forGetter(Strata::maxDepth)			
		).apply(instance, Strata::new));
		
		public List<Layer> generateLayers(RandomSource random) {
			int lastIndex = -1;
	        int layers = this.minLayers + NoiseUtil.round(random.nextFloat() * (this.maxLayers - this.minLayers));
	        List<Layer> result = new ArrayList<>();
	        List<Holder<Block>> materials = Lists.newArrayList(BuiltInRegistries.BLOCK.getTagOrEmpty(this.materials));
	        
	        int seed = random.nextInt();
	        for (int i = 0; i < layers; i++) {
	            int attempts = this.attempts;
	            int index = random.nextInt(materials.size());
	            while (--attempts >= 0 && index == lastIndex) {
	                index = random.nextInt(materials.size());
	            }
	            if (index != lastIndex) {
	                lastIndex = index;
	                BlockState material = materials.get(index).value().defaultBlockState();
	                float depth = this.minDepth + random.nextFloat() * (this.maxDepth - this.minDepth);
	                result.add(new Layer(material, Noises.shiftSeed(Noises.mul(this.noise.value(), depth), random.nextInt()), seed));
	            }
	        }
	        return result;
		}
	}
	
	// this has to be public so that SurfaceSystemExtension can access it
	// should be private otherwise
	public record Layer(BlockState material, Noise depth, int seed) {
	
		public float computeDepth(float x, float z) {
			return this.depth.compute(x, z, this.seed);
		}
	}
	
	private class Rule implements SurfaceRules.SurfaceRule {
		private Context surfaceContext;
		private Noise selector;
		private List<List<Layer>> strata;
		private List<Layer> layers;
		private float[] depthBuffer;
		private long lastUpdateXZ;
		
		public Rule(Context surfaceContext, Noise selector, List<List<Layer>> strata) {
			this.surfaceContext = surfaceContext;
			this.selector = selector;
			this.strata = strata;
			this.lastUpdateXZ = Long.MIN_VALUE;
		}
		
        @Nullable
		@Override
		public BlockState tryApply(int x, int y, int z) {
        	if(this.lastUpdateXZ != this.surfaceContext.lastUpdateXZ) {
        		this.initBuffer(x, z);
        		this.lastUpdateXZ = this.surfaceContext.lastUpdateXZ;
        	}
        	
        	Layer last = null;
        	for(int i = 0; i < this.layers.size(); i++) {
        		Layer layer = last = this.layers.get(i);
        		if(y > this.depthBuffer[i]) {
        			return layer.material();
        		}
        	}
        	
        	return last != null ? last.material() : null;
		}
		
		private void initBuffer(int x, int z) {
        	this.layers = this.selectLayers(x, z);
			int layerCount = this.layers.size();
			
	        if (this.depthBuffer == null || this.depthBuffer.length < layerCount) {
	            this.depthBuffer = new float[layerCount];
	        }
	        
            int localX = this.surfaceContext.blockX & 0xF;
            int localZ = this.surfaceContext.blockZ & 0xF;
            int height = this.surfaceContext.chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, localX, localZ);

            float sum = 0.0F;
            for(int i = 0; i < layerCount; i++) {
            	Layer layer = this.layers.get(i);
            	float depth = layer.computeDepth(x, z);
            	sum += depth;
            	this.depthBuffer[i] = depth;
            }
            
            int y = height;
            for(int i = 0; i < layerCount; i++) {
            	this.depthBuffer[i] = y -= Math.round((this.depthBuffer[i] / sum) * height);
            }
		}
		
		private List<Layer> selectLayers(int x, int z) {
			float selector = this.selector.compute(x, z, 0);
	        int index = (int) (selector * this.strata.size());
	        index = Math.min(this.strata.size() - 1, index);
	        return this.strata.get(index);
	    }
	}
}