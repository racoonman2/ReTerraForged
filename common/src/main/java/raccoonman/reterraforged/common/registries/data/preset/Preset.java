package raccoonman.reterraforged.common.registries.data.preset;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.registries.data.RTFNoiseData;
import raccoonman.reterraforged.common.registries.data.RTFNoiseGeneratorSettings;
import raccoonman.reterraforged.common.registries.data.preset.settings.ClimateSettings;
import raccoonman.reterraforged.common.registries.data.preset.settings.FilterSettings;
import raccoonman.reterraforged.common.registries.data.preset.settings.RiverSettings;
import raccoonman.reterraforged.common.registries.data.preset.settings.TerrainSettings;
import raccoonman.reterraforged.common.registries.data.preset.settings.WorldSettings;

public record Preset(WorldSettings world, ClimateSettings climate, TerrainSettings terrain, RiverSettings rivers, FilterSettings filters) {
	public static final Codec<Preset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WorldSettings.CODEC.fieldOf("world").forGetter(Preset::world),
		ClimateSettings.CODEC.fieldOf("climate").forGetter(Preset::climate),
		TerrainSettings.CODEC.fieldOf("terrain").forGetter(Preset::terrain),
		RiverSettings.CODEC.fieldOf("rivers").forGetter(Preset::rivers),
		FilterSettings.CODEC.fieldOf("filters").forGetter(Preset::filters)		
	).apply(instance, Preset::new));
	
	public static final Preset DEFAULT = new Preset(WorldSettings.DEFAULT, ClimateSettings.DEFAULT, TerrainSettings.DEFAULT, RiverSettings.DEFAULT, FilterSettings.DEFAULT);
	
	//TODO cull unchanged registry entries
	public void exportAsDatapack(RegistryAccess.Frozen registries, Path outputPath) throws IOException {
		Path tempPath = Files.createTempDirectory("datapack-");
		DataGenerator generator = new DataGenerator(tempPath, SharedConstants.getCurrentVersion(), true);
		String packName = FilenameUtils.removeExtension(outputPath.getFileName().toString());
		Path tempPackPath = generator.vanillaPackOutput.getOutputFolder().resolve(packName);
		PackGenerator packGenerator = generator.new PackGenerator(true, packName, new PackOutput(tempPackPath));
        CompletableFuture<HolderLookup.Provider> future = CompletableFuture.supplyAsync(() -> {
        	return this.createLookup(registries);	
        }, Util.backgroundExecutor());
		packGenerator.addProvider((packOutput) -> {
			return new RegistriesDatapackGenerator(packOutput, future);
		});
		packGenerator.addProvider((packOutput) -> {
			return PackMetadataGenerator.forFeaturePack(packOutput, Component.translatable(RTFTranslationKeys.PRESET_METADATA_DESCRIPTION));
		});
		generator.run();
		zippedCopy(tempPackPath, outputPath);
		PathUtils.deleteDirectory(tempPath);
	}
	
	public HolderLookup.Provider createLookup(RegistryAccess.Frozen registries) {
		RegistrySetBuilder builder = new RegistrySetBuilder();
		builder.add(RTFRegistries.NOISE, (ctx) -> RTFNoiseData.bootstrap(ctx, this));
		builder.add(Registries.NOISE_SETTINGS, (ctx) -> RTFNoiseGeneratorSettings.bootstrap(ctx, this));
		HolderLookup.Provider provider = builder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), registries);
		return provider;
	}
	
	private static void zippedCopy(Path input, Path output) {
		Map<String, String> env = ImmutableMap.of("create", "true");
	    URI uri = URI.create("jar:" + output.toUri());
	    try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
	        PathUtils.copyDirectory(input, fs.getPath("/"), StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
