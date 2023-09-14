package raccoonman.reterraforged.forge;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.client.event.RegisterPresetEditorsEvent;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.data.provider.RTFLanguageProvider;
import raccoonman.reterraforged.client.gui.screen.presetconfig.PresetConfigScreen;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.MCDimensionTypes;
import raccoonman.reterraforged.common.worldgen.data.MCNoiseGeneratorSettings;
import raccoonman.reterraforged.common.worldgen.data.MCPlacedFeatures;
import raccoonman.reterraforged.common.worldgen.data.RTFBiomes;
import raccoonman.reterraforged.common.worldgen.data.RTFNoiseData;
import raccoonman.reterraforged.common.worldgen.data.RTFNoiseRouterData;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.platform.registries.forge.RegistryUtilImpl;

@Mod(ReTerraForged.MOD_ID)
public final class ReTerraForgedForge {

    public ReTerraForgedForge() {
    	ReTerraForged.bootstrap();
    	
    	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    	modBus.addListener(ReTerraForgedForge::registerPresetEditors);
    	modBus.addListener(ReTerraForgedForge::gatherData);
    	
    	RegistryUtilImpl.register(modBus);
    }
    
    private static void registerPresetEditors(RegisterPresetEditorsEvent event) {
    	// TODO we probably shouldn't register this for the default preset
    	event.register(WorldPresets.NORMAL, (screen, ctx) -> new PresetConfigScreen(screen));
    }

    private static void gatherData(GatherDataEvent event) {
    	Preset preset = Preset.makeDefault();
    	
    	RegistrySetBuilder builder = new RegistrySetBuilder();
    	builder.add(RTFRegistries.NOISE, (ctx) -> RTFNoiseData.bootstrap(ctx, preset));
    	builder.add(Registries.BIOME, RTFBiomes::bootstrap);
    	builder.add(Registries.DENSITY_FUNCTION, RTFNoiseRouterData::bootstrap);
    	builder.add(Registries.NOISE_SETTINGS, (ctx) -> MCNoiseGeneratorSettings.bootstrap(ctx, preset));
    	builder.add(Registries.DIMENSION_TYPE, (ctx) -> MCDimensionTypes.bootstrap(ctx, preset));
    	builder.add(Registries.PLACED_FEATURE, MCPlacedFeatures::bootstrap);

    	boolean includeClient = event.includeClient();
    	boolean includeServer = event.includeServer();
    	CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider().thenApply(r -> constructRegistries(r, builder));
    	ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    	DataGenerator generator = event.getGenerator();
    	PackOutput output = generator.getPackOutput();

    	generator.addProvider(includeClient, new RTFLanguageProvider.EnglishUS(output));
    	generator.addProvider(includeClient, PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.METADATA_DESCRIPTION)));
    	generator.addProvider(includeServer, new DatapackBuiltinEntriesProvider(output, lookupProvider, ImmutableSet.of(ReTerraForged.MOD_ID, "minecraft")));
//    	generator.addProvider(includeServer, new RTFBlockTagsProvider(output, lookupProvider, existingFileHelper));
    }
    
    private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, RegistrySetBuilder datapackEntriesBuilder) {
        var builderKeys = new HashSet<>(datapackEntriesBuilder.getEntryKeys());
        DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().filter(data -> !builderKeys.contains(data.key())).forEach(data -> datapackEntriesBuilder.add(data.key(), context -> {}));
        return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
    }
}