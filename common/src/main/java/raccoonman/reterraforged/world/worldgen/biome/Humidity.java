package raccoonman.reterraforged.world.worldgen.biome;

public enum Humidity implements BiomeParameter {
    LEVEL_0(-1.0F, -0.35F),
    LEVEL_1(-0.35F, -0.1F),
    LEVEL_2(-0.1F, 0.1F),
    LEVEL_3(0.1F, 0.3F),
    LEVEL_4(0.3F, 1.0F);
	
	private float min;
    private float max;

    private Humidity(float min, float max) {
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
