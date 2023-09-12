package raccoonman.reterraforged.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import raccoonman.reterraforged.common.ReTerraForged;

public final class ReTerraForgedFabric implements ModInitializer, DataGeneratorEntrypoint {

	@Override
	public void onInitialize() {
		ReTerraForged.bootstrap();
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		//TODO 
	}
}