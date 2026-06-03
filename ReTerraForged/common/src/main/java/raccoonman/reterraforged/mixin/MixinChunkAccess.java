package raccoonman.reterraforged.mixin;

import java.util.OptionalInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFChunk;
import raccoonman.reterraforged.mixin.accessors.LevelChunkSectionAccessor;

@Mixin(ChunkAccess.class)
public abstract class MixinChunkAccess implements RTFChunk, LevelHeightAccessor {
	private OptionalInt maxHeight = OptionalInt.empty();

	@Override
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = OptionalInt.of(maxHeight);
	}

	@Override
	public OptionalInt getMaxHeight() {
		return this.maxHeight;
	}
	
	//TODO make this less intrusive
	@Inject(
		method = "fillBiomesFromNoise",
		at = @At("HEAD"),
		cancellable = true
	)
	public void fillBiomesFromNoise(BiomeResolver biomeResolver, Climate.Sampler sampler, CallbackInfo callback) {
		if(this.maxHeight.isEmpty()) {
			return;
		}
		
		int maxHeight = this.maxHeight.getAsInt();
		
		ChunkPos chunkPos = this.getPos();
		int quartX = QuartPos.fromBlock(chunkPos.getMinBlockX());
		int quartZ = QuartPos.fromBlock(chunkPos.getMinBlockZ());
		LevelHeightAccessor levelHeightAccessor = this.getHeightAccessorForGeneration();
		
		int highestTerrainSection = SectionPos.blockToSectionCoord(maxHeight) + 1;
		int highestLevelSection = this.getMaxSectionY();
		if(highestTerrainSection >= highestLevelSection) {
			return;
		}
		
		for (int sectionY = levelHeightAccessor.getMinSectionY(); sectionY <= highestTerrainSection; sectionY++) {
			LevelChunkSection levelChunkSection = this.getSection(this.getSectionIndexFromSectionY(sectionY));
			int quartY = QuartPos.fromSection(sectionY);
			levelChunkSection.fillBiomesFromNoise(biomeResolver, sampler, quartX, quartY, quartZ);
		}
		
		LevelChunkSectionAccessor highestFilledSection = ExtensionUtil.cast(this.getSection(this.getSectionIndexFromSectionY(highestTerrainSection)));
		for (int sectionY = highestTerrainSection + 1; sectionY <= highestLevelSection; sectionY++) {
			LevelChunkSection levelChunkSection = this.getSection(this.getSectionIndexFromSectionY(sectionY));
			LevelChunkSectionAccessor accessor = ExtensionUtil.cast(levelChunkSection);
			accessor.setBiomes(highestFilledSection.getBiomes().copy());
		}
		callback.cancel();
	}

	@Shadow
	public abstract LevelHeightAccessor getHeightAccessorForGeneration();

	@Shadow
	public abstract ChunkPos getPos();
	
	@Shadow
	public abstract LevelChunkSection getSection(int i);
}
