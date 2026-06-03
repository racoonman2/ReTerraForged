package raccoonman.reterraforged.world.worldgen.feature.template.paste;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateContext;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacement;

public interface Paste {
    <T extends TemplateContext>	boolean paste(LevelAccessor world, T ctx, BlockPos origin, Mirror mirror, Rotation rotation, TemplatePlacement<T> placement, PasteConfig config);
}
