package raccoonman.reterraforged.data.preset.registry;

import java.util.Optional;
import java.util.OptionalLong;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.pages.WorldOptions;

public class RTFDimensionTypes {
	
	public static void bootstrap(Preset preset, BootstrapContext<DimensionType> ctx) {
		int minY = preset.getOption(WorldOptions.MIN_Y);
		int maxY = preset.getOption(WorldOptions.MAX_Y);
		int height = -minY + maxY;
		double coordinateScale = preset.getOption(WorldOptions.COORDINATE_SCALE);
		int cloudLevel = preset.getOption(WorldOptions.CLOUD_LEVEL);
		ctx.register(BuiltinDimensionTypes.OVERWORLD, new DimensionType(OptionalLong.empty(), true, false, false, true, coordinateScale, true, false, minY, height, height, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F, Optional.of(cloudLevel), new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
	}
	
	private static ResourceKey<DimensionType> createKey(ResourceLocation location) {
		return ResourceKey.create(Registries.DIMENSION_TYPE, location);
	}
}
