package raccoonman.reterraforged.common.worldgen.data.provider;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import raccoonman.reterraforged.common.worldgen.data.tags.RTFBlockTags;

public class RTFBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {

	public RTFBlockTagsProvider(PackOutput packOutput, CompletableFuture<Provider> completableFuture) {
		super(packOutput, Registries.BLOCK, completableFuture, (block) -> block.builtInRegistryHolder().key());
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(RTFBlockTags.ERODIBLE).add(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.COARSE_DIRT, Blocks.PODZOL);
		this.tag(RTFBlockTags.SOIL).add(Blocks.DIRT, Blocks.COARSE_DIRT);
		this.tag(RTFBlockTags.ROCK).add(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE);
		this.tag(RTFBlockTags.CLAY).add(Blocks.CLAY);
		this.tag(RTFBlockTags.SEDIMENT).add(Blocks.GRAVEL);
	}
}
