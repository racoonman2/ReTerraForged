package raccoonman.reterraforged.world.worldgen.modifier;

import com.mojang.serialization.MapCodec;

import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import raccoonman.reterraforged.platform.LithostitchedUtil;
import raccoonman.reterraforged.platform.RegistryUtil;

public class RTFWorldGenModifiers {
	
	public static void bootstrap() {
		register("append_surface_rule", AppendSurfaceRuleModifier.CODEC);
		register("set_lava_level", SetLavaLevelModifier.CODEC);
		register("set_spawn_finder", SetSpawnFinderModifier.CODEC);
		register("add_feature_placement", AddFeaturePlacementModifier.CODEC);
		register("add_feature", AddFeatureModifier.CODEC);
		register("replace_feature", ReplaceFeatureModifier.CODEC);
		register("insert_feature", InsertFeatureModifier.CODEC);
		register("remove_feature", RemoveFeatureModifier.CODEC);
		register("set_structure_placement", StructurePlacementModifier.CODEC);
		register("set_max_height_layer", SetMaxHeightLayerModifier.CODEC);
	}
	
	public static void register(String name, MapCodec<? extends Modifier> value) {
		RegistryUtil.register(LithostitchedUtil.getModifierTypeRegistry(), name, value);
	}
}
