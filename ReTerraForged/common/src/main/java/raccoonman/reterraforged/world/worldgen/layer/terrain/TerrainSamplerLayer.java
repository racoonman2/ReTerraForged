package raccoonman.reterraforged.world.worldgen.layer.terrain;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.world.worldgen.densityfunction.TerrainFunction;
import raccoonman.reterraforged.world.worldgen.layer.CompositeReference;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.Reference;

public class TerrainSamplerLayer extends Layer<Reference<DensityFunction>> {
	private Layer<Reference<Terrain>> terrainLayer;
	private DataType<float[]> dataType;
	
	public TerrainSamplerLayer(Layer<Reference<Terrain>> terrainLayer, DataType<float[]> dataType) {
		super(terrainLayer.size());
		
		this.terrainLayer = terrainLayer;
		this.dataType = dataType;
	}
	
	@Override
	public Reference<DensityFunction> provide(int layerX, int layerY, Executor executor) {
		Reference<Terrain> reference = this.terrainLayer.provide(layerX, layerY, executor);
		CompletableFuture<DensityFunction> functionFuture = reference.future().thenApply((terrain) -> {
			int minBlockX = this.layerToBlock(layerX);
			int minBlockZ = this.layerToBlock(layerY);
			float[] data = terrain.provide(this.dataType);
			return new TerrainFunction(terrain, data, minBlockX, minBlockZ);
		});
		return new CompositeReference<>(functionFuture, reference);
	}
	
	public record Factory(Holder<Layer.Factory<Reference<Terrain>>> terrainLayer, DataType<float[]> dataType) implements Layer.Factory<Reference<DensityFunction>> {
		public static final MapCodec<TerrainSamplerLayer.Factory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Layer.<Reference<Terrain>>codec().fieldOf("terrain_layer").forGetter(TerrainSamplerLayer.Factory::terrainLayer),
			DataType.<float[]>codec().fieldOf("data_type").forGetter(TerrainSamplerLayer.Factory::dataType)
		).apply(instance, TerrainSamplerLayer.Factory::new));

		@Override
		public TerrainSamplerLayer createLayer(Layer.Provider provider) {
			Layer<Reference<Terrain>> terrainLayer = provider.getOrCreateLayer(this.terrainLayer);
			return new TerrainSamplerLayer(terrainLayer, this.dataType);
		}

		@Override
		public MapCodec<TerrainSamplerLayer.Factory> codec() {
			return CODEC;
		}

		@Override
		public Layer.Factory<Reference<DensityFunction>> mapAll(Layer.Visitor visitor) {
			return visitor.apply(new TerrainSamplerLayer.Factory(Layer.Factory.mapLayer(this.terrainLayer, visitor), this.dataType));
		}
	}
}
