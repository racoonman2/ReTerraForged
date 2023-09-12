package raccoonman.reterraforged.fabric;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup.Provider;

public class WorldGenProvider extends FabricDynamicRegistryProvider {

	public WorldGenProvider(FabricDataOutput output, CompletableFuture<Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public String getName() {
		return "World Gen Provider";
	}

	@Override
	protected void configure(Provider registries, Entries entries) {

	}
}
