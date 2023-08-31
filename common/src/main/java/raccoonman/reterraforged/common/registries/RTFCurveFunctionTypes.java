package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.level.levelgen.noise.curve.CurveFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.MidPointCurve;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.SCurve;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFCurveFunctionTypes {
	
	public static void bootstrap() {
		register("interpolation", Interpolation.CODEC);
		register("mid_point_curve", MidPointCurve.CODEC);
		register("scurve", SCurve.CODEC);
	}
	
	private static void register(String name, Codec<? extends CurveFunction> value) {
		RegistryUtil.register(RTFBuiltInRegistries.CURVE_FUNCTION_TYPE, name, value);
	}
}
