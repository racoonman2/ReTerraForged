package raccoonman.reterraforged.forge.data.provider;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.registries.data.RTFClimates;
import raccoonman.reterraforged.common.registries.data.tags.RTFClimateTags;

public class RTFClimateTagsProvider extends TagsProvider<Climate> {

	public RTFClimateTagsProvider(PackOutput packOutput, CompletableFuture<Provider> completableFuture, ExistingFileHelper existingFileHelper) {
		super(packOutput, RTFRegistries.CLIMATE, completableFuture, ReTerraForged.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(RTFClimateTags.TEMPERATE).add(RTFClimates.TEMPERATE);
		this.tag(RTFClimateTags.OCEAN).add(RTFClimates.OCEAN);
		this.tag(RTFClimateTags.DEEP).add(RTFClimates.DEEP);
	}
}
