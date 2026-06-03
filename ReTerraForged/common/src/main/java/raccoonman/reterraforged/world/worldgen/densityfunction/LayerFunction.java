package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.Reference;

public class LayerFunction implements MappedFunction, SimpleFunction {
	private Layer<Reference<DensityFunction>> layer;
	private DensityFunction fallback;
	
	public LayerFunction(Layer<Reference<DensityFunction>> layer, DensityFunction fallback) {
		this.layer = layer;
		this.fallback = fallback;
	}
	
	public Layer<Reference<DensityFunction>> getLayer() {
		return this.layer;
	}
	
	public DensityFunction getFallback() {
		return this.fallback;
	}
	
	@Override
	public double compute(FunctionContext ctx) {
//		int blockX = ctx.blockX();
//		int blockZ = ctx.blockZ();
//		int chunkX = this.layer.chunkCoord(blockX);
//		int chunkZ = this.layer.chunkCoord(blockZ);
//		DensityFunction function = this.layer.provide(chunkX, chunkZ, Util.backgroundExecutor()).join();
		return this.fallback.compute(ctx);//function.compute(ctx);
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}
	
	@Override
	public DensityFunction mapAll(DensityFunction.Visitor visitor) {
		return visitor.apply(new LayerFunction(this.layer, this.fallback.mapAll(visitor)));
	}
	
	public record Marker(Holder<Layer.Factory<Reference<DensityFunction>>> layer, DensityFunction fallback) implements MappedFunction.Marker {
		public static final MapCodec<LayerFunction.Marker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Layer.<Reference<DensityFunction>>codec().fieldOf("layer").forGetter(LayerFunction.Marker::layer),
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("fallback").forGetter(LayerFunction.Marker::fallback)
		).apply(instance, LayerFunction.Marker::new));
		
		@Override
		public DensityFunction mapAll(DensityFunction.Visitor visitor) {
			return visitor.apply(new LayerFunction.Marker(Layer.Factory.mapLayer(this.layer, visitor), this.fallback.mapAll(visitor)));
		}

		@Override
		public KeyDispatchDataCodec<LayerFunction.Marker> codec() {
			return KeyDispatchDataCodec.of(CODEC);
		}
	}
}
