package raccoonman.reterraforged.client.gui.preset;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;

import net.minecraft.Util;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.DataPackReloadCookie;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import raccoonman.reterraforged.RTFCommon;

public class DataPackLoader {

	public static void tryApplyDataPacks(WorldCreationUiState uiState, PackRepository repository) {
        ImmutableList<String> selectedPacks = ImmutableList.copyOf(repository.getSelectedIds());
        WorldDataConfiguration dataConfiguration = new WorldDataConfiguration(new DataPackConfig(selectedPacks, repository.getAvailableIds().stream().filter(pack -> !selectedPacks.contains(pack)).collect(ImmutableList.toImmutableList())), uiState.getSettings().dataConfiguration().enabledFeatures());
        applyNewPackConfig(uiState, repository, dataConfiguration);
	}
	
	public static void applyNewPackConfig(WorldCreationUiState uiState, PackRepository packRepository, WorldDataConfiguration dataConfiguration) {
		WorldLoader.InitConfig config = CreateWorldScreen.createDefaultLoadConfig(packRepository, dataConfiguration);
		WorldLoader.load(config, dataLoadContext -> {
			if (dataLoadContext.datapackWorldgen().lookupOrThrow(Registries.WORLD_PRESET).listElements().findAny().isEmpty()) {
				throw new IllegalStateException("Needs at least one world preset to continue");
			} else if (dataLoadContext.datapackWorldgen().lookupOrThrow(Registries.BIOME).listElements().findAny().isEmpty()) {
				throw new IllegalStateException("Needs at least one biome continue");
			} else {
				WorldCreationContext worldCreationContext = uiState.getSettings();
				DynamicOps<JsonElement> dynamicOps = worldCreationContext.worldgenLoadContext().createSerializationContext(JsonOps.INSTANCE);
				DataResult<JsonElement> dataResult = WorldGenSettings.encode(dynamicOps, worldCreationContext.options(), worldCreationContext.selectedDimensions())
					.setLifecycle(Lifecycle.stable());
				DynamicOps<JsonElement> dynamicOps2 = dataLoadContext.datapackWorldgen().createSerializationContext(JsonOps.INSTANCE);
				WorldGenSettings worldGenSettings = dataResult.<WorldGenSettings>flatMap(jsonElement -> WorldGenSettings.CODEC.parse(dynamicOps2, jsonElement))
					.getOrThrow(string -> new IllegalStateException("Error parsing worldgen settings after loading data packs: " + string));
				return new WorldLoader.DataLoadOutput<>(
					new DataPackReloadCookie(worldGenSettings, dataLoadContext.dataConfiguration()), dataLoadContext.datapackDimensions()
				);
			}
		}, (resourceManager, serverResources, registryAccess, cookie) -> {
			resourceManager.close();
			return new WorldCreationContext(cookie.worldGenSettings(), registryAccess, serverResources, cookie.dataConfiguration());
		}, Util.backgroundExecutor(), Runnable::run).thenAcceptAsync(uiState::setSettings, Runnable::run).handle((v, throwable) -> {
			if (throwable != null) {
				RTFCommon.LOGGER.warn("Failed to validate datapack", throwable);
	        } else {
	        	RTFCommon.LOGGER.info("Validated datapack");
	        }
			return null;
		}).join();
	}
	
}
