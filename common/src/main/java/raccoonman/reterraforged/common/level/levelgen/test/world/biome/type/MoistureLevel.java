package raccoonman.reterraforged.common.level.levelgen.test.world.biome.type;

public enum MoistureLevel implements Range {
	LEVEL_0(-1.0F, -0.35F),
	LEVEL_1(-0.35F, -0.1F),
	LEVEL_2(-0.1F, 0.1F),
	LEVEL_3(0.1F, 0.3F),
	LEVEL_4(0.3F, 1.0F);
	
	private float min;
	private float max;
	
	private MoistureLevel(float min, float max) {
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
