package raccoonman.reterraforged.world.worldgen.feature;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.cell.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile;
import raccoonman.reterraforged.world.worldgen.feature.DecorateSnowFeature.Config;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class DecorateSnowFeature extends Feature<Config> {
    private static final float SNOW_ROCK_STEEPNESS = 0.45F;
    private static final float SNOW_ROCK_HEIGHT = 95.0F / 255.0F;
    private static final float MIN = min(SnowLayerBlock.LAYERS);
    private static final float MAX = max(SnowLayerBlock.LAYERS);

	public DecorateSnowFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> placeContext) {
		WorldGenLevel level = placeContext.level();
		RandomState randomState = level.getLevel().getChunkSource().randomState();
		
		@Nullable
		GeneratorContext generatorContext;
		if((Object) randomState instanceof RTFRandomState rtfRandomState && (generatorContext = rtfRandomState.generatorContext()) != null) {
			ChunkGenerator generator = placeContext.chunkGenerator();
			ChunkPos chunkPos = new ChunkPos(placeContext.origin());
			int chunkX = chunkPos.x;
			int chunkZ = chunkPos.z;
			ChunkAccess chunk = level.getChunk(chunkX, chunkZ);
			Tile.Chunk tileChunk = generatorContext.cache.provideAtChunk(chunkX, chunkZ).getChunkReader(chunkX, chunkZ);
			raccoonman.reterraforged.world.worldgen.cell.heightmap.Heightmap heightmap = generatorContext.generator.getHeightmap();
			Levels levels = heightmap.levels();
			Noise rand = Noises.white(heightmap.climate().randomSeed(), 1);
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			Config config = placeContext.config();

			for(int x = 0; x < 16; x++) {
				for(int z = 0; z < 16; z++) {
		        	Cell cell = tileChunk.getCell(x, z);
			        if(levels.scale(cell.height) >= generator.getSeaLevel()) {
				        int surfaceY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		        		int worldX = chunkPos.getBlockX(x);
		        		int worldZ = chunkPos.getBlockZ(z);
				        pos.set(worldX, surfaceY, worldZ);
				        
				        if(config.erode) {
				        	if(level.getBiome(pos).value().getTemperature(pos) <= 0.25) {
					            float var = -ColumnDecorator.sampleNoise(worldX, worldZ, 16, 0);
					            float hNoise = rand.compute(worldX, worldZ, 4) * ErodeFeature.HEIGHT_MODIFIER;
					            float sNoise = rand.compute(worldX, worldZ, 5) * ErodeFeature.SLOPE_MODIFIER;
					            float vModifier = cell.terrain == TerrainType.VOLCANO ? 0.15F : 0F;
					            float height = cell.height + var + hNoise + vModifier;
					            float steepness = cell.gradient + var + sNoise + vModifier;
					            if (snowErosion(worldX, worldZ, steepness, height)) {
					                Predicate<BlockState> predicate = Heightmap.Types.MOTION_BLOCKING.isOpaque();
					                for (int dy = 2; dy > 0; dy--) {
					                    pos.setY(surfaceY + dy);
					                    BlockState state = chunk.getBlockState(pos);
					                    if (!predicate.test(state) || state.is(Blocks.SNOW)) {
							            	erodeSnow(chunk, pos);
					                    }
					                }
					            }
					        }
				        }
				        
				        if(config.smooth) {
				            pos.setY(surfaceY + 1);

				            BlockState state = chunk.getBlockState(pos);
				            if (state.isAir()) {
				                pos.setY(surfaceY);
				                state = chunk.getBlockState(pos);
				                if (state.isAir()) {
				                    continue;
				                }
				            }

				            if(state.is(Blocks.SNOW)) {
				            	smoothSnow(chunk, pos, cell, levels, 0.0F);
				            }
				        }
			        }
				}
			}
	        return true;
		} else {
			throw new IllegalStateException();
		}
	}

    private static boolean snowErosion(float x, float z, float steepness, float height) {
        return steepness > ErodeFeature.ROCK_STEEPNESS || (steepness > SNOW_ROCK_STEEPNESS && height > SNOW_ROCK_HEIGHT) || (steepness > ErodeFeature.DIRT_STEEPNESS && height > ColumnDecorator.sampleNoise(x, z, ErodeFeature.DIRT_VAR, ErodeFeature.DIRT_MIN));
    }

    private static void erodeSnow(ChunkAccess chunk, BlockPos.MutableBlockPos pos) {
        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);

        if (pos.getY() > 0) {
            pos.setY(pos.getY() - 1);
            BlockState below = chunk.getBlockState(pos);
            if (below.hasProperty(GrassBlock.SNOWY)) {
                chunk.setBlockState(pos, below.setValue(GrassBlock.SNOWY, false), false);
            }
        }
    }
    
    private static void smoothSnow(ChunkAccess chunk, BlockPos.MutableBlockPos pos, Cell cell, Levels levels, float min) {
        float height = cell.height * levels.worldHeight;
        float depth = getDepth(height);
        if (depth > min) {
            int level = getLevel(depth);
            BlockState layer = getState(level);
            if (layer.is(Blocks.AIR)) {
                return;
            }
            chunk.setBlockState(pos, layer, false);

           fixBaseBlock(chunk, pos, layer, level);
        }
    }

    private static void fixBaseBlock(ChunkAccess chunk, BlockPos pos, BlockState layerMaterial, int level) {
        if (level > 1 && layerMaterial.is(Blocks.SNOW)) {
            BlockPos pos1 = pos.below();
            BlockState below = chunk.getBlockState(pos1);

            // Turns to dirt if submerged or the light-level is low. Light hasn't been calc'd at this
            // at this stage of world-gen so just blanket set everything to snowy dirt.
            if (below.getBlock() instanceof SpreadingSnowyDirtBlock) {
                chunk.setBlockState(pos1, Blocks.DIRT.defaultBlockState(), false);
            }
        }
    }

    private static BlockState getState(int level) {
        if (level < MIN) {
            return Blocks.AIR.defaultBlockState();
        }
        if (level >= MAX) {
            return Blocks.SNOW_BLOCK.defaultBlockState();
        }
        return Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, level);
    }
    
    private static int getLevel(float depth) {
        if (depth > 1) {
            depth = getDepth(depth);
        } else if (depth < 0) {
            depth = 0;
        }
        return NoiseUtil.round(depth * MAX);
    }

    private static float getDepth(float height) {
        return height - (int) height;
    }

    private static int min(Property<Integer> property) {
        return property.getPossibleValues().stream().min(Integer::compareTo).orElse(0);
    }

    private static int max(Property<Integer> property) {
        return property.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
    }

	public record Config(boolean erode, boolean smooth) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("erode").forGetter(Config::erode),
			Codec.BOOL.fieldOf("smooth").forGetter(Config::smooth)
		).apply(instance, Config::new));
	}
}
