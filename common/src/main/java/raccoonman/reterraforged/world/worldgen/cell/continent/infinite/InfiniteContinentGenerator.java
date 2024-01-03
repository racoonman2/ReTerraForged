package raccoonman.reterraforged.world.worldgen.cell.continent.infinite;

import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.continent.SimpleContinent;
import raccoonman.reterraforged.world.worldgen.cell.continent.simple.SimpleRiverGenerator;
import raccoonman.reterraforged.world.worldgen.cell.rivermap.LegacyRiverCache;
import raccoonman.reterraforged.world.worldgen.cell.rivermap.RiverCache;
import raccoonman.reterraforged.world.worldgen.cell.rivermap.Rivermap;

public class InfiniteContinentGenerator implements SimpleContinent {
	private RiverCache riverCache;
	
	public InfiniteContinentGenerator(GeneratorContext context) {
        this.riverCache = new LegacyRiverCache(new SimpleRiverGenerator(this, context));
	}
	
	@Override
	public void apply(Cell cell, float x, float z) {
		cell.continentId = 0.0F;
		cell.continentEdge = 0.0F;
		cell.continentX = 0;
		cell.continentZ = 0;
	}

	@Override
	public Rivermap getRivermap(int x, int z) {
		return this.riverCache.getRivers(x, z);
	}

	@Override
	public long getNearestCenter(float x, float z) {
		return 0;
	}

	@Override
	public float getEdgeValue(float x, float z) {
		return 1.0F;
	}
}
