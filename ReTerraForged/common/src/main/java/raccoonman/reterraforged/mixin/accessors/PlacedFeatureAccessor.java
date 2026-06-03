package raccoonman.reterraforged.mixin.accessors;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

@Mixin(PlacedFeature.class)
public interface PlacedFeatureAccessor {

	@Mutable
	@Accessor("placement")
	void setPlacement(List<PlacementModifier> placement);
}
