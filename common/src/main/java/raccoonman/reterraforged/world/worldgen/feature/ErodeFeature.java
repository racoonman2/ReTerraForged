package raccoonman.reterraforged.world.worldgen.feature;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.tags.RTFBlockTags;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.cell.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile;
import raccoonman.reterraforged.world.worldgen.feature.ErodeFeature.Config;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class ErodeFeature extends Feature<Config> {
	public static final int ROCK_VAR = 30;
    public static final int ROCK_MIN = 140;

    public static final int DIRT_VAR = 40;
    public static final int DIRT_MIN = 95;

    public static final float ROCK_STEEPNESS = 0.65F;
    public static final float DIRT_STEEPNESS = 0.475F;
    public static final float SCREE_STEEPNESS = 0.4F;

    public static final float HEIGHT_MODIFIER = 6F / 255F;
    public static final float SLOPE_MODIFIER = 3F / 255F;

    private static final float SEDIMENT_MODIFIER = 256;
    private static final float SEDIMENT_NOISE = 3F / 255F;
    private static final float SCREE_VALUE = 0.55F;

	public ErodeFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> placeContext) {
		WorldGenLevel level = placeContext.level();
		RandomState randomState = level.getLevel().getChunkSource().randomState();
		
		@Nullable
		GeneratorContext generatorContext;
		if((Object) randomState instanceof RTFRandomState rtfRandomState && (generatorContext = rtfRandomState.generatorContext()) != null) {
			ChunkPos chunkPos = new ChunkPos(placeContext.origin());
			int chunkX = chunkPos.x;
			int chunkZ = chunkPos.z;
			ChunkGenerator generator = placeContext.chunkGenerator();
			ChunkAccess chunk = level.getChunk(chunkX, chunkZ);
			Tile.Chunk tileChunk = generatorContext.cache.provideAtChunk(chunkX, chunkZ).getChunkReader(chunkX, chunkZ);
			raccoonman.reterraforged.world.worldgen.cell.heightmap.Heightmap heightmap = generatorContext.generator.getHeightmap();
			Levels levels = heightmap.levels();
			Noise rand = Noises.white(heightmap.climate().randomSeed(), 1);
			BlockPos.MutableBlockPos pos = new MutableBlockPos();
			
			for(int x = 0; x < 16; x++) {
				for(int z = 0; z < 16; z++) {
					Cell cell = tileChunk.getCell(x, z);
			        if(levels.scale(cell.height) >= generator.getSeaLevel()) {
						int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
						pos.set(chunkPos.getBlockX(x), surfaceY, chunkPos.getBlockZ(z));
						
						erodeColumn(rand, generator, chunk, cell, pos, surfaceY);
					}
				}
			}
			
			return true;
		} else {
			throw new IllegalStateException();
		}
	}
	
	private static void erodeColumn(Noise rand, ChunkGenerator generator, ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, int surfaceY) {
        if (cell.terrain.isRiver() || cell.terrain.isWetland()) {
            return;
        }

        if (cell.terrain == TerrainType.VOLCANO_PIPE) {
            return;
        }
		
        BlockState top = chunk.getBlockState(pos);
        if(top.is(RTFBlockTags.ERODIBLE)) {
            BlockState material = getMaterial(rand, cell, pos, top, generator instanceof NoiseBasedChunkGenerator noiseChunkGenerator ? noiseChunkGenerator.generatorSettings().value().defaultBlock() : Blocks.STONE.defaultBlockState());
            if (material != top) {
                if (material.is(RTFBlockTags.ROCK)) {
                	erodeRock(chunk, cell, pos, surfaceY);
                    return;
                } else {
                    ColumnDecorator.fillDownSolid(chunk, pos, surfaceY, surfaceY - 4, material);
                }
            }
            placeScree(rand, chunk, cell, pos, surfaceY);
        }
	}

    private static void erodeRock(ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, int y) {
        int depth = 32;
        BlockState material = Blocks.GRAVEL.defaultBlockState();
        // find the uppermost layer of rock & record it's depth
        for (int dy = 3; dy < 32; dy++) {
            pos.setY(y - dy);
            BlockState state = chunk.getBlockState(pos);
            if (state.is(RTFBlockTags.ROCK)) {
                material = state;
                depth = dy + 1;
                break;
            }
        }

        // fill downwards to the first rock layer
        for (int dy = 0; dy < depth; dy++) {
            ColumnDecorator.replaceSolid(chunk, pos.setY(y - dy), material);
        }
    }
	
	private static void placeScree(Noise rand, ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, int surfaceY) {
    	int x = pos.getX();
    	int z = pos.getZ();
    	float steepness = cell.gradient + rand.compute(x, z, 1) * SLOPE_MODIFIER;
        if (steepness < SCREE_STEEPNESS) {
            return;
        }

        float sediment = cell.sediment * SEDIMENT_MODIFIER;
        float noise = rand.compute(x, z, 2) * SEDIMENT_NOISE;
        if (sediment + noise > SCREE_VALUE) {
            ColumnDecorator.fillDownSolid(chunk, pos, surfaceY, surfaceY - 2, Blocks.GRAVEL.defaultBlockState());
        }
	}
	
    private static BlockState getMaterial(Noise rand, Cell cell, BlockPos.MutableBlockPos pos, BlockState top, BlockState middle) {
    	int x = pos.getX();
    	int z = pos.getZ();
        float height = cell.height + rand.compute(x, z, 0) * HEIGHT_MODIFIER;
        float steepness = cell.gradient + rand.compute(x, z, 1) * SLOPE_MODIFIER;

        if (steepness > ROCK_STEEPNESS || height > ColumnDecorator.sampleNoise(x, z, ROCK_VAR, ROCK_MIN)) {
            return rock(middle);
        }

        if (steepness > DIRT_STEEPNESS && height > ColumnDecorator.sampleNoise(x, z, DIRT_VAR, DIRT_MIN)) {
            return ground(top);
        }

        return top;
    }
    
    private static BlockState rock(BlockState state) {
        if (state.is(RTFBlockTags.ROCK)) {
            return state;
        }
        return Blocks.STONE.defaultBlockState();
    }

    private static BlockState ground(BlockState state) {
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM)) {
        	return Blocks.COARSE_DIRT.defaultBlockState();
        }
        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
            return Blocks.GRAVEL.defaultBlockState();
        }
        if (state.is(BlockTags.DIRT)) {
            return state;
        }
        if (state.is(Blocks.SAND)) {
        	return Blocks.SMOOTH_SANDSTONE.defaultBlockState();
        }
        if (state.is(Blocks.RED_SAND)) {
        	return Blocks.SMOOTH_RED_SANDSTONE.defaultBlockState();
        }
        return Blocks.COARSE_DIRT.defaultBlockState();
    }
	
	public record Config() implements FeatureConfiguration {
		public static final Codec<Config> CODEC = Codec.unit(Config::new);
	}
}
