package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.file.PathUtils;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.levelgen.WorldOptions;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.worldgen.data.provider.RTFBlockTagsProvider;

//FIXME pressing the create world screen before the pack is copied will fuck the game up
public class PresetConfigScreen extends LinkedPageScreen {
	private CreateWorldScreen parent;
	
	public PresetConfigScreen(CreateWorldScreen parent) {
		this.parent = parent;
		this.currentPage = new SelectPresetPage(this);
	}
	
	@Override
	public void onClose() {
		super.onClose();

		this.minecraft.setScreen(this.parent);
	}
	
	public void setSeed(long seed) {
		this.parent.getUiState().setSettings(this.getSettings().withOptions((options) -> {
			return new WorldOptions(seed, options.generateStructures(), options.generateBonusChest());
		}));
	}
	
	public WorldCreationContext getSettings() {
		return this.parent.getUiState().getSettings();
	}

	public void applyPreset(PresetEntry preset) throws IOException {		
		Pair<Path, PackRepository> path = this.parent.getDataPackSelectionSettings(this.parent.getUiState().getSettings().dataConfiguration());
		Path exportPath = path.getFirst().resolve("reterraforged-preset.zip");
		this.exportAsDatapack(exportPath, preset);
		PackRepository repository = path.getSecond();
		repository.reload();
		if(repository.addPack("file/" + exportPath.getFileName())) {
			this.parent.tryApplyNewDataPacks(repository, false, (data) -> {
			});
		}
	}
	
	public void exportAsDatapack(Path outputPath, PresetEntry preset) throws IOException {
		Path datagenPath = Files.createTempDirectory("datagen-target-");
		Path datagenOutputPath = datagenPath.resolve("output");
		
		DataGenerator dataGenerator = new DataGenerator(datagenPath, SharedConstants.getCurrentVersion(), true);
		PackGenerator packGenerator = dataGenerator.new PackGenerator(true, preset.getName().getString(), new PackOutput(datagenOutputPath));
		CompletableFuture<HolderLookup.Provider> lookup = CompletableFuture.supplyAsync(() -> preset.getPreset().buildPatch(this.getSettings().worldgenLoadContext()));
		packGenerator.addProvider((output) -> {
			return new RegistriesDatapackGenerator(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return new RTFBlockTagsProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.PRESET_METADATA_DESCRIPTION));
		});		
		dataGenerator.run();
		copyToZip(datagenOutputPath, outputPath);
		PathUtils.deleteDirectory(datagenPath);
		
		ReTerraForged.LOGGER.info("Exported datapack to {}", outputPath);
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
