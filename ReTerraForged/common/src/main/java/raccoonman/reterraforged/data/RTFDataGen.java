package raccoonman.reterraforged.data;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.io.file.PathUtils;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;

import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.preset.PresetFileProvider;
import raccoonman.reterraforged.data.preset.registry.RTFConfiguredCarvers;
import raccoonman.reterraforged.data.preset.registry.RTFDimensionTypes;
import raccoonman.reterraforged.data.preset.registry.RTFDimensions;
import raccoonman.reterraforged.data.preset.registry.RTFLayers;
import raccoonman.reterraforged.data.preset.registry.RTFModifiers;
import raccoonman.reterraforged.data.preset.registry.RTFNoiseGeneratorSettings;
import raccoonman.reterraforged.data.preset.registry.RTFNoiseRouterData;
import raccoonman.reterraforged.data.preset.registry.RTFNoises;
import raccoonman.reterraforged.data.preset.registry.RTFWarps;
import raccoonman.reterraforged.platform.ConfigUtil;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.pages.StructureOptions;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.registry.RegistryPatcher;

public class RTFDataGen {
	public static final Path EXPORT_PATH = ConfigUtil.RTF_CONFIG_PATH.resolve("exports");
	
	public static void exportPack(Path path, Consumer<Function<String, DataGenerator.PackGenerator>> generator) throws IOException {
		Files.deleteIfExists(path);
		
		Path tempPath = Files.createTempDirectory("gen-");
		Path outputPath = tempPath.resolve("output");
		
		DataGenerator dataGenerator = new DataGenerator(tempPath, SharedConstants.getCurrentVersion(), true);
		PackOutput output = new PackOutput(outputPath);
		generator.accept((name) -> dataGenerator.new PackGenerator(true, name, output));

		dataGenerator.run();
		copyToZip(outputPath, path);
		PathUtils.deleteDirectory(tempPath);
		
		RTFCommon.LOGGER.info("Exported pack to {}", path);
	}
	
	public static void generateResourcePack(DataGenerator.PackGenerator pack) {
		pack.addProvider(LanguageProvider.EnglishUS::new);

		addMetadata(pack, RTFTranslationKeys.RESOURCE_PACK_METADATA_DESCRIPTION);
	}
	
	public static void generateDataPack(Path path, String presetName, Preset preset, Codec<Preset> presetCodec, StructureOptions.Placements structurePlacements, CompletableFuture<HolderLookup.Provider> registries) throws IOException {
		exportPack(path, (factory) -> {
			generateData(factory.apply(presetName), presetName, preset, presetCodec, structurePlacements, registries);
		});
	}
	
	public static void generateData(DataGenerator.PackGenerator pack, String presetFileName, Preset preset, Codec<Preset> presetCodec, StructureOptions.Placements structurePlacements,CompletableFuture<HolderLookup.Provider> lookupProvider) {
		CompletableFuture<RegistryPatcher.Result> patchedRegistries = lookupProvider.thenApply((provider) -> buildPatch(preset, provider, structurePlacements));
		CompletableFuture<HolderLookup.Provider> fullProvider = patchedRegistries.thenApply(RegistryPatcher.Result::full);
		CompletableFuture<List<HolderLookup.Provider>> patchProviders = patchedRegistries.thenApply(RegistryPatcher.Result::patches);
		
		pack.addProvider((output) -> new PresetFileProvider(output, presetFileName, preset, presetCodec));
		pack.addProvider((output) -> new RegistryDataProvider(output, patchProviders));
//		pack.addProvider((output) -> new RTFBlockTagsProvider(output, fullProvider));
//		pack.addProvider((output) -> new RTFBiomeTagsProvider(preset, output, fullProvider));
//		pack.addProvider((output) -> new RTFParameterSliceTagsProvider(output, fullProvider));
		
		addMetadata(pack, RTFTranslationKeys.DATA_PACK_METADATA_DESCRIPTION);
	}
	
	public static RegistryPatcher.Result buildPatch(Preset preset, HolderLookup.Provider lookupProvider, StructureOptions.Placements structurePlacements) {
		return RegistryPatcher.of(lookupProvider, (builder, v) -> {
//			builder.add(RTFRegistries.PARAMETER_SLICE, RTFParameterSlices::bootstrap);
			builder.add(RTFRegistries.LAYER, (ctx) -> RTFLayers.bootstrap(preset, ctx));
			builder.add(RTFRegistries.NOISE, (ctx) -> RTFNoises.bootstrap(preset, ctx));
			builder.add(RTFRegistries.WARP, (ctx) -> RTFWarps.bootstrap(preset, ctx));
			builder.add(Registries.NOISE_SETTINGS, (ctx) -> RTFNoiseGeneratorSettings.bootstrap(preset, ctx, lookupProvider));
//			builder.add(Registries.BIOME, (ctx) -> RTFBiomes.bootstrap(preset, ctx));
//			builder.add(Registries.CONFIGURED_FEATURE, (ctx) -> RTFConfiguredFeatures.bootstrap(preset, ctx));
//			builder.add(Registries.PLACED_FEATURE, (ctx) -> RTFPlacedFeatures.bootstrap(preset, ctx));
			builder.add(Registries.CONFIGURED_CARVER, (ctx) -> RTFConfiguredCarvers.bootstrap(preset, ctx));
			builder.add(Registries.DENSITY_FUNCTION, (ctx) -> RTFNoiseRouterData.bootstrap(preset, ctx));
			builder.add(Registries.DIMENSION_TYPE, (ctx) -> RTFDimensionTypes.bootstrap(preset, ctx));
			builder.add(Registries.LEVEL_STEM, (ctx) -> RTFDimensions.bootstrap(preset, ctx));
			builder.add(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, (ctx) -> RTFModifiers.bootstrap(preset, ctx, structurePlacements));
		}).build();
	}

	public static void addMetadata(PackGenerator packGenerator, String description) {
		packGenerator.addProvider((output) -> {
			return PackMetadataGenerator.forFeaturePack(output, Component.translatable(description));
		});
	}
	
	public static RandomSource forkSeed(RandomSource source, Preset preset, Option<String> seedOption) {
		String seed = preset.getOption(seedOption);
		return seed.isEmpty() ? source : source.forkPositional().fromHashOf(seed);
	}

	private static void copyToZip(Path input, Path output) {
		Map<String, String> env = ImmutableMap.of("create", "true");
	    URI uri = URI.create("jar:" + output.toUri());
	    try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
	        PathUtils.copyDirectory(input, fs.getPath("/"), StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}