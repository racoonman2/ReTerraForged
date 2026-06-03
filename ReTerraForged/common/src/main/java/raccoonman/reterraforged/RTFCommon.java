package raccoonman.reterraforged;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.commands.RTFCommands;
import raccoonman.reterraforged.debug.RTFDebugScreenEntries;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registry.RTFBuiltInRegistries;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.ParameterSlice;
import raccoonman.reterraforged.world.RTFGameRules;
import raccoonman.reterraforged.world.worldgen.carvers.RTFCarvers;
import raccoonman.reterraforged.world.worldgen.densityfunction.RTFDensityFunctions;
import raccoonman.reterraforged.world.worldgen.feature.RTFFeatures;
import raccoonman.reterraforged.world.worldgen.feature.placement.RTFPlacementModifiers;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorators;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacements;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.LayerTypes;
import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.Noises;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.CurveFunctions;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warp;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warps;
import raccoonman.reterraforged.world.worldgen.structure.RTFStructurePlacements;
import raccoonman.reterraforged.world.worldgen.surface.RTFSurfaceRules;

public class RTFCommon {
	public static final String MOD_ID = "reterraforged";
	public static final Logger LOGGER = LogManager.getLogger("ReTerraForged");

	public static void bootstrap() {
		RTFBuiltInRegistries.bootstrap();
		RTFDensityFunctions.bootstrap();
		RTFSurfaceRules.bootstrap();
		RTFPlacementModifiers.bootstrap();
		RTFFeatures.bootstrap();
		RTFCarvers.bootstrap();
		RTFStructurePlacements.bootstrap();
		RTFCommands.bootstrap();
//		RTFArgumentTypeInfos.bootstrap();
		RTFGameRules.bootstrap();
		RTFDebugScreenEntries.bootstrap();
		Noises.bootstrap();
		Warps.bootstrap();
		CurveFunctions.bootstrap();
		TemplatePlacements.bootstrap();
		TemplateDecorators.bootstrap();
		LayerTypes.bootstrap();
		
		RegistryUtil.createDataRegistry(RTFRegistries.NOISE, Noise.DIRECT_CODEC, false);
		RegistryUtil.createDataRegistry(RTFRegistries.WARP, Warp.DIRECT_CODEC, false);
		RegistryUtil.createDataRegistry(RTFRegistries.PARAMETER_SLICE, ParameterSlice.DIRECT_CODEC, true);
		RegistryUtil.createDataRegistry(RTFRegistries.LAYER, Layer.Factory.DIRECT_CODEC, false);
	}
	
	public static ResourceLocation location(String name) {
		if (name.contains(":")) return ResourceLocation.parse(name);
		return ResourceLocation.fromNamespaceAndPath(RTFCommon.MOD_ID, name);
	}
}
