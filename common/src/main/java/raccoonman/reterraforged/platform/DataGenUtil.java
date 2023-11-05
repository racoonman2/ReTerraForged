package raccoonman.reterraforged.platform;

import java.util.concurrent.CompletableFuture;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public final class DataGenUtil {

	@ExpectPlatform
	public static DataProvider createRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerLookup) {
		throw new UnsupportedOperationException("Missing platform implementation");
	}
}
