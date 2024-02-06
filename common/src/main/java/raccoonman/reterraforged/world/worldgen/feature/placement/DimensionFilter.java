package raccoonman.reterraforged.world.worldgen.feature.placement;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

class DimensionFilter extends PlacementFilter {
	public static final Codec<DimensionFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceKey.codec(Registries.LEVEL_STEM).listOf().fieldOf("blacklist").forGetter((filter) -> filter.blacklist)
	).apply(instance, DimensionFilter::new));
	
	private List<ResourceKey<LevelStem>> blacklist;
	private List<ResourceKey<Level>> levelKeys;
	
	public DimensionFilter(List<ResourceKey<LevelStem>> blacklist) {
		this.blacklist = blacklist;
		this.levelKeys = this.blacklist.stream().map(Registries::levelStemToLevel).toList();
	}
	
	@Override
	protected boolean shouldPlace(PlacementContext ctx, RandomSource rand, BlockPos pos) {
		WorldGenLevel level = ctx.getLevel();
		MinecraftServer server = level.getServer();
				
		for(ResourceKey<Level> key : this.levelKeys) {
			if(server.getLevel(key) == level.getLevel()) {
				return false;
			}
		}
		return true;
	}	

	@Override
	public PlacementModifierType<DimensionFilter> type() {
		return RTFPlacementModifiers.DIMENSION_FILTER;
	}
}
