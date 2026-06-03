package raccoonman.reterraforged.world.worldgen.layer;

import org.jetbrains.annotations.Nullable;

public class LayerCache<A> {
	private Layer<?> layer;
	private int minLayerX;
	private int minLayerY;
	private int cacheLength;
	private A[] values;
	
	public LayerCache(Layer<?> layer, int minLayerX, int minLayerY, int cacheLength, A[] values) {
		this.layer = layer;
		this.minLayerX = minLayerX;
		this.minLayerY = minLayerY;
		this.cacheLength = cacheLength;
		this.values = values;
	}
	
	public int getMinLayerX() {
		return this.minLayerX;
	}
	
	public int getMinLayerY() {
		return this.minLayerY;
	}
	
	public A[] getValues() {
		return this.values;
	}
	
	public int index(int layerX, int layerY) {
		int localLayerX = layerX - this.minLayerX;
		int localLayerY = layerY - this.minLayerY;
		return localLayerY * this.cacheLength + localLayerX;
	}
	
	@Nullable
	public A get(int layerX, int layerY) {
		int index = this.index(layerX, layerY);
		if(index < 0 || index >= this.values.length) {
			return null;
		}
		return this.values[index];
	}
	
	@Nullable
	public A getAtBlock(int blockX, int blockZ) {
		int layerX = this.layer.blockToLayer(blockX);
		int layerY = this.layer.blockToLayer(blockZ);
		return this.get(layerX, layerY);
	}
}
