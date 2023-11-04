package raccoonman.reterraforged.common.level.levelgen.test.world.continent.simple;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2i;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;

public class SingleContinentGenerator extends ContinentGenerator {
    private Vec2i center;
    
    public SingleContinentGenerator(Seed seed, GeneratorContext context) {
        super(seed, context);
        long center = this.getNearestCenter(0.0F, 0.0F, 0);
        int cx = NoiseUtil.unpackLeft(center);
        int cz = NoiseUtil.unpackRight(center);
        this.center = new Vec2i(cx, cz);
    }
    
    @Override
    public void apply(Cell cell, float x, float y, int seed) {
        super.apply(cell, x, y, seed);
        if (cell.continentX != this.center.x() || cell.continentZ != this.center.y()) {
            cell.continentId = 0.0F;
            cell.continentEdge = 0.0F;
            cell.continentX = 0;
            cell.continentZ = 0;
        }
    }
}
