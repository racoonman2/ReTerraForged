package raccoonman.reterraforged.common.worldgen.data;

import java.util.OptionalLong;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings.Properties;

public final class MCDimensionTypes {
	
	public static void bootstrap(BootstapContext<DimensionType> ctx, Preset preset) {
		Properties properties = preset.world().properties;
		ctx.register(BuiltinDimensionTypes.OVERWORLD, new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0D, true, false, -properties.worldDepth, properties.worldHeight, properties.worldHeight, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
    }
}
