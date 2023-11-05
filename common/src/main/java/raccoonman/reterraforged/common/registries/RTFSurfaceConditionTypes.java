package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import raccoonman.reterraforged.common.level.levelgen.surface.condition.NoiseThresholdCondition;
import raccoonman.reterraforged.platform.RegistryUtil;

public final class RTFSurfaceConditionTypes {

	public static void bootstrap() {
		register("noise_threshold", NoiseThresholdCondition.CODEC);
	}
	
	private static void register(String name, Codec<? extends ConditionSource> value) {
		RegistryUtil.register(BuiltInRegistries.MATERIAL_CONDITION, name, value);
	}
}
