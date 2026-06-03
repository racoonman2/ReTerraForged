package raccoonman.reterraforged.world.worldgen.layer;

import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.registry.RTFBuiltInRegistries;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.registry.RegistryUtil;

public abstract class Layer<A> {
	protected int size;
	
	public Layer(int size) {
		this.size = size;
	}
	
	public int size() {
		return this.size;
	}
	
	public int blockToLayer(int blockCoord) {
		return Math.floorDiv(blockCoord, this.size);
	}
	
	public int layerToBlock(int layerCoord) {
		return layerCoord * this.size;
	}
	
	public int chunkToLayer(int chunkCoord) {
		int blockCoord = SectionPos.sectionToBlockCoord(chunkCoord);
		return this.blockToLayer(blockCoord);
	}
	
	public int layerToChunk(int layerCoord) {
		int blockCoord = this.layerToBlock(layerCoord);
		return SectionPos.blockToSectionCoord(blockCoord);
	}
	
	public abstract A provide(int layerX, int layerY, Executor executor);

	public void addDebugScreenInfo(DebugScreenDisplayer display, ResourceLocation layerName, RandomState randomState, BlockPos pos) {
		int layerX = this.blockToLayer(pos.getX());
		int layerY = this.blockToLayer(pos.getZ());
		display.addToGroup(layerName, "Layer x: " + layerX);
		display.addToGroup(layerName, "Layer y: " + layerY);
	}
	
	public static <A> A provideResult(Layer<A> layer, int layerX, int layerY) {
		return layer.provide(layerX, layerY, (v) -> {
			throw new IllegalStateException();
		});
	}
	
	public static <A> Codec<Holder<Layer.Factory<A>>> codec() {
		return RegistryUtil.codec(Layer.Factory.CODEC);
	}
	
	public interface Provider {
		<A> Layer<A> getOrCreateLayer(Layer.Factory<A> layer);

		default <A> Layer<A> getOrCreateLayer(Holder<Layer.Factory<A>> layer) {
			return this.getOrCreateLayer(layer.value());
		}
	}

	public interface Visitor {
		<A> Layer.Factory<A> apply(Layer.Factory<A> factory);
	}
	
	public interface Factory<A> {
		public static final Codec<Layer.Factory<?>> DIRECT_CODEC = RTFBuiltInRegistries.TERRAIN_LAYER_TYPE.byNameCodec().dispatch(Layer.Factory::codec, Function.identity());
	    public static final Codec<Holder<Layer.Factory<?>>> CODEC = RegistryFileCodec.create(RTFRegistries.LAYER, DIRECT_CODEC);
		public static final Codec<HolderSet<Layer.Factory<?>>> LIST_CODEC = RegistryCodecs.homogeneousList(RTFRegistries.LAYER, DIRECT_CODEC);
	    
		Layer<A> createLayer(Layer.Provider provider);
		
		MapCodec<? extends Layer.Factory<?>> codec();

		Layer.Factory<A> mapAll(Layer.Visitor visitor);

		public static Holder<DensityFunction> mapFunction(Holder<DensityFunction> holder, Layer.Visitor visitor) {
			DensityFunction function = holder.value();
			DensityFunction mapped = mapFunction(function, visitor);
			return mapped != function ? Holder.direct(mapped) : holder;
		}

		public static DensityFunction mapFunction(DensityFunction function, Layer.Visitor visitor) {
			if(visitor instanceof DensityFunction.Visitor functionVisitor) {
				return function.mapAll(functionVisitor);
			}
			return function;
		}

		public static <A> Holder<Layer.Factory<A>> mapLayer(Holder<Layer.Factory<A>> holder, DensityFunction.Visitor visitor) {
			if(visitor instanceof Layer.Visitor layerVisitor) {
				return mapLayer(holder, layerVisitor);
			}
			return holder;
		}
		
		public static <A> Holder<Layer.Factory<A>> mapLayer(Holder<Layer.Factory<A>> holder, Layer.Visitor visitor) {
			Layer.Factory<A> layer = holder.value().mapAll(visitor);
			return Holder.direct(layer);
		}
	}
}
