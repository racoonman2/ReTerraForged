package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.CurveFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.ExtensionRuleSource;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

// DeferredRegistry isn't finished so you probably shouldn't use this
public class RTFBuiltInRegistries {
	public static final Registry<Codec<? extends Noise>> NOISE_TYPE = RegistryUtil.createRegistry(RTFRegistries.NOISE_TYPE);
	public static final Registry<Codec<? extends CurveFunction>> CURVE_FUNCTION_TYPE = RegistryUtil.createRegistry(RTFRegistries.CURVE_FUNCTION_TYPE);
	public static final Registry<Codec<? extends Domain>> DOMAIN_TYPE = RegistryUtil.createRegistry(RTFRegistries.DOMAIN_TYPE);
	public static final Registry<Codec<? extends ExtensionRuleSource.ExtensionSource>> SURFACE_EXTENSION_TYPE = RegistryUtil.createRegistry(RTFRegistries.SURFACE_EXTENSION_TYPE);
	
	public static void bootstrap() {
	}
}
