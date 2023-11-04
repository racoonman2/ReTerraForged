package raccoonman.reterraforged.common.asm.extensions;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileProvider;

public interface RandomStateExtension {
	@Nullable
	TileProvider tileCache();
	
	Noise shift(Noise noise);
	
	DensityFunction wrap(DensityFunction input);
}
