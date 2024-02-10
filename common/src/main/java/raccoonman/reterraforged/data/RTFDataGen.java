package raccoonman.reterraforged.data;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.client.data.RTFLanguageProvider;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.data.preset.tags.PresetBiomeTagsProvider;
import raccoonman.reterraforged.data.preset.tags.PresetBlockTagsProvider;
import raccoonman.reterraforged.data.preset.tags.PresetSurfaceLayerProvider;
import raccoonman.reterraforged.platform.DataGenUtil;

public class RTFDataGen {
	public static final String DATAPACK_PATH = "data/reterraforged/datapacks";
	
	public static void generateResourcePacks(ResourcePackFactory resourcePackFactory) {
		DataGenerator.PackGenerator pack = resourcePackFactory.createPack();

		pack.addProvider(RTFLanguageProvider.EnglishUS::new);
		pack.addProvider((PackOutput output) -> PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.METADATA_DESCRIPTION)));
	}
	
	@Deprecated
	public static DataGenerator makePreset(Preset preset, RegistryAccess registryAccess, Path dataGenPath, Path dataGenOutputPath) {
		DataGenerator dataGenerator = new DataGenerator(dataGenPath, SharedConstants.getCurrentVersion(), true);
		PackGenerator packGenerator = dataGenerator.new PackGenerator(true, "preset", new PackOutput(dataGenOutputPath));
		CompletableFuture<HolderLookup.Provider> lookup = CompletableFuture.supplyAsync(() -> preset.buildPatch(registryAccess));
		
		packGenerator.addProvider((output) -> {
			return DataGenUtil.createRegistryProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return new PresetBlockTagsProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return new PresetSurfaceLayerProvider(preset, output, lookup);
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
