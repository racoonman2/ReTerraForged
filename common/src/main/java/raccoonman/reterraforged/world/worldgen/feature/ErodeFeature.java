package raccoonman.reterraforged.world.worldgen.feature;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
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
import raccoonman.reterraforged.world.worldgen.feature.ErodeFeature.Config;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.tile.Tile;

public class ErodeFeature extends Feature<Config> {

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
			raccoonman.reterraforged.world.worldgen.heightmap.Heightmap heightmap = generatorContext.generator.getHeightmap();
			Levels levels = heightmap.levels();
			Noise rand = Noises.white(heightmap.climate().randomSeed(), 1);
			Noise desertErosionVariance = makeDesertErosionVariance(levels);
			BlockPos.MutableBlockPos pos = new MutableBlockPos();
			Config config = placeContext.config();
			for(int x = 0; x < 16; x++) {
				for(int z = 0; z < 16; z++) {
					int worldX = chunkPos.getBlockX(x);
					int worldZ = chunkPos.getBlockZ(z);
					
					Cell cell = tileChunk.getCell(x, z);
					int scaledY = levels.scale(cell.height);
					int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
					Holder<Biome> biome = level.getBiome(pos.set(worldX, surfaceY, worldZ));
					
					pos.set(worldX, surfaceY, worldZ);
					
					if(biome.is(Biomes.DESERT)) {
						erodeDesert(desertErosionVariance, levels, chunk, cell, pos, surfaceY);
						continue;
					}
			        if(surfaceY <= scaledY && surfaceY >= generator.getSeaLevel() - 1 && !biome.is(Biomes.WOODED_BADLANDS) && !biome.is(Biomes.BADLANDS)) {
						erodeColumn(config, rand, generator, chunk, cell, pos, surfaceY, biome);
						//remove any vegetation that may have leaked from other chunks
						pos.setY(surfaceY);
						while(!level.getBlockState(pos.move(Direction.UP)).canSurvive(level, pos)) {
							level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
						}
					}
				}
			}
			return true;
		} else {
			throw new IllegalStateException();
		}
	}

	//TODO move this to surface rules
	@Deprecated(forRemoval = true)
	private static Noise makeDesertErosionVariance(Levels levels) {
		Noise noise = Noises.perlin(435, 8, 1);
		return Noises.mul(noise, levels.scale(16));
	}
	
	// TODO ^
	@Deprecated(forRemoval = true)
	private static void erodeDesert(Noise variance, Levels levels, ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, int surfaceY) {
		float min = levels.ground(10);
		float threshold = levels.ground(40);

        if (cell.gradient < 0.15F) {
            return;
        }

        if (cell.height < min) {
            return;
        }

        float value = cell.height + variance.compute(pos.getX(), pos.getZ(), 0);
        if (cell.gradient > 0.3F || value > threshold) {
            BlockState state = Blocks.SMOOTH_SANDSTONE.defaultBlockState();
            
            if (value > threshold) {
                if (cell.gradient > 0.975) {
                    state = Blocks.TERRACOTTA.defaultBlockState();
                } else if (cell.gradient > 0.85) {
                    state = Blocks.BROWN_TERRACOTTA.defaultBlockState();
                } else if (cell.gradient > 0.75) {
                    state = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
                } else if (cell.gradient > 0.65) {
                    state = Blocks.TERRACOTTA.defaultBlockState();
                }
            }

            for (int dy = 0; dy < 4; dy++) {
                chunk.setBlockState(pos.setY(surfaceY - dy), state, false);
            }
        }
	}
	
	private static void erodeColumn(Config config, Noise rand, ChunkGenerator generator, ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, int surfaceY, Holder<Biome> biome) {
        if (cell.terrain.isRiver() || cell.terrain.isWetland()) {
            return;
        }

        if (cell.terrain == TerrainType.VOLCANO_PIPE) {
            return;
        }
		
        BlockState top = chunk.getBlockState(pos);
        if(top.is(RTFBlockTags.ERODIBLE)) {
        	if((top.is(Blocks.SNOW_BLOCK) || top.is(Blocks.POWDER_SNOW)) && cell.gradient < 0.45F) {
        		return;
        	}
        	
            BlockState material = getMaterial(config, rand, chunk, cell, pos, top, generator instanceof NoiseBasedChunkGenerator noiseChunkGenerator ? noiseChunkGenerator.generatorSettings().value().defaultBlock() : Blocks.STONE.defaultBlockState(), biome);
            if (material != top) {
                if (material.is(config.rockTag())) {
                	erodeRock(config, chunk, cell, pos, surfaceY);
                    return;
                } else {
                    ColumnDecorator.fillDownSolid(chunk, pos, surfaceY, surfaceY - 4, material);
                }
            }
            placeScree(config, rand, chunk, cell, pos, surfaceY);
        }
	}

    private static void erodeRock(Config config, ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, int y) {
        int depth = 32;
        BlockState material = Blocks.GRAVEL.defaultBlockState();
        // find the uppermost layer of rock & record it's depth
        for (int dy = 3; dy < 32; dy++) {
            pos.setY(y - dy);
            BlockState state = chunk.getBlockState(pos);
            if (state.is(config.rockTag())) {
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
	
	private static void placeScree(Config config, Noise rand, ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, int surfaceY) {
    	int x = pos.getX();
    	int z = pos.getZ();
    	float steepness = cell.gradient + rand.compute(x, z, 1) * config.slopeModifier();
        if (steepness < config.screeSteepness()) {
            return;
        }

        float sediment = cell.sediment * config.sedimentNoise();
        float noise = rand.compute(x, z, 2) * config.sedimentNoise();
        if (sediment + noise > config.screeValue()) {
            ColumnDecorator.fillDownSolid(chunk, pos, surfaceY, surfaceY - 2, Blocks.GRAVEL.defaultBlockState());
        }
	}
	
    private static BlockState getMaterial(Config config, Noise rand, ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, BlockState top, BlockState middle, Holder<Biome> biome) {
    	int x = pos.getX();
    	int z = pos.getZ();
        float height = cell.height + rand.compute(x, z, 0) * config.heightModifier();
        float steepness = cell.gradient + rand.compute(x, z, 1) * config.slopeModifier();

        if (steepness > config.rockSteepness() || height > ColumnDecorator.sampleNoise(x, z, config.rockVar(), config.rockMin())) {
            return rock(middle);
        }

        if (!top.is(Blocks.SNOW_BLOCK) && !top.is(Blocks.POWDER_SNOW) && steepness > config.dirtSteepness()) {
        	if(height > ColumnDecorator.sampleNoise(x, z, config.dirtVar(), config.dirtMin())) {
        		return ground(top);
        	}
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

    public record Config(TagKey<Block> rockTag, int rockVar, int rockMin, int dirtVar, int dirtMin, float rockSteepness, float dirtSteepness, float screeSteepness, float heightModifier, float slopeModifier, float sedimentModifier, float sedimentNoise, float screeValue) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TagKey.codec(Registries.BLOCK).fieldOf("rock_tag").forGetter(Config::rockTag),
			Codec.INT.fieldOf("rock_var").forGetter(Config::rockVar),
			Codec.INT.fieldOf("rock_min").forGetter(Config::rockMin),
			Codec.INT.fieldOf("dirt_var").forGetter(Config::dirtVar),
			Codec.INT.fieldOf("dirt_min").forGetter(Config::dirtMin),
			Codec.FLOAT.fieldOf("rock_steepness").forGetter(Config::rockSteepness),
			Codec.FLOAT.fieldOf("dirt_steepness").forGetter(Config::dirtSteepness),
			Codec.FLOAT.fieldOf("scree_steepness").forGetter(Config::screeSteepness),
			Codec.FLOAT.fieldOf("height_modifier").forGetter(Config::heightModifier),
			Codec.FLOAT.fieldOf("slope_modifier").forGetter(Config::slopeModifier),
			Codec.FLOAT.fieldOf("sediment_modifier").forGetter(Config::sedimentModifier),
			Codec.FLOAT.fieldOf("sediment_noise").forGetter(Config::sedimentNoise),
			Codec.FLOAT.fieldOf("screeValue").forGetter(Config::screeValue)
		).apply(instance, Config::new));
	}
}
