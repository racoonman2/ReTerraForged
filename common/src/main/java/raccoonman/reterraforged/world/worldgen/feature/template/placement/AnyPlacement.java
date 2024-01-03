package raccoonman.reterraforged.world.worldgen.feature.template.placement;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import raccoonman.reterraforged.world.worldgen.feature.template.BlockUtils;
import raccoonman.reterraforged.world.worldgen.feature.template.template.Dimensions;
import raccoonman.reterraforged.world.worldgen.feature.template.template.NoopTemplateContext;

record AnyPlacement() implements TemplatePlacement<NoopTemplateContext> {
	public static final Codec<AnyPlacement> CODEC = Codec.unit(AnyPlacement::new);

	@Override
	public boolean canPlaceAt(LevelAccessor world, BlockPos pos, Dimensions dimensions) {
		return true;
	}

	@Override
	public boolean canReplaceAt(LevelAccessor world, BlockPos pos) {
		return !BlockUtils.isSolid(world, pos);
	}

	@Override
	public NoopTemplateContext createContext() {
		return new NoopTemplateContext();
	}

	@Override
	public Codec<AnyPlacement> codec() {
		return CODEC;
	}
}
