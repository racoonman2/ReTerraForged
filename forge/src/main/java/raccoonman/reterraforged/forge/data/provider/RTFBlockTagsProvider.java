package raccoonman.reterraforged.forge.data.provider;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.data.tags.RTFBlockTags;

public class RTFBlockTagsProvider extends TagsProvider<Block> {

	public RTFBlockTagsProvider(PackOutput packOutput, CompletableFuture<Provider> completableFuture, ExistingFileHelper existingFileHelper) {
		super(packOutput, Registries.BLOCK, completableFuture, ReTerraForged.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(RTFBlockTags.ERODIBLE).addTag(BlockTags.DIRT).addTag(BlockTags.SNOW);
	}
}
