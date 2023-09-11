package raccoonman.reterraforged.common.registries.data.preset.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class FilterSettings {
	public static final Codec<FilterSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Erosion.CODEC.fieldOf("erosion").forGetter((o) -> o.erosion),
		Smoothing.CODEC.fieldOf("smoothing").forGetter((o) -> o.smoothing)
	).apply(instance, FilterSettings::new));
	
	public static final FilterSettings DEFAULT = new FilterSettings(Erosion.DEFAULT, Smoothing.DEFAULT);
	
    public Erosion erosion;
    public Smoothing smoothing;
    
    public FilterSettings(Erosion erosion, Smoothing smoothing) {
    	this.erosion = erosion;
    	this.smoothing = smoothing;
    }
    
    public static class Erosion {
    	public static final Codec<Erosion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("dropletsPerChunk").forGetter((o) -> o.dropletsPerChunk),
    		Codec.INT.fieldOf("dropletLifetime").forGetter((o) -> o.dropletLifetime),
    		Codec.FLOAT.fieldOf("dropletVolume").forGetter((o) -> o.dropletVolume),
    		Codec.FLOAT.fieldOf("dropletVelocity").forGetter((o) -> o.dropletVelocity),
    		Codec.FLOAT.fieldOf("erosionRate").forGetter((o) -> o.erosionRate),
    		Codec.FLOAT.fieldOf("depositeRate").forGetter((o) -> o.depositeRate)
    	).apply(instance, Erosion::new));
    	
    	public static final Erosion DEFAULT = new Erosion(135, 12, 0.7F, 0.7F, 0.5F, 0.5F);
    	
    	public int dropletsPerChunk;
        public int dropletLifetime;
        public float dropletVolume;
        public float dropletVelocity;
        public float erosionRate;
        public float depositeRate;
        
        public Erosion(int dropletsPerChunk, int dropletsLifetime, float dropletVolume, float dropletVelocity, float erosionRate, float depositeRate) {
        	this.dropletsPerChunk = dropletsPerChunk;
        	this.dropletLifetime = dropletsLifetime;
        	this.dropletVolume = dropletVolume;
        	this.dropletVelocity = dropletVelocity;
        	this.erosionRate = erosionRate;
        	this.depositeRate = depositeRate;
        }
    }
    
    public static class Smoothing {
    	public static final Codec<Smoothing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("iterations").forGetter((o) -> o.iterations),
    		Codec.FLOAT.fieldOf("smoothingRadius").forGetter((o) -> o.smoothingRadius),
    		Codec.FLOAT.fieldOf("smoothingRate").forGetter((o) -> o.smoothingRate)    		
    	).apply(instance, Smoothing::new));
    	
    	public static final Smoothing DEFAULT = new Smoothing(1, 1.8F, 0.9F);
    	
        public int iterations;
        public float smoothingRadius;
        public float smoothingRate;
        
        public Smoothing(int iterations, float smoothingRadius, float smoothingRate)  {
        	this.iterations = iterations;
        	this.smoothingRadius = smoothingRadius;
        	this.smoothingRate = smoothingRate;
        }
    }
}
