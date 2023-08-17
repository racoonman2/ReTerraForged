package raccoonman.reterraforged.forge.data.provider;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.common.data.ExistingFileHelper;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.registries.data.RTFWorldPresets;

public class RTFWorldPresetTagsProvider extends TagsProvider<WorldPreset> {

	public RTFWorldPresetTagsProvider(PackOutput packOutput, CompletableFuture<Provider> completableFuture, ExistingFileHelper existingFileHelper) {
		super(packOutput, Registries.WORLD_PRESET, completableFuture, ReTerraForged.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(WorldPresetTags.NORMAL).add(RTFWorldPresets.RETERRAFORGED);
	}
}
