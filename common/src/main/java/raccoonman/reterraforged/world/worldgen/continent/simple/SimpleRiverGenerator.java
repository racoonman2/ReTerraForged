package raccoonman.reterraforged.world.worldgen.continent.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.continent.SimpleContinent;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.rivermap.gen.GenWarp;
import raccoonman.reterraforged.world.worldgen.rivermap.river.BaseRiverGenerator;
import raccoonman.reterraforged.world.worldgen.rivermap.river.Network;
import raccoonman.reterraforged.world.worldgen.rivermap.river.River;
import raccoonman.reterraforged.world.worldgen.rivermap.river.RiverWarp;
import raccoonman.reterraforged.world.worldgen.terrain.populator.RiverPopulator;

public class SimpleRiverGenerator extends BaseRiverGenerator<SimpleContinent> {
	
	public SimpleRiverGenerator(SimpleContinent continent, GeneratorContext context) {
		super(continent, context);
	}

	@Override
	public List<Network.Builder> generateRoots(int x, int z, Random random, GenWarp warp) {
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
			float length = this.continent.getDistanceToOcean(x, z, dx, dz);
			float startDist = Math.max(400.0F, startMod * length);
			float x2 = x + dx * startDist;
			float z2 = z + dz * startDist;
			float x3 = x + dx * length;
			float z3 = z + dz * length;
			float valleyWidth = 275.0F * River.MAIN_VALLEY.next(random);
			River river = new River((float) (int) x2, (float) (int) z2, (float) (int) x3, (float) (int) z3);
			RiverPopulator.Settings settings = BaseRiverGenerator.creatSettings(random);
			settings.fadeIn = this.main.fade;
			settings.valleySize = valleyWidth;
			RiverWarp riverWarp = RiverWarp.create(0.1F, 0.85F, random);
			RiverPopulator carver = new RiverPopulator(river, riverWarp, this.main, settings, this.levels);
			Network.Builder branch = Network.builder(carver);
			roots.add(branch);
			this.addLake(branch, random, warp);
		}
		return roots;
	}
}
