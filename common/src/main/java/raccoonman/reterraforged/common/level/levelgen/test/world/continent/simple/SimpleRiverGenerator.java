package raccoonman.reterraforged.common.level.levelgen.test.world.continent.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.SimpleContinent;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.gen.GenWarp;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.river.BaseRiverGenerator;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.river.Network;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.river.River;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.river.RiverCarver;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.river.RiverWarp;

public class SimpleRiverGenerator extends BaseRiverGenerator<SimpleContinent> {
	
	public SimpleRiverGenerator(SimpleContinent continent, GeneratorContext context) {
		super(continent, context);
	}

	@Override
	public List<Network.Builder> generateRoots(int x, int z, Random random, GenWarp warp, int seed) {
		float start = random.nextFloat();
		float spacing = 6.2831855F / this.count;
		float spaceVar = spacing * 0.75F;
		float spaceBias = -spaceVar / 2.0F;
		List<Network.Builder> roots = new ArrayList<>(this.count);
		for (int i = 0; i < this.count; ++i) {
			float variance = random.nextFloat() * spaceVar + spaceBias;
			float angle = start + spacing * i + variance;
			float dx = NoiseUtil.sin(angle);
			float dz = NoiseUtil.cos(angle);
			float startMod = 0.05F + random.nextFloat() * 0.45F;
			float length = ((SimpleContinent) this.continent).getDistanceToOcean(x, z, dx, dz, seed);
			float startDist = Math.max(400.0f, startMod * length);
			float x2 = x + dx * startDist;
			float z2 = z + dz * startDist;
			float x3 = x + dx * length;
			float z3 = z + dz * length;
			float valleyWidth = 275.0F * River.MAIN_VALLEY.next(random);
			River river = new River((float) (int) x2, (float) (int) z2, (float) (int) x3, (float) (int) z3);
			RiverCarver.Settings settings = BaseRiverGenerator.creatSettings(random);
			settings.fadeIn = this.main.fade;
			settings.valleySize = valleyWidth;
			RiverWarp riverWarp = RiverWarp.create(0.1F, 0.85F, random);
			RiverCarver carver = new RiverCarver(river, riverWarp, this.main, settings, this.levels);
			Network.Builder branch = Network.builder(carver);
			roots.add(branch);
			this.addLake(branch, random, warp);
		}
		return roots;
	}
}
