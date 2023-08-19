/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public record ClimateSettings(int climateListScale, RangeValue temperature, RangeValue humidity, BiomeShape biomeShape) {
	public static final ClimateSettings DEFAULT = new ClimateSettings(
		3,
		new RangeValue(7, 6, 2, 0.0f, 0.98f, 0.05f), 
		new RangeValue(7, 6, 1, 0.0f, 1.0f, 0.0f), 
		BiomeShape.DEFAULT 
	);
	
	public static final Codec<ClimateSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("climate_list_scale").forGetter(ClimateSettings::climateListScale),
		RangeValue.CODEC.optionalFieldOf("temperature", new RangeValue(0, 6, 2, 0.0F, 0.98F, 0.05F)).forGetter(ClimateSettings::temperature),
		RangeValue.CODEC.optionalFieldOf("moisture", new RangeValue(0, 6, 1, 0.0F, 1.0F, 0.0F)).forGetter(ClimateSettings::humidity),
		BiomeShape.CODEC.fieldOf("biome_shape").forGetter(ClimateSettings::biomeShape)
	).apply(instance, ClimateSettings::new));

    public record BiomeShape(int biomeSize, int biomeWarpScale) {
    	public static final BiomeShape DEFAULT = new BiomeShape(225, 150);
    	
    	public static final Codec<BiomeShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.intRange(50, 2000).fieldOf("biome_size").forGetter(BiomeShape::biomeSize),
    		Codec.intRange(1, 500).fieldOf("biome_warp_scale").forGetter(BiomeShape::biomeWarpScale)
    	).apply(instance, BiomeShape::new));
    }
    
    public record RangeValue(int seedOffset, int scale, int falloff, float min, float max, float bias) {
    	public static final Codec<RangeValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("seed_offset").forGetter(RangeValue::seedOffset),
    		Codec.intRange(1, 20).fieldOf("scale").forGetter(RangeValue::scale),
    		Codec.intRange(1, 10).fieldOf("falloff").forGetter(RangeValue::falloff),
    		Codec.floatRange(0.0F, 1.0F).fieldOf("min").forGetter(RangeValue::min),
    		Codec.floatRange(0.0F, 1.0F).fieldOf("max").forGetter(RangeValue::max),
    		Codec.floatRange(-1.0F, 1.0F).fieldOf("bias").forGetter(RangeValue::bias)
    	).apply(instance, RangeValue::new));

        public float getMin() {
            return NoiseUtil.clamp(Math.min(this.min, this.max), 0.0f, 1.0f);
        }

        public float getMax() {
            return NoiseUtil.clamp(Math.max(this.min, this.max), this.getMin(), 1.0f);
        }

        public float getBias() {
            return NoiseUtil.clamp(this.bias, -1.0f, 1.0f);
        }

        public Noise apply(Noise module) {
            float min = this.getMin();
            float max = this.getMax();
            float bias = this.getBias() / 2.0f;
            return module.bias(bias).clamp(min, max);
        }
    }
}

