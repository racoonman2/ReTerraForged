package raccoonman.reterraforged.world.worldgen.surface.rule;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.surface.rule.StrataRule.Strata;

public class RTFSurfaceRules {

	public static void bootstrap() {
		register("strata", StrataRule.CODEC);
	}
	
	public static StrataRule strata(ResourceLocation name, Holder<Noise> selector, List<Strata> strata, int iterations) {
		return new StrataRule(name, selector, strata, iterations);
	}
	
	public static void register(String name, Codec<? extends SurfaceRules.RuleSource> value) {
		RegistryUtil.register(BuiltInRegistries.MATERIAL_RULE, name, value);
	}
}
