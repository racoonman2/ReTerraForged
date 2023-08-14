package raccoonman.reterraforged.common.level.levelgen.densityfunctions;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainData;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainLevels;

//TODO remove Suppliers from fields
public record HeightDensityFunction(Supplier<TerrainData> terrainData, Supplier<NoiseGeneratorSettings> settings) implements DensityFunction.SimpleFunction {
    
	@Override
	public double compute(FunctionContext ctx) {
		int x = ctx.blockX();
		int y = ctx.blockY();
		int z = ctx.blockZ();
		int solidY = this.terrainData.get().getHeight(x, z);
		int seaLevel = this.settings.get().seaLevel();
		int waterY = TerrainLevels.getWaterLevel(x, z, seaLevel, this.terrainData.get());
		int top = Math.max(solidY, waterY);
		return y < top + 1 ? 1.0D - ((double) y / top) : 0.0D;
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
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
		throw new UnsupportedOperationException();
	}
	
	public enum Marker implements DensityFunction.SimpleFunction {
		INSTANCE;
		
		public static final KeyDispatchDataCodec<HeightDensityFunction.Marker> CODEC = new KeyDispatchDataCodec<>(Codec.unit(INSTANCE));

		@Override
		public double compute(FunctionContext var1) {
			throw new UnsupportedOperationException();
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
		public KeyDispatchDataCodec<HeightDensityFunction.Marker> codec() {
			return CODEC;
		}
	}
}