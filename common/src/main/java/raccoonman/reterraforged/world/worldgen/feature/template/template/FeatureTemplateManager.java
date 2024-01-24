package raccoonman.reterraforged.world.worldgen.feature.template.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;

public class FeatureTemplateManager {
	private MinecraftServer server;
	private ResourceManager resourceManager;
	private Map<ResourceLocation, FeatureTemplate> cache;
	
	public FeatureTemplateManager(MinecraftServer server, ResourceManager resourceManager) {
		this.server = server;
		this.resourceManager = resourceManager;
		this.cache = new ConcurrentHashMap<>();
	}
	
	public void onReload(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		this.cache.clear();
	}
	
	public FeatureTemplate load(ResourceLocation location) {
		return this.cache.computeIfAbsent(location, this::read);
	}
	
	private FeatureTemplate read(ResourceLocation location) {
		return this.resourceManager.getResource(location).flatMap((resource) -> {
			try(InputStream stream = resource.open()) {
				return FeatureTemplate.load(this.server.registryAccess().lookupOrThrow(Registries.BLOCK).filterFeatures(this.server.getWorldData().enabledFeatures()), stream);
			} catch (IOException e) {
				e.printStackTrace();
				return Optional.empty();
			}
		}).orElse(new FeatureTemplate(ImmutableList.of()));
	}
}
