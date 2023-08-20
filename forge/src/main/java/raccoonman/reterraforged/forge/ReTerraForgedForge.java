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
import net.minecraftforge.client.event.RegisterPresetEditorsEvent;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.client.gui.preview.WorldPreviewScreen;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.registries.data.RTFClimatePresets;
import raccoonman.reterraforged.common.registries.data.RTFClimates;
import raccoonman.reterraforged.common.registries.data.RTFDensityFunctions;
import raccoonman.reterraforged.common.registries.data.RTFDimensionTypes;
import raccoonman.reterraforged.common.registries.data.RTFNoise;
import raccoonman.reterraforged.common.registries.data.RTFNoiseGeneratorSettings;
import raccoonman.reterraforged.common.registries.data.RTFPlacedFeatures;
import raccoonman.reterraforged.common.registries.data.RTFWorldPresets;
import raccoonman.reterraforged.forge.data.provider.RTFBlockTagsProvider;
import raccoonman.reterraforged.forge.data.provider.RTFClimateTagsProvider;
import raccoonman.reterraforged.forge.data.provider.RTFLangProvider;
import raccoonman.reterraforged.forge.data.provider.RTFWorldPresetTagsProvider;
import raccoonman.reterraforged.platform.registries.forge.RegistryUtilImpl;

@Mod(ReTerraForged.MOD_ID)
public final class ReTerraForgedForge {

    public ReTerraForgedForge() {
    	ReTerraForged.bootstrap();
    	
    	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    	modBus.addListener(ReTerraForgedForge::gatherData);
    	modBus.addListener((RegisterPresetEditorsEvent event) -> {
    		event.register(RTFWorldPresets.RETERRAFORGED, (parent, ctx) -> {
    			return new WorldPreviewScreen((int) ctx.options().seed(), parent, ctx.worldgenLoadContext().lookupOrThrow(RTFRegistries.NOISE));
    		});
    	});
    	
    	RegistryUtilImpl.register(modBus);
    }
    
    private static void gatherData(GatherDataEvent event) {
    	RegistrySetBuilder builder = new RegistrySetBuilder();
    	builder.add(RTFRegistries.NOISE, RTFNoise::register);
    	builder.add(RTFRegistries.CLIMATE, RTFClimates::register);
    	builder.add(RTFRegistries.CLIMATE_PRESET, RTFClimatePresets::register);
    	builder.add(Registries.NOISE_SETTINGS, RTFNoiseGeneratorSettings::register);
    	builder.add(Registries.DIMENSION_TYPE, RTFDimensionTypes::register);
    	builder.add(Registries.WORLD_PRESET, RTFWorldPresets::register);
    	builder.add(Registries.DENSITY_FUNCTION, RTFDensityFunctions::register);
    	builder.add(Registries.PLACED_FEATURE, RTFPlacedFeatures::register);
        	
    	boolean includeClient = event.includeClient();
    	boolean includeServer = event.includeServer();
    	CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider().thenApply(r -> constructRegistries(r, builder));
    	ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    	DataGenerator generator = event.getGenerator();
    	PackOutput output = generator.getPackOutput();

    	generator.addProvider(includeClient, new RTFLangProvider.EnglishUS(output, ReTerraForged.MOD_ID));
    	generator.addProvider(includeServer, new DatapackBuiltinEntriesProvider(output, lookupProvider, ImmutableSet.of(ReTerraForged.MOD_ID, "minecraft")));
    	generator.addProvider(includeServer, new RTFBlockTagsProvider(output, lookupProvider, existingFileHelper));
    	generator.addProvider(includeServer, new RTFClimateTagsProvider(output, lookupProvider, existingFileHelper));
    	generator.addProvider(includeServer, new RTFWorldPresetTagsProvider(output, lookupProvider, existingFileHelper));
    }
    
    private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, RegistrySetBuilder datapackEntriesBuilder) {
        var builderKeys = new HashSet<>(datapackEntriesBuilder.getEntryKeys());
        DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().filter(data -> !builderKeys.contains(data.key())).forEach(data -> datapackEntriesBuilder.add(data.key(), context -> {}));
        return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
    }
}