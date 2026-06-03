package raccoonman.reterraforged.world.worldgen.layer.terrain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.RTFCommon;

public record DataType<A>(ResourceLocation name, Function<Terrain, A> factory, Consumer<A> reset) {
	//TODO turn this into a proper Registry
	private static final Map<ResourceLocation, DataType<?>> LOOKUP = new HashMap<>();
	public static final Codec<DataType<?>> CODEC = ResourceLocation.CODEC.xmap(LOOKUP::get, DataType::name);
	
	public A apply(Terrain terrain) {
		return this.factory.apply(terrain);
	}
	
	public void reset(A data) {
		this.reset.accept(data);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <A> Codec<DataType<A>> codec() {
		return (Codec) CODEC;
	}
	
	public static DataType<float[]> floatArray(String name) {
		return register(name, (terrain) -> new float[terrain.cellCount()], (array) -> Arrays.fill(array, 0.0F));
	}
	
	public static DataType<int[]> intArray(String name) {
		return register(name, (terrain) -> new int[terrain.cellCount()], (array) -> Arrays.fill(array, 0));
	}

	public static <A> DataType<A> register(String name, Function<Terrain, A> factory, Consumer<A> reset) {
		ResourceLocation location = RTFCommon.location(name);
		return register(location, factory, reset);
	}
	
	public static <A> DataType<A> register(ResourceLocation name, Function<Terrain, A> factory, Consumer<A> reset) {
		DataType<A> type = new DataType<>(name, factory, reset);
		LOOKUP.put(name, type);
		return type;
	}
}
