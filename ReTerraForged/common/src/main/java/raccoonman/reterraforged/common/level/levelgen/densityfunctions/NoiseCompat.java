package raccoonman.reterraforged.common.level.levelgen.densityfunctions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.noise.Module;
import raccoonman.reterraforged.common.noise.Noise;

public final class NoiseCompat {

	public static DensityFunction seeded(Holder<Noise> noise) {
		return new ModuleFactory(noise);
	}
	
	public record ModuleFactory(Holder<Noise> noise) implements DensityFunction {
		public static final Codec<ModuleFactory> CODEC = Noise.CODEC.xmap(ModuleFactory::new, ModuleFactory::noise);
		
		@Override
		public double compute(FunctionContext ctx) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void fillArray(double[] array, ContextProvider provider) {
			throw new UnsupportedOperationException();
		}

		@Override
		public DensityFunction mapAll(Visitor visitor) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double minValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public double maxValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public KeyDispatchDataCodec<ModuleFactory> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
		
		public DensityFunction forSeed(int seed) {
			return new ModuleDensityFunction(new Module(this.noise, seed));
		}
	}
	
	private record ModuleDensityFunction(Module module) implements DensityFunction.SimpleFunction {
		
		@Override
		public double compute(FunctionContext ctx) {
			return this.module.getValue(ctx.blockX(), ctx.blockZ());
		}

		@Override
		public double minValue() {
			return this.module.type().value().minValue();
		}

		@Override
		public double maxValue() {
			return this.module.type().value().maxValue();
		}

		@Override
		public KeyDispatchDataCodec<ModuleDensityFunction> codec() {
			return new KeyDispatchDataCodec<>(ModuleFactory.CODEC.comapFlatMap((factory) -> {
				return DataResult.error(() -> "Can't deserialize a module");
			}, (module) -> {
				return new ModuleFactory(module.module.type());
			}));
		}
	}
}
