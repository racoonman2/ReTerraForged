package raccoonman.reterraforged.fabric;

import java.nio.file.Path;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.RTFDataGen;
import raccoonman.reterraforged.fabric.mixin.MixinFabricDataGenerator$Pack;

public class RTFFabric implements ModInitializer, DataGeneratorEntrypoint {

	@Override
	public void onInitialize() {
		RTFCommon.bootstrap();
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		RTFDataGen.generateResourcePacks(fabricDataGenerator::createPack);
		RTFDataGen.generateDataPacks((id) -> {
			Path path = fabricDataGenerator.vanillaPackOutput.getOutputFolder().resolve(RTFDataGen.DATAPACK_PATH).resolve(id.getPath());
			return MixinFabricDataGenerator$Pack.invokeNew(fabricDataGenerator, true, id.toString(), new FabricDataOutput(fabricDataGenerator.getModContainer(), path, fabricDataGenerator.isStrictValidationEnabled()));
		});
	}
}