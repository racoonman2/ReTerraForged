package raccoonman.reterraforged.world.worldgen;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.RegistryAccess;

public interface RTFRandomState {
	void initialize(RegistryAccess registries);
	
	@Nullable
	GeneratorContext generatorContext();
}
