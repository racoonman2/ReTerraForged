package raccoonman.reterraforged.common.data.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import raccoonman.reterraforged.common.ReTerraForged;

public final class RTFBlockTags {
	public static final TagKey<Block> ERODIBLE = resolve("erodible");
	
    private static TagKey<Block> resolve(String path) {
    	return TagKey.create(Registries.BLOCK, ReTerraForged.resolve(path));
    }
}
