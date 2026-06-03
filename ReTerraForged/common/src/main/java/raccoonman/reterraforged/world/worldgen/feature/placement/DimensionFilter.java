package raccoonman.reterraforged.world.worldgen.feature.placement;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.registry.RegistryFilter;

class DimensionFilter extends PlacementFilter {
	public static final MapCodec<DimensionFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegistryFilter.codec(ResourceKey.codec(Registries.DIMENSION).listOf()).fieldOf("filter").forGetter((f) -> f.filter)
	).apply(instance, DimensionFilter::new));
	
	private RegistryFilter<ResourceKey<Level>, List<ResourceKey<Level>>> filter;
	
	public DimensionFilter(RegistryFilter<ResourceKey<Level>, List<ResourceKey<Level>>> filter) {
		this.filter = filter;
	}
	
	@Override
	protected boolean shouldPlace(PlacementContext ctx, RandomSource rand, BlockPos pos) {
		WorldGenLevel level = ctx.getLevel();
		ResourceKey<Level> dimension = level.getLevel().dimension();
		return this.filter.test(dimension);
	}

	@Override
	public PlacementModifierType<DimensionFilter> type() {
		return RTFPlacementModifiers.DIMENSION_FILTER;
	}
}
