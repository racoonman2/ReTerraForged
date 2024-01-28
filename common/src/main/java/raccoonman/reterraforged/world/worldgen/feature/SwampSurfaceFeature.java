package raccoonman.reterraforged.world.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.world.worldgen.feature.SwampSurfaceFeature.Config;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class SwampSurfaceFeature extends Feature<Config> {
	private static final Noise MATERIAL_NOISE = makeMaterialNoise();
	
	public SwampSurfaceFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> ctx) {
		Config config = ctx.config();
		BlockPos origin = ctx.origin();
		WorldGenLevel level = ctx.level();
		ChunkGenerator generator = ctx.chunkGenerator();
		ChunkAccess chunk = level.getChunk(origin);
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int waterY = generator.getSeaLevel() - 1;

		for(int localX = 0; localX < 16; localX++) {
			for(int localZ = 0; localZ < 16; localZ++) {
				int x = origin.getX() + localX;
				int z = origin.getZ() + localZ;
				int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
				
				double biomeInfoNoise = Biome.BIOME_INFO_NOISE.getValue(x * 0.25D, z * 0.25D, false);
				BlockState filler = getMaterial(x, waterY, z, waterY, config);

				pos.set(x, surfaceY, z);
				
				if(level.getBiome(pos).is(Biomes.SWAMP)) {
			        if (biomeInfoNoise > 0.0D) {
			            for (int y = surfaceY; y >= surfaceY - 10; --y) {
			                pos.setY(y);
			                if (level.getBlockState(pos).isAir()) {
			                    continue;
			                }

			                if (y == waterY && !level.getFluidState(pos).isEmpty()) {
			                    level.setBlock(pos, filler, 2);
			                }
			                break;
			            }
			        }

			        int oceanFloor = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, localX, localZ);
			        if (oceanFloor <= waterY) {
			        	level.setBlock(pos.setY(oceanFloor), getMaterial(x, oceanFloor, z, waterY, config), 2);
			        }					
				}
			}	
		}
		return true;
	}

    private static BlockState getMaterial(int x, int y, int z, int waterY, Config config) {
        float value = MATERIAL_NOISE.compute(x, z, 0);
        if (value > 0.6F) {
            if (value < 0.75F && y < waterY) {
                return config.clayMaterial();
            }
            return config.gravelMaterial();
        }
        return config.dirtMaterial();
    }
    
    private static Noise makeMaterialNoise() {
    	Noise base = Noises.simplex(23, 40, 2);
    	return Noises.warpWhite(base, 213, 2, 4);    	
    }
    
    public record Config(BlockState clayMaterial, BlockState gravelMaterial, BlockState dirtMaterial) implements FeatureConfiguration {
    	public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		BlockState.CODEC.fieldOf("clay_material").forGetter(Config::clayMaterial),
    		BlockState.CODEC.fieldOf("gravel_material").forGetter(Config::gravelMaterial),
    		BlockState.CODEC.fieldOf("dirt_material").forGetter(Config::dirtMaterial)
    	).apply(instance, Config::new));
    }
}
