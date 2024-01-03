package raccoonman.reterraforged.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifier;
import raccoonman.reterraforged.world.worldgen.feature.chance.ChanceModifier;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacement;
import raccoonman.reterraforged.world.worldgen.noise.domain.Domain;
import raccoonman.reterraforged.world.worldgen.noise.function.CurveFunction;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public class RTFBuiltInRegistries {
	public static final Registry<Codec<? extends Noise>> NOISE_TYPE = RegistryUtil.createRegistry(RTFRegistries.NOISE_TYPE);
	public static final Registry<Codec<? extends Domain>> DOMAIN_TYPE = RegistryUtil.createRegistry(RTFRegistries.DOMAIN_TYPE);
	public static final Registry<Codec<? extends CurveFunction>> CURVE_FUNCTION_TYPE = RegistryUtil.createRegistry(RTFRegistries.CURVE_FUNCTION_TYPE);
	public static final Registry<Codec<? extends ChanceModifier>> CHANCE_MODIFIER_TYPE = RegistryUtil.createRegistry(RTFRegistries.CHANCE_MODIFIER_TYPE);
	public static final Registry<Codec<? extends TemplatePlacement<?>>> TEMPLATE_PLACEMENT_TYPE = RegistryUtil.createRegistry(RTFRegistries.TEMPLATE_PLACEMENT_TYPE);
	public static final Registry<Codec<? extends TemplateDecorator<?>>> TEMPLATE_DECORATOR_TYPE = RegistryUtil.createRegistry(RTFRegistries.TEMPLATE_DECORATOR_TYPE);
	public static final Registry<Codec<? extends BiomeModifier>> BIOME_MODIFIER_TYPE = RegistryUtil.createRegistry(RTFRegistries.BIOME_MODIFIER_TYPE);
	
	public static void bootstrap() {
	}
}
