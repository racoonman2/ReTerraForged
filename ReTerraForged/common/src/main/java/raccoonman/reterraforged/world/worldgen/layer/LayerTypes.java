package raccoonman.reterraforged.world.worldgen.layer;

import java.util.List;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registry.RTFBuiltInRegistries;
import raccoonman.reterraforged.world.worldgen.layer.terrain.DataType;
import raccoonman.reterraforged.world.worldgen.layer.terrain.MaxHeightLayer;
import raccoonman.reterraforged.world.worldgen.layer.terrain.Terrain;
import raccoonman.reterraforged.world.worldgen.layer.terrain.TerrainLayer;
import raccoonman.reterraforged.world.worldgen.layer.terrain.TerrainSamplerLayer;

public class LayerTypes {
	
	public static void bootstrap() {
		register("terrain", TerrainLayer.Factory.CODEC);
		register("sampler", TerrainSamplerLayer.Factory.CODEC);
		register("max_height", MaxHeightLayer.Factory.CODEC);
//		register("continent", ContinentLayer.Factory.CODEC);
	}

	public static TerrainLayer.Factory terrain(int layerSize, int batchCount, List<TerrainLayer.SampledFunction> sampledFunctions) {
		return new TerrainLayer.Factory(layerSize, batchCount, sampledFunctions);
	}
	
	public static TerrainSamplerLayer.Factory terrainSampler(Holder<Layer.Factory<Reference<Terrain>>> terrainLayer, DataType<float[]> dataType) {
		return new TerrainSamplerLayer.Factory(terrainLayer, dataType);
	}

	public static MaxHeightLayer.Factory maxHeight(Holder<Layer.Factory<Reference<Terrain>>> terrainLayer) {
		return new MaxHeightLayer.Factory(terrainLayer);
	}
	
	private static void register(String name, MapCodec<? extends Layer.Factory<?>> codec) {
		RegistryUtil.register(RTFBuiltInRegistries.TERRAIN_LAYER_TYPE, name, codec);
	}
}
