package raccoonman.reterraforged.debug;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.data.preset.registry.RTFLayers;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.layer.Layer;

public class DebugEntryLayer implements DebugScreenEntry {

	@Override
	public void display(DebugScreenDisplayer debugScreenDisplayer, Level level, LevelChunk levelChunk, LevelChunk levelChunk2) {
		Minecraft minecraft = Minecraft.getInstance();
		Entity camera = minecraft.getCameraEntity();
		if(!(level instanceof ServerLevel serverLevel) || camera == null) {
			return;
		}
		
		ResourceLocation layerName = RTFLayers.TERRAIN.location();

        RegistryAccess registryAccess = level.registryAccess();
	    Registry<Layer.Factory<?>> layerRegistry = registryAccess.lookupOrThrow(RTFRegistries.LAYER);
	    Optional<Layer.Factory<?>> layerFactory = layerRegistry.getOptional(layerName);
	    
	    if(layerFactory.isEmpty()) {
	    	return;
	    }
	    
		ServerChunkCache serverChunkCache = serverLevel.getChunkSource();
		RandomState randomState = serverChunkCache.randomState();
		RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);
		Layer<?> terrainLayer = rtfRandomState.getMappedLayer(layerFactory.get());
		
		debugScreenDisplayer.addToGroup(layerName, "Layer: " + layerName);

		BlockPos blockPos = camera.blockPosition();
		terrainLayer.addDebugScreenInfo(debugScreenDisplayer, layerName, randomState, blockPos);
	}

	@Override
	public boolean isAllowed(boolean bl) {
		return true;
	}
}
