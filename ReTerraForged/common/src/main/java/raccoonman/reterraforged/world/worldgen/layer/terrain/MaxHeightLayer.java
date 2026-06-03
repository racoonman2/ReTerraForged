package raccoonman.reterraforged.world.worldgen.layer.terrain;

import java.util.concurrent.Executor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.Reference;

public class MaxHeightLayer extends Layer<Float> {
	private Layer<Reference<Terrain>> terrainLayer;
	
	public MaxHeightLayer(Layer<Reference<Terrain>> terrainLayer) {
		super(SectionPos.sectionToBlockCoord(1));
		this.terrainLayer = terrainLayer;
	}

	@Override
	public Float provide(int layerX, int layerY, Executor executor) {
		int layerBlockX = this.layerToBlock(layerX);
		int layerBlockZ = this.layerToBlock(layerY);
		
		int terrainLayerX = this.terrainLayer.blockToLayer(layerBlockX);
		int terrainLayerY = this.terrainLayer.blockToLayer(layerBlockZ);
		Terrain terrain = TerrainLayer.provideResultNow(this.terrainLayer, terrainLayerX, terrainLayerY);
		float[] heightArray = terrain.provide(Terrain.HEIGHT);
		
		int localBlockX = layerBlockX - this.terrainLayer.layerToBlock(terrainLayerX);
		int localBlockZ = layerBlockZ - this.terrainLayer.layerToBlock(terrainLayerY);
		float maxHeight = Float.MIN_VALUE;
		for(int blockX = localBlockX; blockX < localBlockX + this.size; blockX++) {
			for(int blockZ = localBlockZ; blockZ < localBlockZ + this.size; blockZ++) {
				int cellX = terrain.blockToCell(blockX);
				int cellY = terrain.blockToCell(blockZ);
				int cellIndex = terrain.cellIndex(cellX, cellY);
				maxHeight = Math.max(maxHeight, Terrain.getScaledHeight(heightArray[cellIndex]));
			}
		}
		return maxHeight;
	}
	
	public record Factory(Holder<Layer.Factory<Reference<Terrain>>> terrainLayer) implements Layer.Factory<Float> {
		public static final MapCodec<MaxHeightLayer.Factory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Layer.<Reference<Terrain>>codec().fieldOf("terrain_layer").forGetter(MaxHeightLayer.Factory::terrainLayer)
		).apply(instance, MaxHeightLayer.Factory::new));

		@Override
		public MaxHeightLayer createLayer(Layer.Provider provider) {
			Layer<Reference<Terrain>> terrainLayer = provider.getOrCreateLayer(this.terrainLayer);
			return new MaxHeightLayer(terrainLayer);
		}

		@Override
		public MapCodec<MaxHeightLayer.Factory> codec() {
			return CODEC;
		}

		@Override
		public Layer.Factory<Float> mapAll(Layer.Visitor visitor) {
			return visitor.apply(new MaxHeightLayer.Factory(Layer.Factory.mapLayer(this.terrainLayer, visitor)));
		}
	}
}
