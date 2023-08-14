package raccoonman.reterraforged.forge.data.provider;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateGroup;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.registries.data.RTFClimateGroups;
import raccoonman.reterraforged.common.registries.data.tags.RTFClimateGroupTags;

public class RTFClimateGroupTagsProvider extends TagsProvider<ClimateGroup> {

	public RTFClimateGroupTagsProvider(PackOutput packOutput, CompletableFuture<Provider> completableFuture, ExistingFileHelper existingFileHelper) {
		super(packOutput, RTFRegistries.CLIMATE_GROUP, completableFuture, ReTerraForged.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(RTFClimateGroupTags.DEFAULT).add(RTFClimateGroups.DEFAULT);
	}
}
