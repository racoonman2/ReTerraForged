package raccoonman.reterraforged.world.worldgen;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public interface RTFRandomState {
	void initialize(RegistryAccess registryAccess);
	
	@Nullable
	RegistryAccess registryAccess();
	
	@Nullable
	Preset preset();

	@Nullable
	GeneratorContext generatorContext();
	
	DensityFunction wrap(DensityFunction function);

	Noise wrap(Noise noise);
}
