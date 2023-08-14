/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WorldSettings(int continentScale, ControlPoints controlPoints, Erosion erosion) {
	public static final WorldSettings DEFAULT = new WorldSettings(400, ControlPoints.DEFAULT, Erosion.DEFAULT);
	
	public static final Codec<WorldSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("continent_scale").forGetter(WorldSettings::continentScale),
		ControlPoints.CODEC.fieldOf("control_points").forGetter(WorldSettings::controlPoints),
		Erosion.CODEC.fieldOf("erosion").forGetter(WorldSettings::erosion)
	).apply(instance, WorldSettings::new));
	
    public record ControlPoints(float deepOcean, float shallowOcean, float beach, float coast, float inland) {
    	public static final ControlPoints DEFAULT = new ControlPoints(0.1F, 0.25F, 0.327F, 0.448F, 0.502F);
    	
    	public static final Codec<ControlPoints> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.floatRange(0.0F, 1.0F).fieldOf("deep_ocean").forGetter(ControlPoints::deepOcean),
    		Codec.floatRange(0.0F, 1.0F).fieldOf("shallow_ocean").forGetter(ControlPoints::shallowOcean),
    		Codec.floatRange(0.0F, 1.0F).fieldOf("beach").forGetter(ControlPoints::beach),
    		Codec.floatRange(0.0F, 1.0F).fieldOf("coast").forGetter(ControlPoints::coast),
    		Codec.floatRange(0.0F, 1.0F).fieldOf("inland").forGetter(ControlPoints::inland)
    	).apply(instance, ControlPoints::new));
    }
    
    public record Erosion(int dropletsPerChunk, int dropletLifetime, float dropletVolume, float dropletVelocity, float erosionRate, float depositeRate) {
    	public static final Erosion DEFAULT = new Erosion(135, 12, 0.7F, 0.7F, 0.5F, 0.5F);
    	
    	public static final Codec<Erosion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.intRange(10, 250).fieldOf("droplets_per_chunk").forGetter(Erosion::dropletsPerChunk),
        	Codec.intRange(1, 32).fieldOf("droplet_lifetime").forGetter(Erosion::dropletLifetime),
        	Codec.floatRange(0.0F, 1.0F).fieldOf("droplet_volume").forGetter(Erosion::dropletVolume),
        	Codec.floatRange(0.1F, 1.0F).fieldOf("droplet_velocity").forGetter(Erosion::dropletVelocity),
        	Codec.floatRange(0.0F, 1.0F).fieldOf("erosion_rate").forGetter(Erosion::erosionRate),
        	Codec.floatRange(0.0F, 1.0F).fieldOf("deposite_rate").forGetter(Erosion::depositeRate)
        ).apply(instance, Erosion::new));
    }
}

