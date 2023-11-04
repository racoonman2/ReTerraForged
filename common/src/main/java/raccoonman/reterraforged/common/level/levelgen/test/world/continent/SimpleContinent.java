package raccoonman.reterraforged.common.level.levelgen.test.world.continent;

public interface SimpleContinent extends Continent {
	float getEdgeValue(float x, float z, int seed);

	default float getDistanceToEdge(int cx, int cz, float dx, float dy, int seed) {
		return 1.0F;
	}

	default float getDistanceToOcean(int cx, int cz, float dx, float dy, int seed) {
		return 1.0F;
	}
}
