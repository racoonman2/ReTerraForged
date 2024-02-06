package raccoonman.reterraforged.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.RTFDataGen;

public class RTFFabric implements ModInitializer, DataGeneratorEntrypoint {

	@Override
	public void onInitialize() {
		RTFCommon.bootstrap();
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		RTFDataGen.generateResourcePacks(fabricDataGenerator::createPack);
	}
}