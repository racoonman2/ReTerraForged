package raccoonman.reterraforged.world.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

//TODO possibly @Deprecated?
public class ProcessSnowFeature extends Feature<ProcessSnowFeature.Config> {
    private static final float MIN_LAYERS = min(SnowLayerBlock.LAYERS);
    private static final float MAX_LAYERS = max(SnowLayerBlock.LAYERS);

	public ProcessSnowFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> ctx) {
//		Config config = ctx.config();
//		WorldGenLevel level = ctx.level();
//		
//		RandomState randomState = level.getLevel().getChunkSource().randomState();
//		RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);
//
//		BlockPos origin = ctx.origin();
//		int originX = origin.getX();
//		int originZ = origin.getZ();
//		int chunkX = SectionPos.blockToSectionCoord(originX);
//		int chunkZ = SectionPos.blockToSectionCoord(originZ);
//		
//		ChunkAccess chunk = level.getChunk(chunkX, chunkZ);
//		RTFChunk rtfChunk = ExtensionUtil.cast(chunk);
//		
//		GlobalFunctionVisitor globalFunctionVisitor = rtfRandomState.globalFunctionVisitor();
//		ChunkResources resources = rtfChunk.getResources();
//		
//		DensityFunction surfaceAngleFunction = config.surfaceAngle.value();
//		surfaceAngleFunction = surfaceAngleFunction.mapAll(globalFunctionVisitor);
//		surfaceAngleFunction = surfaceAngleFunction.mapAll(resources);
//		
//		DensityFunction offsetFunction = config.offset.value();
//		offsetFunction = offsetFunction.mapAll(globalFunctionVisitor);
//		offsetFunction = offsetFunction.mapAll(resources);
//		
//		MutableFunctionContext functionCtx = new MutableFunctionContext();
//		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
//
//		double maxSteepness = config.maxSnowSteepness();
//		for(int localX = 0; localX < 16; localX++) {
//			for(int localZ = 0; localZ < 16; localZ++) {
//				int x = originX + localX;
//				int z = originZ + localZ;
//				functionCtx.at(x, 0, z);
//				
//				int surfaceWG = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
//				blockPos.set(x, surfaceWG, z);
//				
//				if(config.erode()) {
//					double slope = surfaceAngleFunction.compute(functionCtx);
//					erodeSnow(level, blockPos, slope, maxSteepness);
//				}
//
//				if(config.smooth()) {
//					int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
//					double offset = offsetFunction.compute(functionCtx) * RTFNoiseRouterData.Y_SCALER;
//					if((int) offset + 1 == surface) {
//						smoothSnow(level, blockPos.set(x, surfaceWG + 1, z), offset);
//					}
//				}
//			}
//		}
		
		return true;
	}

    private static void erodeSnow(WorldGenLevel level, BlockPos.MutableBlockPos pos, double slope, double max) {
    	if(slope > max && level.getBlockState(pos).is(Blocks.SNOW)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            int y = pos.getY();
            pos.setY(y - 1);
            BlockState below = level.getBlockState(pos);
            if (below.hasProperty(GrassBlock.SNOWY)) {
            	level.setBlock(pos, below.setValue(GrassBlock.SNOWY, false), Block.UPDATE_CLIENTS);
            }
    	}
    }
    
    private static void smoothSnow(WorldGenLevel level, BlockPos.MutableBlockPos pos, double offset) {
    	int surfaceNoLeaves = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
    	BlockState state = level.getBlockState(pos.setY(surfaceNoLeaves + 1));
        if (state.isAir()) {
            state = level.getBlockState(pos.setY(surfaceNoLeaves));
            if (state.isAir()) {
                return;
            }
        }
    	
        double depth = getDepth(offset);
        if (depth > 0.0D && state.is(Blocks.SNOW)) {
            int snowLevel = getLevel(depth);
            BlockState layer = getState(snowLevel);
            if (layer.is(Blocks.AIR)) {
                return;
            }
            level.setBlock(pos, layer, Block.UPDATE_CLIENTS);

            fixBaseBlock(level, pos, layer, snowLevel);
        }
    }

    private static void fixBaseBlock(WorldGenLevel level, BlockPos.MutableBlockPos pos, BlockState layerMaterial, int snowLevel) {
        if (layerMaterial.is(Blocks.SNOW)) {
            pos.move(Direction.DOWN);
            BlockState belowState = level.getBlockState(pos);

            if(snowLevel > 1 && belowState.getBlock() instanceof SpreadingSnowyDirtBlock) {
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
            } else if(snowLevel > 0) {
                level.setBlock(pos, Blocks.SNOW_BLOCK.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    private static BlockState getState(int level) {
        if (level < MIN_LAYERS) {
            return Blocks.AIR.defaultBlockState();
        }
        if (level >= MAX_LAYERS) {
            return Blocks.SNOW_BLOCK.defaultBlockState();
        }
        return Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, level);
    }
    
    private static int getLevel(double depth) {
        if (depth > 1) {
            depth = getDepth(depth);
        } else if (depth < 0) {
            depth = 0;
        }
        return NoiseUtil.round(depth * MAX_LAYERS);
    }

    private static double getDepth(double height) {
        return height - (int) height;
    }

    private static int min(Property<Integer> property) {
        return property.getPossibleValues().stream().min(Integer::compareTo).orElse(0);
    }

    private static int max(Property<Integer> property) {
        return property.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
    }

	public record Config(/* Holder<DensityFunction> surfaceAngle, */Holder<DensityFunction> offset, boolean erode, boolean smooth, double maxSnowSteepness) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//			DensityFunction.CODEC.fieldOf("surface_angle").forGetter(ProcessSnowFeature.Config::surfaceAngle),
			DensityFunction.CODEC.fieldOf("offset").forGetter(ProcessSnowFeature.Config::offset),
			Codec.BOOL.fieldOf("erode").forGetter(Config::erode),
			Codec.BOOL.fieldOf("smooth").forGetter(Config::smooth),
			Codec.DOUBLE.fieldOf("max_snow_steepness").forGetter(Config::maxSnowSteepness)
		).apply(instance, Config::new));
	}
}
