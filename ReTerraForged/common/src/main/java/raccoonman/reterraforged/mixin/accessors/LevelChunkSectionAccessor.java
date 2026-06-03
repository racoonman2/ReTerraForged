package raccoonman.reterraforged.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerRO;

@Mixin(LevelChunkSection.class)
public interface LevelChunkSectionAccessor {
	@Accessor("biomes")
	void setBiomes(PalettedContainerRO<Holder<Biome>> biomes);
	
	@Accessor("biomes")
	PalettedContainerRO<Holder<Biome>> getBiomes();
}
