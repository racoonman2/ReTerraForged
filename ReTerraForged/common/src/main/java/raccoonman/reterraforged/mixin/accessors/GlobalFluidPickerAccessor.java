package raccoonman.reterraforged.mixin.accessors;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

@Mixin(NoiseBasedChunkGenerator.class)
public interface GlobalFluidPickerAccessor {
	
	@Mutable
	@Accessor("globalFluidPicker")
	void setGlobalFluidPicker(Supplier<Aquifer.FluidPicker> fluidPicker);
}
