package raccoonman.reterraforged.world.worldgen.cell.continent.fancy;

import java.util.Random;

import raccoonman.reterraforged.data.worldgen.preset.settings.WorldSettings.ControlPoints;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.cell.rivermap.RiverGenerator;
import raccoonman.reterraforged.world.worldgen.cell.rivermap.Rivermap;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

public class FancyContinent implements RiverGenerator {
	private Island[] islands;
	private FancyRiverGenerator riverGenerator;

	public FancyContinent(int seed, int nodes, float radius, GeneratorContext context, FancyContinentGenerator continent) {
		ControlPoints controlPoints = context.preset.world().controlPoints;
		this.islands = generateIslands(controlPoints, 3, nodes, radius, new Random(seed));
		this.riverGenerator = new FancyRiverGenerator(continent, context);
	}

	public float getEdgeValue(float x, float y, int seed) {
		float value = 0.0F;
		for (Island island : this.islands) {
			float v = island.getEdgeValue(x, y);
			value = Math.max(v, value);
		}
		return process(value);
	}

	public Island getMain() {
		return this.islands[0];
	}

	public Island[] getIslands() {
		return this.islands;
	}

	public long getMin() {
		float x = Float.MAX_VALUE;
		float z = Float.MAX_VALUE;
		for (Island island : this.islands) {
			x = Math.min(x, island.getMin().x());
			z = Math.min(z, island.getMin().y());
		}
		return PosUtil.packf(x, z);
	}

	public long getMax() {
		float x = Float.MIN_VALUE;
		float z = Float.MIN_VALUE;
		for (Island island : this.islands) {
			x = Math.max(x, island.getMin().x());
			z = Math.max(z, island.getMin().y());
		}
		return PosUtil.packf(x, z);
	}

	public float getLandValue(float x, float y) {
		float value = 0.0F;
		for (Island island : this.islands) {
			float v = island.getLandValue(x, y);
			value = Math.max(v, value);
		}
		return value;
	}

	public long getValueId(float x, float y) {
		int id = -1;
		float value = 0.0F;
		for (Island island : this.islands) {
			float v = island.getEdgeValue(x, y);
			if (v > value) {
				value = v;
				id = island.getId();
			}
			value = Math.max(v, value);
		}
		return PosUtil.packMix(id, value);
	}

	@Override
	public Rivermap generateRivers(int x, int z, long id) {
		return this.riverGenerator.generateRivers(x, z, id);
	}

	private static float process(float value) {
		return value;
	}

	private static Island[] generateIslands(ControlPoints controlPoints, int islandCount, int nodeCount, float radius, Random random) {
		int dirs = 4;
		Island main = generate(0, controlPoints, nodeCount, radius, random);
		Island[] islands = new Island[1 + islandCount * dirs];
		islands[0] = main;
		int i = 1;
		float yawStep = 1.0F / dirs * 6.2831855F;
		for (int dir = 0; dir < dirs; ++dir) {
			Island previous = main;
			int nCount = Math.max(2, nodeCount - 1);
			float r = radius * 0.5F;
			float yaw = yawStep * dir + random.nextFloat() * yawStep;
			for (int island = 0; island < islandCount; ++island) {
				Island next = generate(i, controlPoints, nCount, r, random);
				float y = yaw + nextFloat(random, -0.2F, 0.2F);
				float distance = previous.radius();
				float dx = NoiseUtil.sin(y * 6.2831855F) * distance;
				float dz = NoiseUtil.cos(y * 6.2831855F) * distance;
				float ox = previous.getCenter().x() + dx;
				float oy = previous.getCenter().y() + dz;
				next.translate(new Vec2f(ox, oy));
				nCount = Math.max(2, nCount - 1);
				r *= 0.8F;
				islands[i++] = next;
				previous = next;
			}
		}
		return islands;
	}

	private static Island generate(int id, ControlPoints controlPoints, int nodes, float radius, Random random) {
		float minScale = 0.75F;
		float maxScale = 2.5F;
		float minLen = radius * 1.5F;
		float maxLen = radius * 3.5F;
		float maxYaw = 1.5707964F;
		float minYaw = -maxYaw;
		Segment[] segments = new Segment[nodes - 1];
		Vec2f pointA = new Vec2f(0.0F, 0.0F);
		float scaleA = nextFloat(random, minScale, maxScale);
		float previousYaw = nextFloat(random, 0.0F, 6.2831855F);
		for (int i = 0; i < segments.length; ++i) {
			float length = nextFloat(random, minLen, maxLen);
			float yaw = previousYaw + nextFloat(random, minYaw, maxYaw);
			float dx = NoiseUtil.sin(yaw) * length;
			float dz = NoiseUtil.cos(yaw) * length;
			Vec2f pointB = new Vec2f(pointA.x() + dx, pointA.y() + dz);
			float scaleB = nextFloat(random, minScale, maxScale);
			segments[i] = new Segment(pointA, pointB, scaleA, scaleB);
			previousYaw = yaw;
			pointA = pointB;
			scaleA = scaleB;
		}
		return new Island(id, segments, controlPoints, radius * 3.0f, radius * 1.25F, radius, radius * 0.975F);
	}
	
	public static float nextFloat(Random random, float min, float max) {
		return min + random.nextFloat() * (max - min);
	}
}
