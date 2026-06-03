package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.MapCodec;

public record CellValue() implements Noise {
	public static final MapCodec<CellValue> CODEC = MapCodec.unit(CellValue::new);
	
	@Override
	public float compute(float x, float z, int seed) {
		return NoiseUtil.map(NoiseUtil.valCoord2D(seed, (int) x, (int) z), -1.0F, 1.0F, 2.0F);
	}

	@Override
	public float minValue() {
		return 0.0F;
	}

	@Override
	public float maxValue() {
		return 1.0F;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}

	@Override
	public MapCodec<CellValue> codec() {
		return CODEC;
	}
}
