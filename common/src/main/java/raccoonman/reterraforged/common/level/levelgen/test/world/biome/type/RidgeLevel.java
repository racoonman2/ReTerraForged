package raccoonman.reterraforged.common.level.levelgen.test.world.biome.type;

public enum RidgeLevel implements Range {
	VALLEYS(-0.05F, 0.05F),
	LOW_SLICE(-0.26666668F, -0.05F),
	MID_SLICE(-0.4F, -0.26666668F),
	HIGH_SLICE(-0.56666666F, -0.4F),
	PEAKS(-0.7666667F, -0.56666666F);
	
	private float min;
	private float max;
	
	private RidgeLevel(float min, float max) {
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
