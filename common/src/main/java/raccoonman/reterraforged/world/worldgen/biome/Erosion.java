package raccoonman.reterraforged.world.worldgen.biome;

public enum Erosion implements BiomeParameter {
    LEVEL_0(-1.0F, -0.78F),
    LEVEL_1(-0.78F, -0.375F),
    LEVEL_2(-0.375F, -0.2225F),
    LEVEL_3(-0.2225F, 0.05F),
    LEVEL_4(0.05F, 0.45F),
    LEVEL_5(0.45F, 0.55F),
    LEVEL_6(0.55F, 1.0F);
	
	private float min;
	private float max;
	
	private Erosion(float min, float max) {
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
