package raccoonman.reterraforged.common.registries.data.noise;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.ClimateCellSampler;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Humidity;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Temperature;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public final class RTFClimateNoise {
	public static final ResourceKey<Noise> CLIMATE_SIZE = resolve("climate_size");
	public static final ResourceKey<Noise> CLIMATE_FREQUENCY = resolve("climate_frequency");
	public static final ResourceKey<Noise> CLIMATE_WARP_SCALE = resolve("climate_warp_scale");
	public static final ResourceKey<Noise> CLIMATE_WARP_STRENGTH = resolve("climate_warp_strength");
	public static final ResourceKey<Noise> CLIMATE_WARP = resolve("climate_warp");
	
	public static final ResourceKey<Noise> TEMPERATURE_SCALER = resolve("temperature_scaler");
	public static final ResourceKey<Noise> TEMPERATURE_SIZE = resolve("temperature_size");
	public static final ResourceKey<Noise> TEMPERATURE_SCALE = resolve("temperature_scale");
	public static final ResourceKey<Noise> TEMPERATURE_BIAS = resolve("temperature_bias");
	public static final ResourceKey<Noise> TEMPERATURE = resolve("temperature");
	
	public static final ResourceKey<Noise> HUMIDITY_SCALER = resolve("humidity_scaler");
	public static final ResourceKey<Noise> HUMIDITY_SIZE = resolve("humidity_size");
	public static final ResourceKey<Noise> HUMIDITY_SCALE = resolve("humidity_scale");
	public static final ResourceKey<Noise> HUMIDITY_BIAS = resolve("humidity_bias");
	public static final ResourceKey<Noise> HUMIDITY = resolve("humidity");

	public static void register(BootstapContext<Noise> ctx) {
		Noise climateSize = register(ctx, CLIMATE_SIZE, Source.constant(225.0D));
		Noise climateFreq = register(ctx, CLIMATE_FREQUENCY, Source.constant(1.0D).div(climateSize));
		Noise climateWarpScale = register(ctx, CLIMATE_WARP_SCALE, Source.constant(150.0F));
		Noise climateWarpStrength = register(ctx, CLIMATE_WARP_STRENGTH, Source.constant(80.0F));
		Noise climateWarp = register(ctx, CLIMATE_WARP, Source.builder().octaves(2).legacySimplex().freq(Source.constant(1.0D).div(climateWarpScale), Source.constant(1.0D).div(climateWarpScale)).bias(-0.5));
		
		Noise tempScaler = register(ctx, TEMPERATURE_SCALER, Source.constant(6));
		Noise tempSize = register(ctx, TEMPERATURE_SIZE, tempScaler.mul(climateSize));
		Noise tempScale = register(ctx, TEMPERATURE_SCALE, tempSize.mul(climateFreq).round());
		Noise tempBias = register(ctx, TEMPERATURE_BIAS, Source.constant(0.05F));
		register(ctx, TEMPERATURE, 
			new ClimateCellSampler(
				new Temperature(Source.constant(1.0D).div(tempScale), 2).bias(tempBias.div(Source.constant(2))).warp(
					Source.builder().octaves(2).build(Source.PERLIN).freq(Source.constant(1.0D).div(tempScale.mul(Source.constant(4))), Source.constant(1.0D).div(tempScale.mul(Source.constant(4)))).shift(1),
					Source.builder().octaves(2).build(Source.PERLIN).freq(Source.constant(1.0D).div(tempScale.mul(Source.constant(4))), Source.constant(1.0D).div(tempScale.mul(Source.constant(4)))).shift(2),
					tempScale.mul(Source.constant(4))
				),
				DistanceFunction.EUCLIDEAN
			).freq(climateFreq, climateFreq).warp(climateWarp.shift(3), climateWarp.shift(4), climateWarpStrength).warp(
				Source.builder().octaves(1).build(Source.PERLIN).freq(Source.constant(1.0D).div(tempScale), Source.constant(1.0D).div(tempScale)).shift(5),
				Source.builder().octaves(1).build(Source.PERLIN).freq(Source.constant(1.0D).div(tempScale), Source.constant(1.0D).div(tempScale)).shift(6),
				tempScale
			)
		);
		
		Noise humidityScaler = register(ctx, HUMIDITY_SCALER, Source.constant(6 * 2.5F));
		Noise humiditySize = register(ctx, HUMIDITY_SIZE, humidityScaler.mul(climateSize));
		Noise humidityScale = register(ctx, HUMIDITY_SCALE, humiditySize.mul(climateFreq).round());
		Noise humidityBias = register(ctx, HUMIDITY_BIAS, Source.constant(0.0F));
		register(ctx, HUMIDITY,
			new ClimateCellSampler(
				new Humidity(Source.builder().octaves(1).legacySimplex().freq(Source.constant(1.0D).div(humidityScale), Source.constant(1.0D).div(humidityScale)).clamp(0.125, 0.875).map(0.0, 1.0).freq(0.5, 1.0), 1).bias(humidityBias.div(Source.constant(1))),
				DistanceFunction.EUCLIDEAN
			).freq(climateFreq, climateFreq).warp(climateWarp.shift(7), climateWarp.shift(8), climateWarpStrength)
		);
	}
	
	private static Noise register(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise value) {
		return new HolderNoise(ctx.register(key, value));
	}

	private static ResourceKey<Noise> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.NOISE, "climate/" + path);
	}
}
