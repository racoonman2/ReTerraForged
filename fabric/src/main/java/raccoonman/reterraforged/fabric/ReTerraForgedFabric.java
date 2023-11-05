package raccoonman.reterraforged.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFLanguageProvider;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.common.ReTerraForged;

public final class ReTerraForgedFabric implements ModInitializer, DataGeneratorEntrypoint {

	@Override
	public void onInitialize() {
		ReTerraForged.bootstrap();
	}

	//TODO merge this with forge's datagen since they're the same now
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		Pack pack = fabricDataGenerator.createPack();
		pack.addProvider((FabricDataOutput output) -> new RTFLanguageProvider.EnglishUS(output));
		pack.addProvider((FabricDataOutput output) -> PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.METADATA_DESCRIPTION)));
	}
}