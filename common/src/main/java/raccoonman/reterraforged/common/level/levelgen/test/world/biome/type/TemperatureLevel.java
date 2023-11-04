package raccoonman.reterraforged.common.level.levelgen.test.world.biome.type;

public enum TemperatureLevel implements Range {
	LEVEL_0(-1.0F, -0.45F),
	LEVEL_1(-0.45F, -0.15F),
	LEVEL_2(-0.15F, 0.2F),
	LEVEL_3(0.2F, 0.55F),
	LEVEL_4(0.55F, 1.0F);
	
	private float min;
	private float max;
	
	private TemperatureLevel(float min, float max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public float min() {
		return this.min;
	}
	
	@Override
	public float max() {
		return this.max;
	}
}
