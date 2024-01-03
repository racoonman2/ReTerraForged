package raccoonman.reterraforged.world.worldgen.feature.placement;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

@Deprecated
public class LegacyCountExtraModifier extends PlacementModifier {
	public static final Codec<LegacyCountExtraModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("count").forGetter((p) -> p.count),
		Codec.FLOAT.fieldOf("extra_chance").forGetter((p) -> p.extraChance),
		Codec.INT.fieldOf("extra_count").forGetter((p) -> p.extraCount)
	).apply(instance, LegacyCountExtraModifier::new));

	private int count;
	private float extraChance;
	private int extraCount;
	
	public LegacyCountExtraModifier(int count, float extraChance, int extraCount) {
		this.count = count;
		this.extraChance = extraChance;
		this.extraCount = extraCount;
	}
	
	@Override
	public Stream<BlockPos> getPositions(PlacementContext ctx, RandomSource random, BlockPos pos) {
	      int i = this.count + (random.nextFloat() < this.extraChance ? this.extraCount : 0);
	      return IntStream.range(0, i).mapToObj((o) -> {
	         return pos;
	      });
	}

	@Override
	public PlacementModifierType<LegacyCountExtraModifier> type() {
		return RTFPlacementModifiers.LEGACY_COUNT_EXTRA;
	}
}
