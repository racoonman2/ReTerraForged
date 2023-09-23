package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;

@Mixin(NoiseBasedChunkGenerator.class)
abstract class MixinNoiseBasedChunkGenerator extends ChunkGenerator {
	@Shadow
    private Holder<NoiseGeneratorSettings> settings;
	
	public MixinNoiseBasedChunkGenerator(BiomeSource biomeSource) {
		super(biomeSource);
	}

	@Override
    public void applyBiomeDecoration(WorldGenLevel worldGenLevel, ChunkAccess chunkAccess, StructureManager structureManager) {
    	super.applyBiomeDecoration(worldGenLevel, chunkAccess, structureManager);

    	RandomState randomState = worldGenLevel.getLevel().getChunkSource().randomState();
        WorldGenerationContext worldGenerationContext = new WorldGenerationContext((NoiseBasedChunkGenerator) (Object) this, worldGenLevel);
        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk(chunk -> this.createNoiseChunk(chunk, structureManager, Blender.empty(), randomState));
        SurfaceRules.Context context = new SurfaceRules.Context(randomState.surfaceSystem(), randomState, chunkAccess, noiseChunk, worldGenLevel.getBiomeManager()::getBiome, worldGenLevel.registryAccess().registryOrThrow(Registries.BIOME), worldGenerationContext);
        if((Object) context instanceof ContextExtension extension) {
            NoiseGeneratorSettings noiseGeneratorSettings = this.settings.value();
        	noiseGeneratorSettings.surfaceRule().apply(context);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            ChunkPos chunkPos = chunkAccess.getPos();
            BlockColumn column = new BlockColumn() {

                @Override
                public BlockState getBlock(int y) {
                    return chunkAccess.getBlockState(mutableBlockPos.setY(y));
                }

                @Override
                public void setBlock(int y, BlockState blockState) {
                    LevelHeightAccessor level = chunkAccess.getHeightAccessorForGeneration();
                    if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
                        chunkAccess.setBlockState(mutableBlockPos.setY(y), blockState, false);
                        if (!blockState.getFluidState().isEmpty()) {
                            chunkAccess.markPosForPostprocessing(mutableBlockPos);
                        }
                    }
                }

                @Override
                public String toString() {
                    return "ChunkBlockColumn " + chunkPos;
                }
            };

            int chunkX = chunkPos.getMinBlockX();
            int chunkZ = chunkPos.getMinBlockZ();
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    int worldX = chunkX + x;
                    int worldZ = chunkZ + z;
                    mutableBlockPos.setX(worldX).setZ(worldZ);
                    context.updateXZ(worldX, worldZ);
                    extension.applySurfaceDecorators(column);
                }
            }
        }
    }

	@Shadow
	public NoiseChunk createNoiseChunk(ChunkAccess chunkAccess, StructureManager structureManager, Blender blender, RandomState randomState) {
		throw new UnsupportedOperationException();
	}
}
