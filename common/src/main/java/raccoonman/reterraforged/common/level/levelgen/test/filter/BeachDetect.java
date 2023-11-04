package raccoonman.reterraforged.common.level.levelgen.test.filter;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.filter.Filter.Visitor;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.ControlPoints;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;

public class BeachDetect implements Filter, Visitor {
    private ControlPoints transition;
    
    public BeachDetect(GeneratorContext context) {
        this.transition = ControlPoints.make(context.settings.world().controlPoints);
    }
    
    @Override
    public void apply(Filterable map, int seedX, int seedZ, int iterations) {
        this.iterate(map, this);
    }
    
    @Override
    public void visit(Filterable cellMap, Cell cell, int dx, int dz) {
        if (cell.terrain.isCoast() && cell.continentEdge < this.transition.beach()) {
            Cell n = cellMap.getCellRaw(dx, dz - 8);
            Cell s = cellMap.getCellRaw(dx, dz + 8);
            Cell e = cellMap.getCellRaw(dx + 8, dz);
            Cell w = cellMap.getCellRaw(dx - 8, dz);
            float gx = this.grad(e, w, cell);
            float gz = this.grad(n, s, cell);
            float d2 = gx * gx + gz * gz;
            if (d2 < 0.275F) {
                cell.terrain = TerrainType.BEACH;
            }
        }
    }
    
    private float grad(Cell a, Cell b, Cell def) {
        int distance = 17;
        if (a.isAbsent()) {
            a = def;
            distance -= 8;
        }
        if (b.isAbsent()) {
            b = def;
            distance -= 8;
        }
        return (a.value - b.value) / distance;
    }
}
