package raccoonman.reterraforged.world.worldgen.layer;

import net.minecraft.world.level.levelgen.DensityFunction;

public record RequiredLayer(Layer<Reference<DensityFunction>> layer, int cacheRadius) {
}
