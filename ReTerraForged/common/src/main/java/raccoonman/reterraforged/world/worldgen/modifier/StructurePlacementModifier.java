package raccoonman.reterraforged.world.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import raccoonman.reterraforged.mixin.accessors.StructureSetAccessor;

public record StructurePlacementModifier(Holder<StructureSet> structureSet, StructurePlacement placement) implements Modifier {
	public static final MapCodec<StructurePlacementModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StructureSet.CODEC.fieldOf("structure_set").forGetter(StructurePlacementModifier::structureSet),
		StructurePlacement.CODEC.fieldOf("placement").forGetter(StructurePlacementModifier::placement)
	).apply(instance, StructurePlacementModifier::new));

	@Override
	public void applyModifier() {
		((StructureSetAccessor) (Object) this.structureSet.value()).setPlacement(this.placement);
	}

	@Override
	public MapCodec<StructurePlacementModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
