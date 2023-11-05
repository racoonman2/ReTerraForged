package raccoonman.reterraforged.forge;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import raccoonman.reterraforged.client.data.RTFLanguageProvider;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.platform.forge.RegistryUtilImpl;

@Mod(ReTerraForged.MOD_ID)
public final class ReTerraForgedForge {

    public ReTerraForgedForge() {
    	ReTerraForged.bootstrap();
    	
    	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    	
    	if (FMLEnvironment.dist == Dist.CLIENT) {
    		modBus.addListener(ReTerraForgedForgeClient::registerPresetEditors);
    	}
    	modBus.addListener(ReTerraForgedForge::gatherData);
    	
    	RegistryUtilImpl.register(modBus);
    }

    private static void gatherData(GatherDataEvent event) {
    	boolean includeClient = event.includeClient();
    	DataGenerator generator = event.getGenerator();
    	PackOutput output = generator.getPackOutput();

    	generator.addProvider(includeClient, new RTFLanguageProvider.EnglishUS(output));
    	generator.addProvider(includeClient, PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.METADATA_DESCRIPTION)));
    }
}