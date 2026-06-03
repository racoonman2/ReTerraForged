package raccoonman.reterraforged.mixin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFAquifer;
import raccoonman.reterraforged.extensions.RTFChunk;
import raccoonman.reterraforged.world.worldgen.LayerResources;
import raccoonman.reterraforged.world.worldgen.MaxHeightUtil;
import raccoonman.reterraforged.world.worldgen.densityfunction.FastFlatCache;
import raccoonman.reterraforged.world.worldgen.densityfunction.FunctionProvider;
import raccoonman.reterraforged.world.worldgen.densityfunction.LayerCacheFunction;
import raccoonman.reterraforged.world.worldgen.densityfunction.LayerFunction;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.Reference;

//TODO add max height optimizations to preliminary surface
@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	@Shadow
	@Mutable
	private int cellCountY;
	@Shadow
    @Final
    private int cellHeight;
	@Shadow
    @Final
    private int noiseSizeXZ;
	@Shadow
    @Final
    private int firstNoiseX;
	@Shadow
    @Final
    private int firstNoiseZ;
	@Shadow
    @Final
    private Aquifer aquifer;
	@Shadow
	@Final
    private DensityFunctions.BeardifierOrMarker beardifier;

	private int minBlockX, minBlockZ;
	private int chunkX, chunkZ;
	private boolean fullChunk;
	private List<FastFlatCache> flatCaches;
	private MutableObject<DensityFunction> waterLevel;
	private DensityFunction.Visitor localVisitor;
	private RandomState randomState;
	
	@Redirect(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseRouter;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/NoiseRouter;"
		)
	)
	private NoiseRouter init(NoiseRouter noiseRouter, DensityFunction.Visitor visitor, int cellCountXZ, RandomState randomState, int minBlockX, int minBlockZ, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifierOrMarker, NoiseGeneratorSettings generatorSettings) {
		this.minBlockX = minBlockX;
		this.minBlockZ = minBlockZ;
		this.chunkX = SectionPos.blockToSectionCoord(minBlockX);
		this.chunkZ = SectionPos.blockToSectionCoord(minBlockZ);
		
		int fullChunkCellCount = SectionPos.sectionToBlockCoord(1) / noiseSettings.getCellWidth();
		this.fullChunk = cellCountXZ == fullChunkCellCount;
		this.flatCaches = new ArrayList<>();
		this.waterLevel = new MutableObject<>();

		if(this.fullChunk) {
			RTFChunk rtfChunk = ExtensionUtil.cast(LayerResources.getCurrentChunk());
			int maxHeight = Math.min(noiseSettings.height(), MaxHeightUtil.getMaxHeight(this.chunkX, this.chunkZ, rtfChunk.getMaxHeight().orElseGet(noiseSettings::height), generatorSettings, noiseSettings, beardifierOrMarker));
			this.cellCountY = Math.min(this.cellCountY, maxHeight / this.cellHeight);
		}
		
		this.localVisitor = FunctionProvider.localVisitor(this.minBlockX, this.minBlockZ);
		this.randomState = randomState;
		return noiseRouter.mapAll(visitor);
	}
	
	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void init(CallbackInfo callback) {
		FastFlatCache.initialize(this.flatCaches, this.firstNoiseX, this.firstNoiseZ, this.noiseSizeXZ);
		
		if(this.aquifer instanceof RTFAquifer aquifer) {
			aquifer.setWaterLevel(this.waterLevel);
		}
	}
   
	@Inject(
		method = "wrapNew",
		at = @At("HEAD"),
		cancellable = true
	)
	private void wrapNew(DensityFunction function, CallbackInfoReturnable<DensityFunction> callback) {
		if(function instanceof FastFlatCache.Marker cacheFunction) {
			FastFlatCache cache = new FastFlatCache(cacheFunction.function(), this.firstNoiseX, this.firstNoiseZ, this.noiseSizeXZ, cacheFunction.fullResolution());
			this.flatCaches.add(cache);
			callback.setReturnValue(cache);
		}
		
		if(function instanceof LayerFunction layerFunction) {
			int radius = 4;
			
			Layer<Reference<DensityFunction>> layer = layerFunction.getLayer();
			DensityFunction fallback = layerFunction.getFallback();
			int minChunkX = this.chunkX - radius;
			int minChunkZ = this.chunkZ - radius;
			int maxChunkX = this.chunkX + radius;
			int maxChunkZ = this.chunkZ + radius;
			DensityFunction cacheFunction = LayerCacheFunction.of(layer, fallback, minChunkX, minChunkZ, maxChunkX, maxChunkZ);
			callback.setReturnValue(cacheFunction);
		}
		
		DensityFunction mapped = this.localVisitor.apply(function);
		if(mapped != function) {
			callback.setReturnValue(mapped);
		}
	}
}