package raccoonman.reterraforged.fabric;

import net.fabricmc.api.ModInitializer;
import raccoonman.reterraforged.common.ReTerraForged;

public final class ReTerraForgedFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		ReTerraForged.bootstrap();
    }
}