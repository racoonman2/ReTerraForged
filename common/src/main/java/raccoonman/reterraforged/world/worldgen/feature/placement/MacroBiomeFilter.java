package raccoonman.reterraforged.world.worldgen.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.world.worldgen.cell.Cell;

class MacroBiomeFilter extends CellFilter {
	public static final Codec<MacroBiomeFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("chance").forGetter((filter) -> filter.chance)
	).apply(instance, MacroBiomeFilter::new));

	private float chance;
	
	public MacroBiomeFilter(float chance) {
		this.chance = chance;
	}
	
	@Override
	protected boolean shouldPlace(Cell cell, PlacementContext ctx, RandomSource rand, BlockPos pos) {
		return cell.macroBiomeId > (1.0F - this.chance);
	}
	
	@Override
	public PlacementModifierType<MacroBiomeFilter> type() {
		return RTFPlacementModifiers.MACRO_BIOME_FILTER;
	}
}
