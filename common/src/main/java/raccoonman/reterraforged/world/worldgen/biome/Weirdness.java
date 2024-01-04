package raccoonman.reterraforged.world.worldgen.biome;

public enum Weirdness implements BiomeParameter {
    MID_SLICE_NORMAL_ASCENDING(-1.0F, -0.93333334F),
    HIGH_SLICE_NORMAL_ASCENDING(-0.93333334F, -0.7666667F),
    PEAK_NORMAL(-0.7666667F, -0.56666666F),
    HIGH_SLICE_NORMAL_DESCENDING(-0.56666666F, -0.4F),
    MID_SLICE_NORMAL_DESCENDING(-0.4F, -0.26666668F),
    LOW_SLICE_NORMAL_DESCENDING(-0.26666668F, -0.05F),
    VALLEY(-0.05F, 0.05F),
    LOW_SLICE_VARIANT_ASCENDING(0.05F, 0.26666668F),
    MID_SLICE_VARIANT_ASCENDING(0.26666668F, 0.4F),
    HIGH_SLICE_VARIANT_ASCENDING(0.4F, 0.56666666F),
    PEAK_VARIANT(0.56666666F, 0.7666667F),
    HIGH_SLICE_VARIANT_DESCENDING(0.7666667F, 0.93333334F),
    MID_SLICE_VARIANT_DESCENDING(0.93333334F, 1.0F);
	
    private float min;
    private float max;

    private Weirdness(float min, float max) {
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

    public static float peaksAndValleys(float f) {
        return -(Math.abs(Math.abs(f) - 0.6666667f) - 0.33333334f) * 3.0f;
    }

    public static void main(String[] args) {
    	for(Weirdness w : Weirdness.values()) {
    		System.out.println(w.name() + ":  mid=" + w.mid() + ", min=" + peaksAndValleys(w.min) + ", max=" + peaksAndValleys(w.max));
    	}
    	System.out.println(peaksAndValleys(0.34F));
    }
}