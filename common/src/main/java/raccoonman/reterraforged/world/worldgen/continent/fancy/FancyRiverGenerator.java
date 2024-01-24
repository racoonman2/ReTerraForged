package raccoonman.reterraforged.world.worldgen.continent.fancy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.rivermap.Rivermap;
import raccoonman.reterraforged.world.worldgen.rivermap.gen.GenWarp;
import raccoonman.reterraforged.world.worldgen.rivermap.river.BaseRiverGenerator;
import raccoonman.reterraforged.world.worldgen.rivermap.river.Network;
import raccoonman.reterraforged.world.worldgen.rivermap.river.River;
import raccoonman.reterraforged.world.worldgen.rivermap.river.RiverCarver;
import raccoonman.reterraforged.world.worldgen.rivermap.river.RiverConfig;
import raccoonman.reterraforged.world.worldgen.rivermap.river.RiverWarp;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;
import raccoonman.reterraforged.world.worldgen.util.Variance;

public class FancyRiverGenerator extends BaseRiverGenerator<FancyContinentGenerator> {
	private static final Variance MAIN_PADDING = Variance.of(0.05F, 0.1F);
	private static final Variance MAIN_JITTER = Variance.of(-0.2F, 0.4F);
	private float freq;

	public FancyRiverGenerator(FancyContinentGenerator continent, GeneratorContext context) {
		super(continent, context);
		this.freq = 1.0F / context.preset.world().continent.continentScale;
	}

	@Override
	public Rivermap generateRivers(int x, int z, long id) {
		Random random = new Random(id + this.seed);
		GenWarp warp = GenWarp.make((int) id, this.continentScale);
		List<Network> networks = new ArrayList<>(32);
		List<Network.Builder> roots = new ArrayList<>(16);
		for (Island island : ((FancyContinentGenerator) this.continent).getSource().getIslands()) {
			this.generateRoots(((FancyContinentGenerator) this.continent).getSource(), island, random, warp, roots);
			for (Network.Builder river : roots) {
				networks.add(river.build());
			}
			roots.clear();
		}
		return new Rivermap(x, z, networks.toArray(Network[]::new), warp);
	}

	private void generateRoots(FancyContinent continent, Island island, Random random, GenWarp warp, List<Network.Builder> roots) {
		Segment[] segments = island.getSegments();
		int lineCount = Math.max(1, 8 - island.getId());
		int endCount = Math.max(4, 12 - island.getId());
		for (int i = 0; i < segments.length; ++i) {
			boolean end = i == 0 || i == segments.length - 1;
			Segment segment = segments[i];
			int riverCount = end ? (lineCount - 1) : lineCount;
			this.collectSegmentRoots(continent, island, segment, riverCount, random, warp, roots);
		}
		Segment first = segments[0];
		this.collectPointRoots(continent, island, first.a, first.scaleA, endCount, random, warp, roots);
		Segment last = segments[segments.length - 1];
		this.collectPointRoots(continent, island, last.b, last.scaleB, endCount, random, warp, roots);
	}

	private void collectSegmentRoots(FancyContinent continent, Island island, Segment segment, int count, Random random, GenWarp warp, List<Network.Builder> roots) {
		float dx = segment.dx;
		float dy = segment.dy;
		float nx = dy / segment.length;
		float ny = -dx / segment.length;
		float stepSize = 1.0F / (count + 2);
		for (int i = 0; i < count; ++i) {
			float progress = stepSize + stepSize * i;
			if (progress > 1.0F) {
				return;
			}
			float startX = segment.a.x() + dx * progress;
			float startZ = segment.a.y() + dy * progress;
			float radiusScale = NoiseUtil	.lerp(segment.scaleA, segment.scaleB, progress);
			float radius = island.coast() * radiusScale;
			int dir = random.nextBoolean() ? -1 : 1;
			float dirX = nx * dir + FancyRiverGenerator.MAIN_JITTER.next(random);
			float dirZ = ny * dir + FancyRiverGenerator.MAIN_JITTER.next(random);
			float scale = getExtendScale(island.getId(), startX, startZ, dirX, dirZ, radius, continent);
			if (scale != 0.0f) {
				float startPad = FancyRiverGenerator.MAIN_PADDING.next(random);
				float x1 = startX + dir * dirX * radius * startPad;
				float y1 = startZ + dir * dirZ * radius * startPad;
				float x2 = startX + dirX * radius * scale;
				float y2 = startZ + dirZ * radius * scale;
				this.addRoot(x1, y1, x2, y2, this.main, random, warp, roots);
			}
		}
	}

	private void collectPointRoots(FancyContinent continent, Island island, Vec2f vec, float radiusScale, int count, Random random, GenWarp warp, List<Network.Builder> roots) {
		float yawStep = 6.2831855f / count;
		float radius = island.coast() * radiusScale;
		for (int i = 0; i < count; ++i) {
			float yaw = yawStep * i;
			float dx = NoiseUtil.cos(yaw);
			float dz = NoiseUtil.sin(yaw);
			float scale = getExtendScale(island.getId(), vec.x(), vec.y(), dx, dz, radius, continent);
			if (scale != 0.0F) {
				float startPad = FancyRiverGenerator.MAIN_PADDING.next(random);
				float startX = vec.x() + dx * startPad * radius;
				float startZ = vec.y() + dz * startPad * radius;
				float endX = vec.x() + dx * radius * scale;
				float endZ = vec.y() + dz * radius * scale;
				if (continent.getEdgeValue(endX, endZ, seed) <= 0.1F) {
					this.addRoot(startX, startZ, endX, endZ, this.main, random, warp, roots);
				}
			}
		}
	}

	private void addRoot(float x1, float z1, float x2, float z2, RiverConfig config, Random random, GenWarp warp, List<Network.Builder> roots) {
		River river = new River(x1 / this.freq, z1 / this.freq, x2 / this.freq, z2 / this.freq);
		if (this.riverOverlaps(river, null, roots)) {
			return;
		}
		RiverCarver.Settings settings = BaseRiverGenerator.creatSettings(random);
		settings.fadeIn = config.fade;
		settings.valleySize = 275.0F * River.FORK_VALLEY.next(random);
		RiverWarp riverWarp = RiverWarp.create(0.1F, 0.85F, random);
		RiverCarver carver = new RiverCarver(river, riverWarp, config, settings, this.levels);
		Network.Builder network = Network.builder(carver);
		roots.add(network);
		this.generateForks(network, River.FORK_SPACING, this.fork, random, warp, roots, 0);
		this.generateWetlands(network, random);
	}

	private static float getExtendScale(int islandId, float startX, float startZ, float dx, float dz, float radius, FancyContinent continent) {
		float scale = 1.0F;
		for (int i = 0; i < 25; ++i) {
			float x = startX + dx * radius * scale;
			float z = startZ + dz * radius * scale;
			long packed = continent.getValueId(x, z);
			if (PosUtil.unpackLeft(packed) != islandId) {
				return 0.0F;
			}
			if (PosUtil.unpackRightf(packed) < 0.1F) {
				return scale;
			}
			scale += 0.075F;
		}
		return 0.0F;
	}
}
