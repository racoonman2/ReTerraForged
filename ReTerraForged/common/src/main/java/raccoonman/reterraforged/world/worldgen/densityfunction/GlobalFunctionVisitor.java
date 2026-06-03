package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.Reference;
import raccoonman.reterraforged.world.worldgen.layer.RequiredLayer;
import raccoonman.reterraforged.world.worldgen.noise.Noise;

public class GlobalFunctionVisitor implements DensityFunction.Visitor, Layer.Visitor {
	private RandomState randomState;
	private long seed;
	private int noiseSeed;
	private Map<Layer.Factory<?>, Layer.Factory<?>> mappedLayers;
	private Map<DensityFunction, DensityFunction> mappedFunctions;
	
	public GlobalFunctionVisitor(RandomState randomState, long seed) {
		this.randomState = randomState;
		this.seed = seed;
		this.noiseSeed = randomState.random.fromSeed(seed).nextInt();
		this.mappedLayers = new HashMap<>();
		this.mappedFunctions = new HashMap<>();
	}

	public long seed() {
		return this.seed;
	}
	
	public int noiseSeed() {
		return this.noiseSeed;
	}

	public DensityFunction mapForChunk(DensityFunction function, NoiseChunk noiseChunk) {
		return function.mapAll(this).mapAll(noiseChunk::wrap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A> Layer.Factory<A> apply(Layer.Factory<A> factory) {
//		return (Layer.Factory<A>) this.mappedLayers.computeIfAbsent(factory, (k) -> k);
		return factory;
	}
	
	@Override
	public DensityFunction apply(DensityFunction function) {
		RTFRandomState rtfRandomState = ExtensionUtil.cast(this.randomState);
		return this.mappedFunctions.computeIfAbsent(function, (v) -> {
			if(function instanceof NoiseFunction.Marker marker) {
				Noise noise = marker.noise().value();
				return new NoiseFunction(noise, this.noiseSeed);
			}
			
			if(function instanceof LayerFunction.Marker marker) {
				Holder<Layer.Factory<Reference<DensityFunction>>> layerHolder = marker.layer();
				Layer<Reference<DensityFunction>> layer = rtfRandomState.getOrCreateLayer(layerHolder);
				DensityFunction fallback = marker.fallback();
				Set<RequiredLayer> requiredLayers = rtfRandomState.getRequiredLayers();
				requiredLayers.add(new RequiredLayer(layer, 8));
				return new LayerFunction(layer, fallback);
			}
			
			if(function instanceof WeightedFunction.Marker marker) {
				DensityFunction input = marker.input();
				DensityFunction[] baked = marker.bake();
				return new WeightedFunction(input, baked);
			}
			return function;
		});
	}

	@Override
	public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder noiseHolder) {
		Holder<NormalNoise.NoiseParameters> holder = noiseHolder.noiseData();
		NormalNoise normalNoise = this.randomState.getOrCreateNoise(holder.unwrapKey().orElseThrow());
		return new DensityFunction.NoiseHolder(holder, normalNoise);
	}
}
