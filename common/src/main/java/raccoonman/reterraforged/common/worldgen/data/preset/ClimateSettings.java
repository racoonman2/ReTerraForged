package raccoonman.reterraforged.common.worldgen.data.preset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;

public class ClimateSettings {
	public static final Codec<ClimateSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RangeValue.CODEC.fieldOf("temperature").forGetter((o) -> o.temperature),
		RangeValue.CODEC.fieldOf("moisture").forGetter((o) -> o.moisture),
		BiomeShape.CODEC.fieldOf("biomeShape").forGetter((o) -> o.biomeShape),
		BiomeNoise.CODEC.fieldOf("biomeEdgeShape").forGetter((o) -> o.biomeEdgeShape)
	).apply(instance, ClimateSettings::new));
	
	public RangeValue temperature;
    public RangeValue moisture;
    public BiomeShape biomeShape;
    public BiomeNoise biomeEdgeShape;
    
    public ClimateSettings(RangeValue temperature, RangeValue moisture, BiomeShape biomeShape, BiomeNoise biomeEdgeShape) {
    	this.temperature = temperature;
    	this.moisture = moisture;
    	this.biomeShape = biomeShape;
    	this.biomeEdgeShape = biomeEdgeShape;
    }
    
    public ClimateSettings copy() {
    	return new ClimateSettings(this.temperature.copy(), this.moisture.copy(), this.biomeShape.copy(), this.biomeEdgeShape.copy());
    }
    
    public static ClimateSettings makeDefault() {
    	return new ClimateSettings(new RangeValue(0, 6, 2, 0.0F, 0.98F, 0.05F), new RangeValue(0, 6, 1, 0.0F, 1.0F, 0.0F), BiomeShape.makeDefault(), BiomeNoise.makeDefault());
    }
    
    public static class RangeValue {
    	public static final Codec<RangeValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("seedOffset").forGetter((o) -> o.seedOffset),
    		Codec.INT.fieldOf("scale").forGetter((o) -> o.scale),
    		Codec.INT.fieldOf("falloff").forGetter((o) -> o.falloff),
    		Codec.FLOAT.fieldOf("min").forGetter((o) -> o.min),
    		Codec.FLOAT.fieldOf("max").forGetter((o) -> o.max),
    		Codec.FLOAT.fieldOf("bias").forGetter((o) -> o.bias)
    	).apply(instance, RangeValue::new));
    	
        public int seedOffset;
        public int scale;
        public int falloff;
        public float min;
        public float max;
        public float bias;
        
        public RangeValue(int seedOffset, int scale, int falloff, float min, float max, float bias) {
            this.seedOffset = seedOffset;
            this.min = min;
            this.max = max;
            this.bias = bias;
            this.scale = scale;
            this.falloff = falloff;
        }
        
        public float getMin() {
            return NoiseUtil.clamp(Math.min(this.min, this.max), 0.0F, 1.0F);
        }
        
        public float getMax() {
            return NoiseUtil.clamp(Math.max(this.min, this.max), this.getMin(), 1.0F);
        }
        
        public float getBias() {
            return NoiseUtil.clamp(this.bias, -1.0F, 1.0F);
        }
        
        public Noise apply(Noise module) {
            float min = this.getMin();
            float max = this.getMax();
            float bias = this.getBias() / 2.0F;
            return module.bias(bias).clamp(min, max);
        }
        
        public RangeValue copy() {
        	return new RangeValue(this.seedOffset, this.scale, this.falloff, this.min, this.max, this.bias);
        }
    }
    
    public static class BiomeShape {
    	public static final Codec<BiomeShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("biomeSize").forGetter((o) -> o.biomeSize),
    		Codec.INT.fieldOf("macroNoiseSize").forGetter((o) -> o.macroNoiseSize),
    		Codec.INT.fieldOf("biomeWarpScale").forGetter((o) -> o.biomeWarpScale),
    		Codec.INT.fieldOf("biomeWarpStrength").forGetter((o) -> o.biomeWarpStrength)    		
    	).apply(instance, BiomeShape::new));
    	
        public int biomeSize;
        public int macroNoiseSize;
        public int biomeWarpScale;
        public int biomeWarpStrength;
        
        public BiomeShape(int biomeSize, int macroNoiseSize, int biomeWarpScale, int biomeWarpStrength) {
        	this.biomeSize = biomeSize;
        	this.macroNoiseSize = macroNoiseSize;
        	this.biomeWarpScale = biomeWarpScale;
        	this.biomeWarpStrength = biomeWarpStrength;
        }
        
        public BiomeShape copy() {
        	return new BiomeShape(this.biomeSize, this.macroNoiseSize, this.biomeWarpScale, this.biomeWarpStrength);
        }
        
        public static BiomeShape makeDefault() {
        	return new BiomeShape(225, 8, 150, 80);
        }
    }
    
    public static class BiomeNoise {
    	public static final Codec<BiomeNoise> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Source.CODEC.fieldOf("type").forGetter((o) -> o.type),
    		Codec.INT.fieldOf("scale").forGetter((o) -> o.scale),
    		Codec.INT.fieldOf("octaves").forGetter((o) -> o.octaves),
    		Codec.FLOAT.fieldOf("gain").forGetter((o) -> o.gain),
    		Codec.FLOAT.fieldOf("lacunarity").forGetter((o) -> o.lacunarity),
    		Codec.INT.fieldOf("strength").forGetter((o) -> o.strength)
    	).apply(instance, BiomeNoise::new));
    	
        public Source type;
        public int scale;
        public int octaves;
        public float gain;
        public float lacunarity;
        public int strength;

        public BiomeNoise(Source type, int scale, int octaves, float gain, float lacunarity, int strength) {
            this.type = type;
            this.scale = scale;
            this.octaves = octaves;
            this.gain = gain;
            this.lacunarity = lacunarity;
            this.strength = strength;
        }
        
        public Noise build() {
            return Source.build(this.scale, this.octaves).gain(this.gain).lacunarity(this.lacunarity).build(this.type).bias(-0.5F);
        }
        
        public BiomeNoise copy() {
        	return new BiomeNoise(this.type, this.scale, this.octaves, this.gain, this.lacunarity, this.strength);
        }
        
        public static BiomeNoise makeDefault() {
        	return new BiomeNoise(Source.SIMPLEX, 24, 2, 0.5F, 2.65F, 14);
        }
    }
}
