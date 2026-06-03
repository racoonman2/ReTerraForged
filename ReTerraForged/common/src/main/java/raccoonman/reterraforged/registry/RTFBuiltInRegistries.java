package raccoonman.reterraforged.registry;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Registry;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacement;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.CurveFunction;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warp;

public class RTFBuiltInRegistries {
	public static final Registry<MapCodec<? extends Noise>> NOISE_TYPE = RegistryUtil.createRegistry(RTFRegistries.NOISE_TYPE);
	public static final Registry<MapCodec<? extends Warp>> WARP_TYPE = RegistryUtil.createRegistry(RTFRegistries.WARP_TYPE);
	public static final Registry<MapCodec<? extends CurveFunction>> CURVE_FUNCTION_TYPE = RegistryUtil.createRegistry(RTFRegistries.CURVE_FUNCTION_TYPE);
	public static final Registry<MapCodec<? extends TemplatePlacement<?>>> TEMPLATE_PLACEMENT_TYPE = RegistryUtil.createRegistry(RTFRegistries.TEMPLATE_PLACEMENT_TYPE);
	public static final Registry<MapCodec<? extends TemplateDecorator<?>>> TEMPLATE_DECORATOR_TYPE = RegistryUtil.createRegistry(RTFRegistries.TEMPLATE_DECORATOR_TYPE);
	public static final Registry<MapCodec<? extends Layer.Factory<?>>> TERRAIN_LAYER_TYPE = RegistryUtil.createRegistry(RTFRegistries.TERRAIN_LAYER_TYPE);
	
	public static void bootstrap() {
		
	}
}
