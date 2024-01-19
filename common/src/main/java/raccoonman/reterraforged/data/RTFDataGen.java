package raccoonman.reterraforged.data;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.client.data.RTFLanguageProvider;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.data.worldgen.preset.PresetConfiguredFeatures;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.data.worldgen.preset.tags.PresetBiomeTagsProvider;
import raccoonman.reterraforged.data.worldgen.preset.tags.PresetBlockTagsProvider;
import raccoonman.reterraforged.data.worldgen.preset.tags.PresetDensityFunctionTagsProvider;
import raccoonman.reterraforged.platform.DataGenUtil;
import raccoonman.reterraforged.world.worldgen.feature.RTFFeatures;
import raccoonman.reterraforged.world.worldgen.feature.SwampSurfaceFeature;

public class RTFDataGen {
	public static final String DATAPACK_PATH = "data/reterraforged/datapacks";
	
	public static void generateResourcePacks(ResourcePackFactory resourcePackFactory) {
		DataGenerator.PackGenerator pack = resourcePackFactory.createPack();

		pack.addProvider(RTFLanguageProvider.EnglishUS::new);
		pack.addProvider((PackOutput output) -> PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.METADATA_DESCRIPTION)));
	}

	public static void generateDataPacks(DataPackFactory builtInPackFactory) {
//		DataGenerator.PackGenerator defaultPack = builtInPackFactory.createPack(RTFCommon.location("default"));
//		defaultPack.addProvider((PackOutput output) -> PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.METADATA_DESCRIPTION)));
//		
		generateMudSwamps(builtInPackFactory.createPack(RTFCommon.location("mud_swamps")));
	}
	
	private static void generateMudSwamps(DataGenerator.PackGenerator generator) {
		HolderLookup.Provider lookupProvider = VanillaRegistries.createLookup();
		CompletableFuture<HolderLookup.Provider> lookup = CompletableFuture.supplyAsync(() -> {
			RegistrySetBuilder builder = new RegistrySetBuilder();
			builder.add(Registries.CONFIGURED_FEATURE, (ctx) -> {
				FeatureUtils.register(ctx, PresetConfiguredFeatures.SWAMP_SURFACE, RTFFeatures.SWAMP_SURFACE, new SwampSurfaceFeature.Config(Blocks.CLAY.defaultBlockState(), Blocks.MUD.defaultBlockState(), Blocks.MUD.defaultBlockState()));
			});
			return builder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), lookupProvider);
		});
		generator.addProvider((output) -> {
			return DataGenUtil.createRegistryProvider(output, lookup);
		});
		generator.addProvider((output) -> {
			return PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.MUD_SWAMPS_METADATA_DESCRIPTION));
		});
	}
	
	@Deprecated
	public static DataGenerator makePreset(Preset preset, RegistryAccess registryAccess, Path dataGenPath, Path dataGenOutputPath, String presetName) {
		DataGenerator dataGenerator = new DataGenerator(dataGenPath, SharedConstants.getCurrentVersion(), true);
		PackGenerator packGenerator = dataGenerator.new PackGenerator(true, presetName, new PackOutput(dataGenOutputPath));
		CompletableFuture<HolderLookup.Provider> lookup = CompletableFuture.supplyAsync(() -> preset.buildPatch(registryAccess));
		
		packGenerator.addProvider((output) -> {
			return DataGenUtil.createRegistryProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return new PresetDensityFunctionTagsProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return new PresetBlockTagsProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return new PresetBiomeTagsProvider(preset, output, CompletableFuture.completedFuture(registryAccess));
		});
		packGenerator.addProvider((output) -> {
			return PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.PRESET_METADATA_DESCRIPTION));
		});
		return dataGenerator;
	}
	
	public interface ResourcePackFactory {
		DataGenerator.PackGenerator createPack();
	}
	
	public interface DataPackFactory {
		DataGenerator.PackGenerator createPack(ResourceLocation id);
	}
}
