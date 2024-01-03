package raccoonman.reterraforged.data.worldgen.tags;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.tags.RTFDensityFunctionTags;
import raccoonman.reterraforged.world.worldgen.terrablender.TBCompat;

public class RTFDensityFunctionTagsProvider extends TagsProvider<DensityFunction> {

	public RTFDensityFunctionTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(packOutput, Registries.DENSITY_FUNCTION, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
//		this.tag(RTFDensityFunctionTags.ADDITIONAL_NOISE_ROUTER_FUNCTIONS).add(TBCompat.UNIQUENESS);
	}
}
