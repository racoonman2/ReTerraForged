package raccoonman.reterraforged.world.worldgen.noise.curvefunction;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import raccoonman.reterraforged.registry.RTFBuiltInRegistries;

public interface CurveFunction {
    public static final Codec<CurveFunction> CODEC = RTFBuiltInRegistries.CURVE_FUNCTION_TYPE.byNameCodec().dispatch(CurveFunction::codec, Function.identity());
	
	float apply(float f);
	
	MapCodec<? extends CurveFunction> codec();
}
