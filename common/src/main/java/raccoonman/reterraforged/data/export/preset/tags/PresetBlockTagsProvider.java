package raccoonman.reterraforged.data.export.preset.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import raccoonman.reterraforged.tags.RTFBlockTags;

public class PresetBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {
	
	public PresetBlockTagsProvider(PackOutput packOutput, CompletableFuture<Provider> completableFuture) {
		super(packOutput, Registries.BLOCK, completableFuture, (block) -> block.builtInRegistryHolder().key());
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(RTFBlockTags.SOIL).add(Blocks.DIRT, Blocks.COARSE_DIRT);
		this.tag(RTFBlockTags.CLAY).add(Blocks.CLAY);
		this.tag(RTFBlockTags.SEDIMENT).add(Blocks.SAND, Blocks.GRAVEL);
		this.tag(RTFBlockTags.ERODIBLE).add(Blocks.SNOW_BLOCK).add(Blocks.POWDER_SNOW).add(Blocks.PACKED_ICE).add(Blocks.GRAVEL).addOptionalTag(BlockTags.DIRT.location());

		Block[] rocks = { Blocks.GRANITE, Blocks.ANDESITE, Blocks.STONE, Blocks.DIORITE };
		this.tag(RTFBlockTags.ROCK).add(rocks);
		this.tag(RTFBlockTags.ORE_COMPATIBLE_ROCK).add(rocks);
		this.tag(BlockTags.OVERWORLD_CARVER_REPLACEABLES).add(Blocks.CLAY);
	}
}