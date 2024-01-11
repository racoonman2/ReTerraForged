package raccoonman.reterraforged.data.worldgen.preset.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SurfaceSettings(Erosion erosion) {
	public static final Codec<SurfaceSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Erosion.CODEC.fieldOf("erosion").forGetter(SurfaceSettings::erosion)
	).apply(instance, SurfaceSettings::new));
	
    public SurfaceSettings copy() {
    	return new SurfaceSettings(this.erosion.copy());
    }
    
    public static class Erosion {
    	public static final Codec<Erosion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("rockVariance").forGetter((o) -> o.rockVariance),
    		Codec.INT.fieldOf("rockMin").forGetter((o) -> o.rockMin),
    		Codec.INT.fieldOf("dirtVariance").forGetter((o) -> o.dirtVariance),
    		Codec.INT.fieldOf("dirtMin").forGetter((o) -> o.dirtMin),
    		Codec.FLOAT.fieldOf("rockSteepness").forGetter((o) -> o.rockSteepness),
    		Codec.FLOAT.fieldOf("dirtSteepness").forGetter((o) -> o.dirtSteepness),
    		Codec.FLOAT.fieldOf("screeSteepness").forGetter((o) -> o.screeSteepness)
    	).apply(instance, Erosion::new));
    	
    	public int rockVariance; 
    	public int rockMin;
    	public int dirtVariance;
    	public int dirtMin;
    	public float rockSteepness;
    	public float dirtSteepness;
    	public float screeSteepness;
    	public float screeValue; //TODO
    	
        public Erosion(int rockVariance, int rockMin, int dirtVariance, int dirtMin, float rockSteepness, float dirtSteepness, float screeSteepness) {
        	this.rockVariance = rockVariance;
        	this.rockMin = rockMin;
        	this.dirtVariance = dirtVariance;
        	this.dirtMin = dirtMin;
        	this.rockSteepness = rockSteepness;
        	this.dirtSteepness = dirtSteepness;
        	this.screeSteepness = screeSteepness;
        }
        
        public Erosion copy() {
        	return new Erosion(this.rockVariance, this.rockMin, this.dirtVariance, this.dirtMin, this.rockSteepness, this.dirtSteepness, this.screeSteepness);
        }
    }
}
