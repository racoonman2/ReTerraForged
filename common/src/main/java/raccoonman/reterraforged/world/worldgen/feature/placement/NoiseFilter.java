package raccoonman.reterraforged.world.worldgen.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

class NoiseFilter extends PlacementFilter {
	public static final Codec<NoiseFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.CODEC.fieldOf("noise").forGetter((filter) -> filter.noise),
		Codec.FLOAT.fieldOf("threshold").forGetter((filter) -> filter.threshold)
	).apply(instance, NoiseFilter::new));

	private Holder<Noise> noise;
	private float threshold;
	
	public NoiseFilter(Holder<Noise> noise, float threshold) {
		this.noise = noise;
		this.threshold = threshold;
	}
	
	@Override
	protected boolean shouldPlace(PlacementContext ctx, RandomSource rand, BlockPos pos) {
		return this.noise.value().compute(pos.getX(), pos.getZ(), (int) ctx.getLevel().getSeed()) > this.threshold;
	}
	
	@Override
	public PlacementModifierType<NoiseFilter> type() {
		return RTFPlacementModifiers.NOISE_FILTER;
	}
}
