package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.chunk.ProtoChunk;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.TerrainDataHolder;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainData;

@Implements({
	@Interface(iface = TerrainDataHolder.class, prefix = ReTerraForged.MOD_ID + "$terrainDataHolder$"),
})
@Mixin(ProtoChunk.class)
class MixinProtoChunk {
	private volatile TerrainData terrainData;
	
	public void reterraforged$terrainDataHolder$setTerrainData(TerrainData terrainData) {
		this.terrainData = terrainData;
	}
	
	public TerrainData reterraforged$terrainDataHolder$getTerrainData() {
		return this.terrainData;
	}	
}
