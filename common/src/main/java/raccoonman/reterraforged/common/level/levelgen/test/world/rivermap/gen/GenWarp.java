package raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.gen;

import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public class GenWarp {
    public static final GenWarp EMPTY = new GenWarp();
    public final Domain lake;
    public final Domain river;
    
    private GenWarp() {
        this.lake = Domain.DIRECT;
        this.river = Domain.DIRECT;
    }
    
    public GenWarp(int seed, int continentScale) {
        this.lake = Domain.warp(++seed, 200, 1, 300.0).add(Domain.warp(++seed, 50, 2, 50.0));
        this.river = Domain.warp(++seed, 95, 1, 25.0).add(Domain.warp(++seed, 16, 1, 5.0));
    }
}
