package raccoonman.reterraforged.common.registries.data;

import java.util.OptionalLong;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

public final class RTFDimensionTypes {
	
	public static void bootstrap(BootstapContext<DimensionType> ctx) {
		final int worldHeight = 1024;
		ctx.register(BuiltinDimensionTypes.OVERWORLD, new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0D, true, false, -64, worldHeight, worldHeight, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
    }
}
