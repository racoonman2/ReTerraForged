package raccoonman.reterraforged.data.worldgen.preset.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.data.worldgen.preset.PresetNoiseRouterData;
import raccoonman.reterraforged.integration.terrablender.TBNoiseRouterData;
import raccoonman.reterraforged.tags.RTFDensityFunctionTags;

public class PresetDensityFunctionTagsProvider extends TagsProvider<DensityFunction> {

	public PresetDensityFunctionTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(packOutput, Registries.DENSITY_FUNCTION, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(RTFDensityFunctionTags.ADDITIONAL_NOISE_ROUTER_FUNCTIONS).add(PresetNoiseRouterData.GRADIENT, PresetNoiseRouterData.HEIGHT_EROSION, PresetNoiseRouterData.SEDIMENT, TBNoiseRouterData.UNIQUENESS);
	}
}
