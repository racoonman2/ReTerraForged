package raccoonman.reterraforged.common.asm.mixin;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.ErosionRule;
import raccoonman.reterraforged.common.level.levelgen.test.api.material.layer.LayerMaterial;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileProvider;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkReader;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;

@Mixin(ChunkGenerator.class)
class MixinChunkGenerator {
	
	@Inject(
		at = @At("HEAD"),
		method = "createStructures"
	)
    public void createStructures(RegistryAccess registryAccess, ChunkGeneratorStructureState chunkGeneratorStructureState, StructureManager structureManager, ChunkAccess chunkAccess, StructureTemplateManager structureTemplateManager, CallbackInfo callback) {
		RandomState randomState = chunkGeneratorStructureState.randomState();
		if((Object) randomState instanceof RandomStateExtension randomStateExtension) {
			@Nullable
			TileProvider tileCache = randomStateExtension.tileCache();
			if(tileCache != null) {
				ChunkPos pos = chunkAccess.getPos();
				randomStateExtension.tileCache().queueChunk(pos.x, pos.z);
			}
		}
    }
	
	@Inject(
		at = @At("RETURN"),
		method = "applyBiomeDecoration"
	)
    public void applyBiomeDecoration(WorldGenLevel worldGenLevel, ChunkAccess chunkAccess, StructureManager structureManager, CallbackInfo callback) {
		RandomState randomState = worldGenLevel.getLevel().getChunkSource().randomState();
		if((Object) randomState instanceof RandomStateExtension randomStateExtension) {
			@Nullable
			TileProvider tileCache = randomStateExtension.tileCache();
			if(tileCache != null) {
				ChunkPos chunkPos = chunkAccess.getPos();
				ChunkReader reader = tileCache.getChunk(chunkPos.x, chunkPos.z);
				
				BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
				for(int dx = 0; dx < 16; dx++) {
					for(int dz = 0; dz < 16; dz++) {
						Cell cell = reader.getCell(dx, dz);
						
						int x = chunkPos.getBlockX(dx);
						int z = chunkPos.getBlockZ(dz);

						//maybe make this a feature or something?
						//this is difficult without biome modifiers
						//pretty sure fabric has them though so maybe use a platform method or something
						this.erodeSnow(chunkAccess, worldGenLevel.getBiomeManager(), cell, x, z, pos);
						this.smoothLayers(chunkAccess, cell, pos, x, z);
					}
				}
				
				reader.close();
				reader.dispose();
			}
		}
	}
    
	private void erodeSnow(ChunkAccess chunk, BiomeManager biomeManager, Cell cell, int x, int z, BlockPos.MutableBlockPos pos) {
        int surfaceY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		pos.set(x, surfaceY, z);
        if (biomeManager.getBiome(pos).value().getTemperature(pos) <= 0.25) {
        	float var = -ErosionRule.Rule.getNoise(x, z, 678, 16, 0);
            float hNoise = ErosionRule.Rule.RANDOM.compute(x, z, 986) * ErosionRule.Rule.HEIGHT_MODIFIER;
            float sNoise = ErosionRule.Rule.RANDOM.compute(x, z, 768) * ErosionRule.Rule.SLOPE_MODIFIER;
            float vModifier = cell.terrain == TerrainType.VOLCANO ? 0.15F : 0F;
            float height = cell.value + var + hNoise + vModifier;
            float steepness = cell.gradient + var + sNoise + vModifier;
            if (this.snowErosion(x, z, steepness, height)) {
                Predicate<BlockState> predicate = Heightmap.Types.MOTION_BLOCKING.isOpaque();
                for (int dy = 2; dy > 0; dy--) {
                    pos.setY(surfaceY + dy);
                    BlockState state = chunk.getBlockState(pos);
                    if (!predicate.test(state)) {
                        this.erodeSnow(chunk, pos);
                    }
                }
            }
        }
	}

    private static final float SNOW_ROCK_STEEPNESS = 0.45F;
    private static final float SNOW_ROCK_HEIGHT = 95F / 255F;
    private boolean snowErosion(float x, float z, float steepness, float height) {
        return steepness > ErosionRule.Rule.ROCK_STEEPNESS || (steepness > SNOW_ROCK_STEEPNESS && height > SNOW_ROCK_HEIGHT) || (steepness > ErosionRule.Rule.DIRT_STEEPNESS && height > ErosionRule.Rule.getNoise(x, z, 4535, ErosionRule.Rule.DIRT_VAR, ErosionRule.Rule.DIRT_MIN));
    }

    private void erodeSnow(ChunkAccess chunk, BlockPos.MutableBlockPos pos) {
        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);

        if (pos.getY() > 0) {
            pos.setY(pos.getY() - 1);
            BlockState below = chunk.getBlockState(pos);
            if (below.hasProperty(GrassBlock.SNOWY)) {
                chunk.setBlockState(pos, below.setValue(GrassBlock.SNOWY, false), false);
            }
        }
    }
    
    private static final LayerMaterial SNOW_MATERIAL = LayerMaterial.of(Blocks.SNOW_BLOCK, Blocks.SNOW);
    private void smoothLayers(ChunkAccess chunk, Cell cell, BlockPos.MutableBlockPos pos, int x, int z) {
        int surfaceY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    	pos.set(x, surfaceY + 1, z);

    	BlockState state = chunk.getBlockState(pos);
    	if (state.isAir()) {
    		pos.set(x, surfaceY, z);
    		state = chunk.getBlockState(pos);
    		if (state.isAir()) {
    			return;
    		}
    	}
    	
    	if(state.is(Blocks.SNOW)) {
    		LayerMaterial material = SNOW_MATERIAL;
        	this.setLayer(chunk, pos, material, cell, 0F);
    	}
    }

    private void setLayer(ChunkAccess chunk, BlockPos pos, LayerMaterial material, Cell cell, float min) {
        float height = cell.value * 256;
        float depth = material.getDepth(height);
        if (depth > min) {
            int level = material.getLevel(depth);
            BlockState layer = material.getState(level);
            if (layer == LayerMaterial.NONE) {
                return;
            }
            chunk.setBlockState(pos, layer, false);

            fixBaseBlock(chunk, pos, layer, level);
        }
    }

    private void fixBaseBlock(ChunkAccess chunk, BlockPos pos, BlockState layerMaterial, int level) {
        if (level > 1 && layerMaterial.is(Blocks.SNOW)) {
            BlockPos pos1 = pos.below();
            BlockState below = chunk.getBlockState(pos1);

            // Turns to dirt if submerged or the light-level is low. Light hasn't been calc'd at this
            // at this stage of world-gen so just blanket set everything to snowy dirt.
            if (below.getBlock() instanceof SpreadingSnowyDirtBlock) {
                chunk.setBlockState(pos1, below.setValue(SpreadingSnowyDirtBlock.SNOWY, true), false);
            }
        }
    }
}
