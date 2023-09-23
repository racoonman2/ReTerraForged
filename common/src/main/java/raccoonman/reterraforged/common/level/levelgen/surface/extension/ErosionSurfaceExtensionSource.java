package raccoonman.reterraforged.common.level.levelgen.surface.extension;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.FlatCache;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;
import raccoonman.reterraforged.common.level.levelgen.density.Slope;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.worldgen.data.tags.RTFBlockTags;

//TODO this doesnt respect preset settings
public record ErosionSurfaceExtensionSource(List<MaterialSource> materials, Holder<DensityFunction> height, int seaLevel, int yScale, float heightModifier, float slopeModifier, boolean erodeSnow) implements SurfaceExtensionSource {
	public static final Codec<ErosionSurfaceExtensionSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		MaterialSource.CODEC.listOf().fieldOf("materials").forGetter(ErosionSurfaceExtensionSource::materials),
		DensityFunction.CODEC.fieldOf("height").forGetter(ErosionSurfaceExtensionSource::height),
		Codec.INT.fieldOf("sea_level").forGetter(ErosionSurfaceExtensionSource::seaLevel),
		Codec.INT.fieldOf("y_scale").forGetter(ErosionSurfaceExtensionSource::yScale),
		Codec.FLOAT.fieldOf("height_modifier").forGetter(ErosionSurfaceExtensionSource::heightModifier),
		Codec.FLOAT.fieldOf("slope_modifier").forGetter(ErosionSurfaceExtensionSource::slopeModifier),
		Codec.BOOL.fieldOf("erode_snow").forGetter(ErosionSurfaceExtensionSource::erodeSnow)
	).apply(instance, ErosionSurfaceExtensionSource::new));
	
	public ErosionSurfaceExtensionSource {
		materials = ImmutableList.copyOf(materials);
	}
	
	@Override
	public Extension apply(Context surfaceContext) {
		if((Object) surfaceContext.randomState instanceof RandomStateExtension randomStateExt) {		
			DensityFunction cachedHeight = randomStateExt.cache(this.height.value(), surfaceContext.noiseChunk);
			DensityFunction cachedSteepness = randomStateExt.cache(new FlatCache.Marker(new Slope(Holder.direct(cachedHeight), (float) this.seaLevel / (float) this.yScale, 10.0F, 1), 0), surfaceContext.noiseChunk);
			return new Extension(surfaceContext, cachedHeight, cachedSteepness, this.materials.stream().map((material) -> material.apply(surfaceContext)).toList(), this.seaLevel, this.yScale, this.heightModifier, this.slopeModifier, this.erodeSnow);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public Codec<ErosionSurfaceExtensionSource> codec() {
		return CODEC;
	}

	//TODO we should use surface rules to determine the material
	private record Extension(Context surfaceContext, DensityFunction height, DensityFunction steepness, List<Material> materials, int seaLevel, int yScale, float heightModifier, float slopeModifier, boolean erodeSnow, MutableFunctionContext functionContext) implements SurfaceExtension {

		public Extension(Context surfaceContext, DensityFunction height, DensityFunction steepness, List<Material> materials, int seaLevel, int yScale, float heightModifier, float slopeModifier, boolean erodeSnow) {
			this(surfaceContext, height, steepness, materials, seaLevel, yScale, heightModifier, slopeModifier, erodeSnow, new MutableFunctionContext());
		}
		
		@Override
		public void apply(BlockColumn column) {	        
			ChunkPos chunkPos = this.surfaceContext.chunk.getPos();
			int chunkLocalX = chunkPos.getBlockX(this.surfaceContext.blockX);
			int chunkLocalZ = chunkPos.getBlockZ(this.surfaceContext.blockZ);
			int y = this.surfaceContext.chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkLocalX, chunkLocalZ);
			BlockState surface = column.getBlock(y);

	        if (y < this.seaLevel) {
	            return;
	        }
	        
			if(surface.is(RTFBlockTags.ERODIBLE)) {
				BlockState top = Blocks.GRASS.defaultBlockState();
				BlockState middle = Blocks.STONE.defaultBlockState();
				BlockState material = this.getMaterial(this.surfaceContext.blockX, y, this.surfaceContext.blockZ, top, middle);
				if (material != top) {
	                if (material.is(BlockTags.BASE_STONE_OVERWORLD)) {
	                    erodeRock(column, y);
	                } else {
	                    fillDownSolid(column, y, y - 4, material);
	                } 
	            }
			}
		}
		
		private static void erodeRock(BlockColumn column, int y) {
	        int depth = 32;
	        BlockState material = findRock(Blocks.GRAVEL.defaultBlockState(), column, y, depth);

	        // fill downwards to the first rock layer
	        for (int dy = 0; dy < depth; dy++) {
	            replaceSolid(column, y - dy, material);
	        }
	    }
		
		private static BlockState findRock(BlockState initial, BlockColumn column, int y, int depth) {
	        // find the uppermost layer of rock & record it's depth
			BlockState material = initial;
	        for (int dy = 3; dy < 32; dy++) {
	            BlockState state = column.getBlock(y - dy);
	            if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
	                material = state;
	                break;
	            }
	        }
	        return material;
		}
		
		// obviously we're not hardcoding this in the final version
		// TOOD: make all this stuff configurable
		
		private static final Noise RAND = Source.builder().shift(21415).rand();

	    private static final Noise VARIANCE = Source.perlin(0, 100, 1);
		private static float getNoise(float x, float z, int seed, float scale, float bias) {
	        return VARIANCE.compute(x, z, seed) * scale + bias;
	    }

		private static final List<Block> GRASS = ImmutableList.of(Blocks.DRIED_KELP_BLOCK, Blocks.GRASS_BLOCK, Blocks.HAY_BLOCK, Blocks.MYCELIUM, Blocks.NETHER_WART_BLOCK, Blocks.SHROOMLIGHT, Blocks.TARGET, Blocks.WARPED_WART_BLOCK);
		private static final List<Block> DIRT = ImmutableList.of(Blocks.COARSE_DIRT, Blocks.DIRT, Blocks.FARMLAND, Blocks.DIRT_PATH, Blocks.PODZOL, Blocks.SOUL_SOIL);
	    private static final int ROCK_VAR = 30;
	    private static final int ROCK_MIN = 140;
	    private static final int DIRT_VAR = 40;
	    private static final int DIRT_MIN = 95;
	    private static final float ROCK_STEEPNESS = 0.65F;
	    private static final float DIRT_STEEPNESS = 0.475F;
	    
		@Nullable
		private BlockState getMaterial(int x, int y, int z, BlockState top, BlockState middle) {
			float height = (float) (this.sample(this.height, this.surfaceContext.blockX, y, this.surfaceContext.blockZ) + RAND.compute(x, z, 4) * this.heightModifier);
			float steepness = (float) (this.sample(this.steepness, this.surfaceContext.blockX, y, this.surfaceContext.blockZ) + RAND.compute(x, z, 8) * this.slopeModifier);

			if (steepness > ROCK_STEEPNESS || height > getNoise(x, z, 4, ROCK_VAR, ROCK_MIN)) {
				return rock(middle);
			}
			
			if (steepness > DIRT_STEEPNESS && height > getNoise(x, z, 8, DIRT_VAR, DIRT_MIN)) {
				return ground(top);
			}			
//			for(Material material : this.materials) {
//				if (steepness > material.source.steepness || height > getNoise(x, z, 4, material.source.var, material.source.min)) {
//					return material.rule.tryApply(x, y, z);
//				}
//			}
			return top;
		}
		
		private static BlockState rock(BlockState state) {
	        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
	            return state;
	        }
	        return Blocks.STONE.defaultBlockState();
	    }

	    private static BlockState ground(BlockState state) {
	        if (GRASS.contains(state.getBlock())) {
	            return Blocks.COARSE_DIRT.defaultBlockState();
	        }
	        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
	            return Blocks.GRAVEL.defaultBlockState();
	        }
	        if (DIRT.contains(state.getBlock())) {
	            return state;
	        }
	        if (state.getBlock() == Blocks.SAND) {
	        	return Blocks.SMOOTH_SANDSTONE.defaultBlockState();
	        }
	        if (state.getBlock() == Blocks.RED_SAND) {
	        	return Blocks.RED_SANDSTONE.defaultBlockState();
	        }
	        return Blocks.COARSE_DIRT.defaultBlockState();
	    }
		
		private double sample(DensityFunction function, int x, int y, int z) {
			this.functionContext.blockX = x;
			this.functionContext.blockY = y;
			this.functionContext.blockZ = z;
			return function.compute(this.functionContext);
		}
		
		private static int fillDownSolid(BlockColumn column, int from, int to, BlockState state) {
	        for (int dy = from; dy > to; dy--) { ;
	            replaceSolid(column, dy, state);
	        }
	        return to;
	    }

	    private static void replaceSolid(BlockColumn column, int y, BlockState state) {
	        if (column.getBlock(y).isAir()) {
	            return;
	        }
	        column.setBlock(y, state);
	    }
	}
	
	public record MaterialSource(float steepness, float var, float min, RuleSource ruleSource) {
		public static final Codec<MaterialSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("steepness").forGetter(MaterialSource::steepness),
			Codec.FLOAT.fieldOf("var").forGetter(MaterialSource::var),
			Codec.FLOAT.fieldOf("min").forGetter(MaterialSource::min),
			RuleSource.CODEC.fieldOf("rule_source").forGetter(MaterialSource::ruleSource)
		).apply(instance, MaterialSource::new));
		
		public Material apply(Context surfaceContext) {
			return new Material(this, this.ruleSource.apply(surfaceContext));
		}
	}
	
	@Deprecated(forRemoval = true)
	private record Material(MaterialSource source, SurfaceRule rule) {
	}
}
