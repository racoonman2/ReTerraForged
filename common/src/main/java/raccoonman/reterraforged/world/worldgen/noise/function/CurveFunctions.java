package raccoonman.reterraforged.world.worldgen.noise.function;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registries.RTFBuiltInRegistries;

public class CurveFunctions {

	public static void bootstrap() {
		register("interpolation", Interpolation.CODEC);
		register("scurve", SCurveFunction.CODEC);
		register("terrace", TerraceFunction.CODEC);
	}

	public static SCurveFunction scurve(float lower, float upper) {
		return new SCurveFunction(lower, upper);
	}
	
	public static TerraceFunction terrace(float inputRange, float ramp, float cliff, float rampHeight, float blendRange, int steps) {
		return new TerraceFunction(inputRange, ramp, cliff, rampHeight, blendRange, steps);
	}
	
	private static void register(String name, Codec<? extends CurveFunction> value) {
		RegistryUtil.register(RTFBuiltInRegistries.CURVE_FUNCTION_TYPE, name, value);
	}
}
