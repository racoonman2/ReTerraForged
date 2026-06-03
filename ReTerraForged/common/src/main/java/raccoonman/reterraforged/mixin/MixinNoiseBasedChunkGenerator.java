package raccoonman.reterraforged.mixin;

import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFChunk;
import raccoonman.reterraforged.extensions.RTFChunkGenerator;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.LayerResources;
import raccoonman.reterraforged.world.worldgen.MaxHeightUtil;
import raccoonman.reterraforged.world.worldgen.layer.CacheLayer;
import raccoonman.reterraforged.world.worldgen.layer.Layer;

@Mixin(NoiseBasedChunkGenerator.class)
abstract class MixinNoiseBasedChunkGenerator extends ChunkGenerator {
	@Shadow
	@Final
    private Holder<NoiseGeneratorSettings> settings;

	public MixinNoiseBasedChunkGenerator(BiomeSource biomeSource, Function<Holder<Biome>, BiomeGenerationSettings> settingsGetter) {
		super(biomeSource, settingsGetter);
	}

	@Inject(
		method = "doCreateBiomes",
		at = @At("HEAD")
	)
	@Deprecated //TODO move to NoiseChunk
    public void doCreateBiomes(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk, CallbackInfo callback) {
		RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);
		RTFChunkGenerator rtfChunkGenerator = ExtensionUtil.cast(this);
		
		Layer.Factory<Float> maxHeightLayerFactory = rtfChunkGenerator.getMaxHeightLayer();
		if(maxHeightLayerFactory == null) {
			return;
		}
		
		Layer<Float> layer = rtfRandomState.getMappedLayer(maxHeightLayerFactory);
		
		ChunkPos chunkPos = chunk.getPos();
		int minChunkBlockX = chunkPos.getMinBlockX();
		int minChunkBlockZ = chunkPos.getMinBlockZ();
		int layerX = layer.blockToLayer(minChunkBlockX);
		int layerY = layer.blockToLayer(minChunkBlockZ);
		float maxHeight = CacheLayer.provideResult(layer, layerX, layerY);
		RTFChunk rtfChunk = ExtensionUtil.cast(chunk);
		rtfChunk.setMaxHeight(Mth.ceil(maxHeight));
    }
    
	@Redirect(
		method = { "fillFromNoise", "populateNoise" },
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseSettings;height()I"
		)
	)
    public int fillFromNoise(NoiseSettings settings, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
		RTFChunk rtfChunk = ExtensionUtil.cast(chunk);
		int maxHeight = rtfChunk.getMaxHeight().orElseGet(settings::height);
		maxHeight = MaxHeightUtil.getMaxHeight(chunk.getPos(), maxHeight, this.settings.value(), settings, structureManager);
		return maxHeight;
    }
	
	@Inject(
		method = "createNoiseChunk",	
		at = @At("HEAD")
	)
    private void createNoiseChunk(ChunkAccess chunkAccess, StructureManager structureManager, Blender blender, RandomState randomState, CallbackInfoReturnable<NoiseChunk> callback) {
		LayerResources.setCurrentChunk(chunkAccess);
	}

	@Redirect(
		method = { "iterateNoiseColumn", "sampleHeightmap" },
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseSettings;height()I"
		),
		require = 2
	)
    private int iterateNoiseColumn(NoiseSettings settings, LevelHeightAccessor levelHeightAccessor, RandomState randomState, int blockX, int blockZ, @Nullable MutableObject<NoiseColumn> mutableObject, @Nullable Predicate<BlockState> predicate) {
//		int chunkX = SectionPos.blockToSectionCoord(blockX);
//		int chunkZ = SectionPos.blockToSectionCoord(blockZ);
//		RTFChunk rtfChunk = rtfRandomState.resourceLayer().getIfPresent(chunk);
		int maxHeight =  settings.height();//rtfChunk == null ? settings.height() : MaxHeightUtil.getMaxHeight(chunkX, chunkZ, rtfChunk.getMaxHeight(settings.height()), this.settings.value(), settings);
		return maxHeight;
    }
}
