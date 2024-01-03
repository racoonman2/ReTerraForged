package raccoonman.reterraforged.world.worldgen.floatproviders;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;
import raccoonman.reterraforged.platform.RegistryUtil;

public class RTFFloatProviderTypes {
	public static final FloatProviderType<LegacyCanyonYScale> LEGACY_CANYON_Y_SCALE = register("legacy_canyon_y_scale", LegacyCanyonYScale.CODEC);
	
	public static void bootstrap() {
	}
	
	private static <T extends FloatProvider> FloatProviderType<T> register(String name, Codec<T> codec) {
		FloatProviderType<T> type = () -> codec;
		RegistryUtil.register(BuiltInRegistries.FLOAT_PROVIDER_TYPE, name, type);
		return type;
	}
}
