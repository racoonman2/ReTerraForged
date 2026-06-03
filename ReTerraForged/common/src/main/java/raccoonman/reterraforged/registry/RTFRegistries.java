package raccoonman.reterraforged.registry;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.world.ParameterSlice;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacement;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.CurveFunction;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warp;

public class RTFRegistries {
	public static final ResourceKey<Registry<MapCodec<? extends Noise>>> NOISE_TYPE = createRegistryKey("worldgen/noise_type");
	public static final ResourceKey<Registry<MapCodec<? extends Warp>>> WARP_TYPE = createRegistryKey("worldgen/warp_type");
	public static final ResourceKey<Registry<MapCodec<? extends CurveFunction>>> CURVE_FUNCTION_TYPE = createRegistryKey("worldgen/curve_function_type");
	public static final ResourceKey<Registry<MapCodec<? extends TemplatePlacement<?>>>> TEMPLATE_PLACEMENT_TYPE = createRegistryKey("worldgen/template_placement_type");
	public static final ResourceKey<Registry<MapCodec<? extends TemplateDecorator<?>>>> TEMPLATE_DECORATOR_TYPE = createRegistryKey("worldgen/template_decorator_type");
	public static final ResourceKey<Registry<MapCodec<? extends Layer.Factory<?>>>> TERRAIN_LAYER_TYPE = createRegistryKey("worldgen/terrain_layer_type");
	public static final ResourceKey<Registry<Noise>> NOISE = createRegistryKey("worldgen/noise");
	public static final ResourceKey<Registry<Warp>> WARP = createRegistryKey("worldgen/warp");
	public static final ResourceKey<Registry<ParameterSlice>> PARAMETER_SLICE = createRegistryKey("worldgen/parameter_slice");
	public static final ResourceKey<Registry<Layer.Factory<?>>> LAYER = createRegistryKey("worldgen/layer");

	public static <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> registryKey, String valueKey) {
		return ResourceKey.create(registryKey, RTFCommon.location(valueKey));
	}
	
	public static <T> ResourceKey<Registry<T>> createRegistryKey(String key) {
		return ResourceKey.createRegistryKey(RTFCommon.location(key));
	}
}
