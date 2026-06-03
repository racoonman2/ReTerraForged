package raccoonman.reterraforged.data.preset.registry;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.registry.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.LayerTypes;
import raccoonman.reterraforged.world.worldgen.layer.Reference;
import raccoonman.reterraforged.world.worldgen.layer.terrain.Terrain;
import raccoonman.reterraforged.world.worldgen.layer.terrain.TerrainLayer;

public class RTFLayers {
	public static final ResourceKey<Layer.Factory<Reference<Terrain>>> TERRAIN = createKey("terrain/terrain");
	public static final ResourceKey<Layer.Factory<Reference<DensityFunction>>> TERRAIN_HEIGHT = createKey("terrain/height");
	public static final ResourceKey<Layer.Factory<Float>> MAX_HEIGHT_LAYER = createKey("terrain/max_height");
	
	public static void bootstrap(Preset preset, BootstrapContext<Layer.Factory<?>> ctx) {
		Holder.Reference<Layer.Factory<Reference<Terrain>>> terrainLayer = register(ctx, TERRAIN, createTerrainLayer(preset, ctx));
		register(ctx, TERRAIN_HEIGHT, LayerTypes.terrainSampler(terrainLayer, Terrain.HEIGHT));
		register(ctx, MAX_HEIGHT_LAYER, LayerTypes.maxHeight(terrainLayer));
	}
	
	private static Layer.Factory<Reference<Terrain>> createTerrainLayer(Preset preset, BootstrapContext<Layer.Factory<?>> ctx) {
		int layerSize = SectionPos.sectionToBlockCoord(8);
		int batchCount = 2;
	
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		Holder<DensityFunction> terrainFunction = densityFunctions.getOrThrow(NoiseRouterKeys.TERRAIN_HEIGHT);
		List<TerrainLayer.SampledFunction> sampledFunctions = List.of(
			new TerrainLayer.SampledFunction(Terrain.HEIGHT, terrainFunction)
		);
		return LayerTypes.terrain(layerSize, batchCount, sampledFunctions);
	}
	
	private static <A> Holder.Reference<Layer.Factory<A>> register(BootstrapContext<Layer.Factory<?>> ctx, ResourceKey<Layer.Factory<A>> key, Layer.Factory<A> layer) {
		return RegistryUtil.registerTyped(ctx, key, layer);
	}
	
	private static <A> ResourceKey<Layer.Factory<A>> createKey(String path) {
    	return RegistryUtil.createTypedKey(RTFRegistries.LAYER, RTFCommon.location(path));
    }
}