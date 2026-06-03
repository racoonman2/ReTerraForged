package raccoonman.reterraforged.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

@Mixin(StructureSet.class)
public interface StructureSetAccessor {

    @Mutable
	@Accessor("placement")
	public void setPlacement(StructurePlacement placement);
}
