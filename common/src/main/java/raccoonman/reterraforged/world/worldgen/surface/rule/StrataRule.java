package raccoonman.reterraforged.world.worldgen.surface.rule;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.tags.RTFBlockTags;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.surface.RTFSurfaceSystem;
import raccoonman.reterraforged.world.worldgen.surface.rule.StrataRule.Layer;
import raccoonman.reterraforged.world.worldgen.tile.Tile;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

public record StrataRule(ResourceLocation cacheId, int buffer, int iterations, Holder<Noise> selector, List<Layer> layers) implements SurfaceRules.RuleSource {
	public static final Codec<StrataRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("cache_id").forGetter(StrataRule::cacheId),
		Codec.INT.fieldOf("buffer").forGetter(StrataRule::buffer),
		Codec.INT.fieldOf("iterations").forGetter(StrataRule::iterations),
		Noise.CODEC.fieldOf("selector").forGetter(StrataRule::selector),
		Layer.CODEC.listOf().fieldOf("layers").forGetter(StrataRule::layers)
	).apply(instance, StrataRule::new));

	@Override
	public Rule apply(Context ctx) {
		if((Object) ctx.system instanceof RTFSurfaceSystem rtfSurfaceSystem) {
			return new Rule(ctx, rtfSurfaceSystem.getOrCreateStrata(this.cacheId, this::generate));
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<StrataRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	public List<Strata> generate(RandomSource random) {
		List<Strata> strata = new ArrayList<>(this.iterations);
		for(int i = 0; i < this.iterations; i++) {
			strata.add(this.generateStrata(random));
		}
		return strata;
	}
	
	private Strata generateStrata(RandomSource random) {
		List<Stratum> stratum = new ArrayList<>();
		for(Layer layer : this.layers) {
			HolderSet<Block> materials = BuiltInRegistries.BLOCK.getTag(layer.materials()).orElseThrow();
			int layerCount = layer.layers(random.nextFloat());
	        int lastIndex = -1;
	        for (int i = 0; i < layerCount; i++) {
	            int attempts = layer.attempts();
	            int index = random.nextInt(materials.size());
	            while (--attempts >= 0 && index == lastIndex) {
	                index = random.nextInt(materials.size());
	            }
	            if (index != lastIndex) {
	                lastIndex = index;
	                BlockState material = materials.get(index).value().defaultBlockState();
	                float depth = layer.depth(random.nextFloat());
	                stratum.add(new Stratum(material, Noises.mul(Noises.perlin(random.nextInt(), 128, 3), depth)));
	            }
	        }
		}
		return new Strata(stratum);
	}
	
	public class Rule implements SurfaceRules.SurfaceRule {
		private Context context;
		private List<Strata> strataEntries;
		private Levels levels;
		private Tile.Chunk chunk;
		private Strata strata;
		@Nullable
		private Stratum bufferMaterial;
		private int height;
		private int index;
		private float[] depthBuffer;
		private long lastXZ;
		
		public Rule(Context context, List<Strata> strataEntries) {
			this.context = context;
			this.strataEntries = strataEntries;
			
			if((Object) context.randomState instanceof RTFRandomState rtfRandomState) {
				ChunkPos chunkPos = context.chunk.getPos();
				
				GeneratorContext generatorContext = rtfRandomState.generatorContext();
				this.levels = generatorContext.levels;
				this.chunk = generatorContext.cache.provideChunk(chunkPos.x, chunkPos.z);
			} else {
				throw new IllegalStateException();
			}
		}
		
	    private Strata selectStrata(int x, int z) {
	    	float value = StrataRule.this.selector.value().compute(x, z, 0);
	        int index = (int)(value * this.strataEntries.size());
	        index = Math.min(this.strataEntries.size() - 1, index);
	        return this.strataEntries.get(index);
	    }

		private void update(int x, int z) {
			Strata strata = this.selectStrata(x, z);
			
			if(this.strata != strata) {
				this.strata = strata;
				
				for(Stratum stratum : this.strata.stratum()) {
					if(stratum.state().is(RTFBlockTags.ROCK)) {
						this.bufferMaterial = stratum;
						break;
					}	
				}
			}
			
			List<Stratum> stratum = this.strata.stratum();
			int stratumCount = stratum.size();
			
			if(this.depthBuffer == null || this.depthBuffer.length < stratumCount) {
				this.depthBuffer = new float[stratumCount];
			}

			float sum = 0.0F;
			for(int i = 0; i < stratumCount; i++) {
				float depth = stratum.get(i).depth().compute(x, z, 0);
				sum += depth;
				this.depthBuffer[i] = depth;
			}
			
			this.height = this.levels.scale(this.chunk.getCell(x, z).height);
			
			this.index = 0;
			
			int dy = this.height;
			int stoneStart = this.height - this.context.stoneDepthAbove + 1;
			for(int i = 0; i < stratumCount; i++) {
				this.depthBuffer[i] = dy -= NoiseUtil.round((this.depthBuffer[i] / sum) * this.height);
				
//				if(dy <= stoneStart && this.index == 0) {
//					this.index = i;
//				}
			}
		}
		
		@Override
		public BlockState tryApply(int x, int y, int z) {
			long packedPos = PosUtil.pack(x, z);
			if(this.lastXZ != packedPos) {
				this.update(x, z);
				this.lastXZ = packedPos;
			}
			
			if(StrataRule.this.buffer != 0 && y > this.height - StrataRule.this.buffer) {
				return this.bufferMaterial.state();
			}
			
			
			List<Stratum> stratum = this.strata.stratum;
			
			while(y < this.depthBuffer[this.index] && this.index + 1 < stratum.size()) {
				this.index++;
			}
			
//			for(int i = 0; i < stratum.size(); i++) {
//				if(y > this.depthBuffer[i]) {
//					BlockState state = stratum.get(i).state();
//					if(StrataRule.this.buffer != 0 && !state.is(RTFBlockTags.ROCK)) {
//						continue;
//					}
//					return state;
//				}
//			}
			return stratum.get(this.index).state;
		}
	}
	
	public record Buffer(int size, TagKey<Block> materials) {
		public static final Codec<Buffer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("size").forGetter(Buffer::size),
			TagKey.hashedCodec(Registries.BLOCK).fieldOf("materials").forGetter(Buffer::materials)
		).apply(instance, Buffer::new));
	}
	
	public record Strata(List<Stratum> stratum) {
	}
	
	public record Stratum(BlockState state, Noise depth) {
	}
	
	public record Layer(TagKey<Block> materials, Holder<Noise> depth, int attempts, int minLayers, int maxLayers, float minDepth, float maxDepth) {
		public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TagKey.hashedCodec(Registries.BLOCK).fieldOf("materials").forGetter(Layer::materials),
			Noise.CODEC.fieldOf("depth").forGetter(Layer::depth),
			Codec.INT.fieldOf("attempts").forGetter(Layer::attempts),
			Codec.INT.fieldOf("min_layers").forGetter(Layer::minLayers),
			Codec.INT.fieldOf("max_layers").forGetter(Layer::maxLayers),
			Codec.FLOAT.fieldOf("min_depth").forGetter(Layer::minDepth),
			Codec.FLOAT.fieldOf("max_depth").forGetter(Layer::maxDepth)
		).apply(instance, Layer::new));
		
        public int layers(float f) {
            int range = this.maxLayers - this.minLayers;
            return this.minLayers + NoiseUtil.round(f * range);
        }
        
        public float depth(float f) {
            float range = this.maxDepth - this.minDepth;
            return this.minDepth + f * range;
        }
	}
}
