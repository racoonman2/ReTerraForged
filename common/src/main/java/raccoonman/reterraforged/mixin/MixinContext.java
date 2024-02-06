package raccoonman.reterraforged.mixin;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.world.worldgen.surface.RTFSurfaceContext;
import raccoonman.reterraforged.world.worldgen.surface.SurfaceRegion;

@Implements(@Interface(iface = RTFSurfaceContext.class, prefix = "reterraforged$RTFSurfaceContext$"))
@Mixin(Context.class)
abstract class MixinContext {
	@Shadow
	@Final
    public ChunkAccess chunk;

	@Nullable
	private Set<ResourceKey<Biome>> surroundingBiomes;
	
	@Inject(at = @At("TAIL"), method = "<init>")
	private void Context(CallbackInfo callback) {
		WorldGenRegion region = SurfaceRegion.get();
		
		if(region != null) {
			ChunkPos centerPos = this.chunk.getPos();
			
	    	this.surroundingBiomes = new HashSet<>();
	    	
	    	for(int x = -1; x <= 1; x++) {
	    		for(int z = -1; z <= 1; z++) {
	    			ChunkAccess chunk = region.getChunk(centerPos.x + x, centerPos.z + z);
	    			
	    			for(LevelChunkSection section : chunk.getSections()) {
	    				section.getBiomes().getAll((biome) -> {
	    					biome.unwrapKey().ifPresent(this.surroundingBiomes::add);
	    				});
	    			}
	        	}
	    	}
		}
	}

	public Set<ResourceKey<Biome>> reterraforged$RTFSurfaceContext$getSurroundingBiomes() {
		return this.surroundingBiomes;
	}
}
