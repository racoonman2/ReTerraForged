package raccoonman.reterraforged.data.preset.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import raccoonman.reterraforged.data.preset.PresetSurfaceLayerData;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.tags.RTFSurfaceLayerTags;
import raccoonman.reterraforged.world.worldgen.surface.rule.LayeredSurfaceRule;

public class PresetSurfaceLayerTags extends TagsProvider<LayeredSurfaceRule.Layer> {
	private Preset preset;
	
	public PresetSurfaceLayerTags(Preset preset, PackOutput packOutput, CompletableFuture<Provider> completableFuture) {
		super(packOutput, RTFRegistries.SURFACE_LAYERS, completableFuture);
		
		this.preset = preset;
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(RTFSurfaceLayerTags.ON_FLOOR).add(PresetSurfaceLayerData.EROSION, PresetSurfaceLayerData.DIRT_EROSION);
		this.tag(RTFSurfaceLayerTags.UNDER_FLOOR).add(PresetSurfaceLayerData.EROSION, PresetSurfaceLayerData.DIRT_EROSION);
		this.tag(RTFSurfaceLayerTags.EROSION).add(PresetSurfaceLayerData.ROCK_EROSION, PresetSurfaceLayerData.BADLANDS_EROSION);
		this.tag(RTFSurfaceLayerTags.EROSION_VARIANT).add(PresetSurfaceLayerData.ROCK_EROSION, PresetSurfaceLayerData.BADLANDS_EROSION);
	}
}