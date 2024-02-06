package raccoonman.reterraforged.mixin;

import java.util.BitSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableObject;
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
import raccoonman.reterraforged.world.worldgen.surface.BiomeCheckStatus;
import raccoonman.reterraforged.world.worldgen.surface.RTFSurfaceContext;
import raccoonman.reterraforged.world.worldgen.surface.SurfaceRegion;

// cool hack to fix performance issues caused by BiomeConditionSource
@Mixin(targets = "net.minecraft.world.level.levelgen.SurfaceRules$BiomeConditionSource")
public class MixinSurfaceRules$BiomeConditionSource {
	@Final
    Predicate<ResourceKey<Biome>> biomeNameTest;

    @Inject(at = @At("HEAD"), method = "apply", cancellable = true)
    public void apply(SurfaceRules.Context ctx, CallbackInfoReturnable<SurfaceRules.Condition> callback) {
    	WorldGenRegion region = SurfaceRegion.get();

    	Set<ResourceKey<Biome>> surroundingBiomes;
    	if((Object) ctx instanceof RTFSurfaceContext rtfSurfaceContext && (surroundingBiomes = rtfSurfaceContext.getSurroundingBiomes()) != null) {
    		boolean skipChunk = surroundingBiomes.stream().filter(this.biomeNameTest).findAny().isEmpty();
            callback.setReturnValue(new BiomeCondition(skipChunk, null, ctx, region));
    	}
    }
    
    class BiomeCondition extends SurfaceRules.LazyYCondition {
    	private boolean skipChunk;
    	private MutableObject<BitSet> mask;
    	private SurfaceRules.Context ctx;
    	private WorldGenRegion region;
    	
    	private int lastSectionY;
    	private int lastQuartY;
    	private BiomeCheckStatus status;
    	
    	public BiomeCondition(boolean skipChunk, MutableObject<BitSet> mask, SurfaceRules.Context ctx, WorldGenRegion region) {
    		super(ctx);
    		this.skipChunk = skipChunk;
    		this.mask = mask;
    		this.ctx = ctx;
    		this.region = region;
    		
    		this.lastSectionY = Integer.MIN_VALUE;
    		this.lastQuartY = Integer.MIN_VALUE;
    	}
    	
    	private Boolean skipSection(BitSet mask) {
			int sectionY = this.context.chunk.getSectionIndex(this.ctx.blockY);
			
			boolean failed = false;
			boolean found = false;
			
			for(int x = -1; x <= 1; x++) {
				for(int y = -1; y <= 1; y++) {
					for(int z = -1; z <= 1; z++) {
		    			int centeredX = x + 1;
		    			int centeredY = y + 1;
		    			int centeredZ = z + 1;
						int index = (centeredX * 9) + (centeredZ * 3) + Math.min(this.context.chunk.getMaxSection(), sectionY + centeredY);
						
						if(mask.get(index))	{
							found = true;
							if(failed) {
								return false;
							}
						} else {
							failed = true;
						}
					}
				}
			}
			return found ? null : true;
    	}
    	
    	private BiomeCheckStatus getStatus() {
			int quartX = QuartPos.fromBlock(this.ctx.blockX);
			int quartY = QuartPos.fromBlock(this.ctx.blockY);
			int quartZ = QuartPos.fromBlock(this.ctx.blockZ);
			
			boolean failed = false;
			boolean found = false;
			
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
							found = true;
							if(failed) {
								return BiomeCheckStatus.MIXED;
							}
						} else {
							failed = true;
						}
					}
				}
			}
			return found ? BiomeCheckStatus.ALL : BiomeCheckStatus.NONE;
    	}
    	
		@Override
		public boolean compute() {
			if(this.skipChunk) {
				return false;
			}
			
//			int sectionY = this.context.chunk.getSectionIndex(this.context.blockY);
//			BitSet mask;
//			if(this.lastSectionY != sectionY && (mask = this.mask.getValue()) != null) {
//				this.lastSectionY = sectionY;
//				this.skip = this.skipSection(mask);
//				System.out.println(this.skip);
//			}
//			
//			if(this.skip) {
//				return false;
//			}
//			
//			if(this.skip == null) {
//				System.out.println("section culling");
//				return true;
//			}
			
			int quartY = QuartPos.fromBlock(this.context.blockY);
			if(this.lastQuartY != quartY) {
				this.status = this.getStatus();
				this.lastQuartY = quartY;
			}
		
			return switch(this.status) {
				case ALL: yield true;
				case MIXED: yield this.ctx.biome.get().is(MixinSurfaceRules$BiomeConditionSource.this.biomeNameTest);
				case NONE: yield false;
			};
		}
    }
}