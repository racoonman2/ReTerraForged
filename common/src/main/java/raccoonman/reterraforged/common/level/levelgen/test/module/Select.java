package raccoonman.reterraforged.common.level.levelgen.test.module;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;

public class Select {
    private Noise control;
    
    public Select(Noise control) {
        this.control = control;
    }
    
    public float getSelect(Cell cell, float x, float y, int seed) {
        return this.control.compute(x, y, seed);
    }
}
