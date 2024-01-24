package raccoonman.reterraforged.world.worldgen.biome.spawn;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import raccoonman.reterraforged.world.worldgen.biome.RTFClimateSampler;

// TODO replace this with a mixin once we can get that working
@Deprecated(forRemoval = true)
public class SpawnFinderFix {
	public Result result;

	public SpawnFinderFix(List<ParameterPoint> list, Sampler sampler) {
		if ((Object) sampler instanceof RTFClimateSampler rtfClimateSampler) {
			BlockPos center = rtfClimateSampler.getSpawnSearchCenter();

			this.result = SpawnFinderFix.getSpawnPositionAndFitness(list, sampler, center.getX(), center.getZ());
			this.radialSearch(list, sampler, 2048.0f, 512.0f);
			this.radialSearch(list, sampler, 512.0f, 32.0f);
		}
	}

	private void radialSearch(List<ParameterPoint> list, Sampler sampler, float f, float g) {
		float h = 0.0f;
		float i = g;
		BlockPos blockPos = this.result.location();
		while (i <= f) {
			int j = blockPos.getX() + (int) (Math.sin(h) * (double) i);
			Result result = SpawnFinderFix.getSpawnPositionAndFitness(list, sampler, j,
					blockPos.getZ() + (int) (Math.cos(h) * (double) i));
			if (result.fitness() < this.result.fitness()) {
				this.result = result;
			}
			if (!((double) (h += g / i) > Math.PI * 2))
				continue;
			h = 0.0f;
			i += g;
		}
	}

	private static Result getSpawnPositionAndFitness(List<ParameterPoint> list, Sampler sampler, int i, int j) {
		double d = Mth.square(2500.0);
		long l = (long) ((double) Mth.square(10000.0f)
				* Math.pow((double) (Mth.square((long) i) + Mth.square((long) j)) / d, 2.0));
		TargetPoint targetPoint = sampler.sample(QuartPos.fromBlock(i), 0, QuartPos.fromBlock(j));
		TargetPoint targetPoint2 = new TargetPoint(targetPoint.temperature(), targetPoint.humidity(),
				targetPoint.continentalness(), targetPoint.erosion(), 0L, targetPoint.weirdness());
		long m = Long.MAX_VALUE;
		for (ParameterPoint parameterPoint : list) {
			m = Math.min(m, parameterPoint.fitness(targetPoint2));
		}
		return new Result(new BlockPos(i, 0, j), l + m);
	}

	public record Result(BlockPos location, long fitness) {
	}
}
