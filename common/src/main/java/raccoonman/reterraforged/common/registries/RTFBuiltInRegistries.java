package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.domain.Domain;
import raccoonman.reterraforged.common.noise.func.CurveFunction;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

// DeferredRegister isn't finished so you probably shouldn't use this
public class RTFBuiltInRegistries {
	public static final Registry<Codec<? extends Noise>> NOISE_TYPE = RegistryUtil.createRegistry(RTFRegistries.NOISE_TYPE);
	public static final Registry<Codec<? extends CurveFunction>> CURVE_FUNCTION_TYPE = RegistryUtil.createRegistry(RTFRegistries.CURVE_FUNCTION_TYPE);
	public static final Registry<Codec<? extends Domain>> DOMAIN_TYPE = RegistryUtil.createRegistry(RTFRegistries.DOMAIN_TYPE);
	
	public static void bootstrap() {
	}
}
