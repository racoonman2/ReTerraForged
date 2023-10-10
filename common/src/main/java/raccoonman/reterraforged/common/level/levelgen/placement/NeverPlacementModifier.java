package raccoonman.reterraforged.common.level.levelgen.placement;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.common.registries.RTFPlacementModifierTypes;

public class NeverPlacementModifier extends PlacementModifier {
	public static final NeverPlacementModifier INSTANCE = new NeverPlacementModifier();
	public static final Codec<NeverPlacementModifier> CODEC = Codec.unit(INSTANCE);
	
	@Override
	public Stream<BlockPos> getPositions(PlacementContext ctx, RandomSource randomSource, BlockPos pos) {
		return Stream.empty();
	}

	@Override
	public PlacementModifierType<NeverPlacementModifier> type() {
		return RTFPlacementModifierTypes.NEVER;
	}
}
