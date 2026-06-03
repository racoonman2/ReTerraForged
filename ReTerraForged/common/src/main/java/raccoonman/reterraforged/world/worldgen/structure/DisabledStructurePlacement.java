package raccoonman.reterraforged.world.worldgen.structure;

import java.util.Optional;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

public class DisabledStructurePlacement extends StructurePlacement {
	public static final DisabledStructurePlacement INSTANCE = new DisabledStructurePlacement(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 0.0F, 0, Optional.empty());
    public static final MapCodec<DisabledStructurePlacement> CODEC = MapCodec.unit(INSTANCE);

	@SuppressWarnings("deprecation")
	public DisabledStructurePlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> optional) {
		super(locateOffset, frequencyReductionMethod, frequency, salt, optional);
	}

	@Override
	protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int chunkX, int chunkZ) {
		return false;
	}

	@Override
	public StructurePlacementType<DisabledStructurePlacement> type() {
		return RTFStructurePlacements.DISABLED;
	}
}
