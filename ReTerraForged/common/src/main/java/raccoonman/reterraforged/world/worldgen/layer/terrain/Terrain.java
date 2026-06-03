package raccoonman.reterraforged.world.worldgen.layer.terrain;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import raccoonman.reterraforged.world.worldgen.layer.Resource;

public class Terrain extends CellGrid implements Resource {
	public static final DataType<float[]> HEIGHT = DataType.floatArray("height");

	private Terrain.Data data;

	public Terrain(int size, int cellSize, Terrain.Data data) {
		super(size, cellSize);
		this.data = data;
	}
	
	@SuppressWarnings("unchecked")
	public <A> A provide(DataType<A> type) {
		return (A) this.data.map.computeIfAbsent(type, (v) -> type.apply(this));
	}

	@Override
	public void close() {
		this.data.close();
	}
	
	public static float getScaledHeight(float height) {
		return height * 256.0F;
	}
	
	public static class Data implements Resource {
		public Map<DataType<?>, Object> map;
		private Consumer<Terrain.Data> closeCallback;
		
		public Data(Consumer<Terrain.Data> closeCallback) {
			this.map = new HashMap<>();
			this.closeCallback = closeCallback;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void close() {
			this.map.forEach((type, value) -> ((DataType) type).reset(value));
			this.closeCallback.accept(this);
		}
	}
}
