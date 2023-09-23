package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import raccoonman.reterraforged.common.level.levelgen.surface.condition.DensityThresholdCondition;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFSurfaceConditions {

	public static void bootstrap() {
		register("density_threshold", DensityThresholdCondition.CODEC);
	}
	
	private static void register(String name, Codec<? extends ConditionSource> value) {
		RegistryUtil.register(BuiltInRegistries.MATERIAL_CONDITION, name, value);
	}
}
