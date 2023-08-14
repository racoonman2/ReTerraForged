package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.noise.func.CurveFunction;
import raccoonman.reterraforged.common.noise.func.Interpolation;
import raccoonman.reterraforged.common.noise.func.MidPointCurve;
import raccoonman.reterraforged.common.noise.func.SCurve;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFCurveFunctionTypes {
	public static final ResourceKey<Codec<? extends CurveFunction>> INTERPOLATION = resolve("interpolation");
	public static final ResourceKey<Codec<? extends CurveFunction>> MID_POINT_CURVE = resolve("mid_point_curve");
	public static final ResourceKey<Codec<? extends CurveFunction>> SCURVE = resolve("scurve");
	
	public static void register() {
		RegistryUtil.register(INTERPOLATION, () -> Interpolation.CODEC);
		RegistryUtil.register(MID_POINT_CURVE, () -> MidPointCurve.CODEC);
		RegistryUtil.register(SCURVE, () -> SCurve.CODEC);
	}
	
	private static ResourceKey<Codec<? extends CurveFunction>> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.CURVE_FUNCTION_TYPE, path);
	}
}
