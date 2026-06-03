package raccoonman.reterraforged.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import raccoonman.reterraforged.RTFCommon;

public class RTFBlockTags {
	public static final TagKey<Block> SOIL = tag("soil");
	public static final TagKey<Block> SEDIMENT = tag("sediment");
	public static final TagKey<Block> CLAY = tag("clay");
	public static final TagKey<Block> ROCK = tag("rock");
	public static final TagKey<Block> ROCK_DEEP = tag("rock_deep");
	
	public static final TagKey<Block> BWG_ROCK = tag("bwg_rock");
	
    private static TagKey<Block> tag(String path) {
    	return TagKey.create(Registries.BLOCK, RTFCommon.location(path));
    }
}
