package raccoonman.reterraforged.world.worldgen.terrablender;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;

public class TBCompat {
	public static final ResourceKey<DensityFunction> UNIQUENESS = ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation("terrablender:uniqueness"));
}
