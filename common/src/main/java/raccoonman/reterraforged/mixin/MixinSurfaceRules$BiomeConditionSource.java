package raccoonman.reterraforged.mixin;

import java.util.Set;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.world.worldgen.surface.RTFSurfaceContext;
import raccoonman.reterraforged.world.worldgen.surface.SurfaceRegion;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

@Mixin(targets = "net.minecraft.world.level.levelgen.SurfaceRules$BiomeConditionSource")
public class MixinSurfaceRules$BiomeConditionSource {
	@Final
    Predicate<ResourceKey<Biome>> biomeNameTest;

    @Inject(at = @At("HEAD"), method = "apply", cancellable = true)
    public void apply(SurfaceRules.Context ctx, CallbackInfoReturnable<SurfaceRules.Condition> callback) {
    	WorldGenRegion region = SurfaceRegion.get();

    	Set<ResourceKey<Biome>> surroundingBiomes;
    	if((Object) ctx instanceof RTFSurfaceContext rtfSurfaceContext && (surroundingBiomes = rtfSurfaceContext.getSurroundingBiomes()) != null) {
            callback.setReturnValue(new BiomeCondition(surroundingBiomes.stream().filter(this.biomeNameTest).findAny().isEmpty(), ctx, region));
    	}
    }
    
    class BiomeCondition implements SurfaceRules.Condition {
    	private boolean notNearby;
    	private SurfaceRules.Context ctx;
    	private WorldGenRegion region;
    	
    	private boolean skip;
    	private boolean lastResult;
    	private int lastBlockY;
    	private long lastBlockXZ;
    	private int lastQuartY;
    	
    	public BiomeCondition(boolean notNearby, SurfaceRules.Context ctx, WorldGenRegion region) {
    		this.notNearby = notNearby;
    		this.ctx = ctx;
    		this.region = region;

    		this.lastBlockY = Integer.MAX_VALUE;
    		this.lastQuartY = Integer.MAX_VALUE;
    	}
    	
    	private boolean skip() {
			int quartX = QuartPos.fromBlock(this.ctx.blockX);
			int quartY = QuartPos.fromBlock(this.ctx.blockY);
			int quartZ = QuartPos.fromBlock(this.ctx.blockZ);
			
			for(int x = -1; x <= 1; x++) {
				for(int y = -1; y <= 1; y++) {
					for(int z = -1; z <= 1; z++) {
						int dQuartX = quartX + x;
						int dQuartZ = quartZ + z;
						
						int sectionX = QuartPos.toSection(dQuartX);
						int sectionZ = QuartPos.toSection(dQuartZ);
						
						int lQuartX = dQuartX - QuartPos.fromSection(sectionX);
						int lQuartY = quartY + y;
						int lQuartZ = dQuartZ - QuartPos.fromSection(sectionZ);
						
						ChunkAccess chunk = this.region.getChunk(sectionX, sectionZ);
						
						if(chunk.getNoiseBiome(lQuartX, lQuartY, lQuartZ).is(MixinSurfaceRules$BiomeConditionSource.this.biomeNameTest)) {
							return false;
						}
					}
				}	
			}
			return true;
    	}
    	
		@Override
		public boolean test() {
			if(this.notNearby) {
				return false;
			}

			int quartY = QuartPos.fromBlock(this.ctx.blockY);
			long blockXZ = PosUtil.pack(this.ctx.blockX, this.ctx.blockZ);
			if(quartY != this.lastQuartY || this.lastBlockXZ != blockXZ) {
				this.lastQuartY = quartY;
				this.lastBlockXZ = blockXZ;
				this.skip = this.skip();
			}

			if(this.skip) {
				return false;
			}

			if(this.lastBlockY != this.ctx.blockY) {
				this.lastBlockY = this.ctx.blockY;
				this.lastResult = this.ctx.biome.get().is(MixinSurfaceRules$BiomeConditionSource.this.biomeNameTest);
			}
			return this.lastResult;
		}
    }
}