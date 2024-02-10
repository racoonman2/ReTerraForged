package raccoonman.reterraforged.world.worldgen.surface;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public interface RTFSurfaceContext {
	@Nullable
	Set<ResourceKey<Biome>> getSurroundingBiomes();
}
