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
    		Codec.INT.fieldOf("snowHeight").forGetter((o) -> o.snowHeight),
    		Codec.FLOAT.fieldOf("rockSteepness").forGetter((o) -> o.rockSteepness),
    		Codec.FLOAT.fieldOf("dirtSteepness").forGetter((o) -> o.dirtSteepness),
    		Codec.FLOAT.fieldOf("screeSteepness").forGetter((o) -> o.screeSteepness),
    		Codec.FLOAT.fieldOf("snowSteepness").forGetter((o) -> o.snowSteepness),
    		Codec.FLOAT.fieldOf("heightModifier").forGetter((o) -> o.heightModifier),
    		Codec.FLOAT.fieldOf("slopeModifier").forGetter((o) -> o.slopeModifier)
    	).apply(instance, Erosion::new));
    	
    	public int rockVariance; 
    	public int rockMin;
    	public int dirtVariance;
    	public int dirtMin;
    	public int snowHeight;
    	public float rockSteepness;
    	public float dirtSteepness;
    	public float screeSteepness;
    	public float snowSteepness;
    	public float heightModifier; 
    	public float slopeModifier;

    	public float screeValue; //TODO

        public Erosion(int rockVariance, int rockMin, int dirtVariance, int dirtMin, int snowHeight, float rockSteepness, float dirtSteepness, float screeSteepness, float snowSteepness, float heightModifier, float slopeModifier) {
        	this.rockVariance = rockVariance;
        	this.rockMin = rockMin;
        	this.dirtVariance = dirtVariance;
        	this.dirtMin = dirtMin;
        	this.snowHeight = snowHeight;
        	this.rockSteepness = rockSteepness;
        	this.dirtSteepness = dirtSteepness;
        	this.screeSteepness = screeSteepness;
        	this.snowSteepness = snowSteepness;
        	this.heightModifier = heightModifier;
        	this.slopeModifier = slopeModifier;
        }
        
        public Erosion copy() {
        	return new Erosion(this.rockVariance, this.rockMin, this.dirtVariance, this.dirtMin, this.snowHeight, this.rockSteepness, this.dirtSteepness, this.screeSteepness, this.snowSteepness, this.heightModifier, this.slopeModifier);
        }
    }
}
