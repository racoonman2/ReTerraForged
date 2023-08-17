package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.chunk.ProtoChunk;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.TerrainHolder;
import raccoonman.reterraforged.common.level.levelgen.terrain.Terrain;

@Implements({
	@Interface(iface = TerrainHolder.class, prefix = ReTerraForged.MOD_ID + "$TerrainHolder$"),
})
@Mixin(ProtoChunk.class)
class MixinProtoChunk {
	private Terrain terrain;
	
	public void reterraforged$TerrainHolder$setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	
	public Terrain reterraforged$TerrainHolder$getTerrain() {
		return this.terrain;
	}
}