package raccoonman.reterraforged.common.level.levelgen.test.world.rivermap;

import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.ExpiringEntry;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Heightmap;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.gen.GenWarp;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.river.Network;

public class Rivermap implements ExpiringEntry {
    private int x;
    private int z;
    private Domain lakeWarp;
    private Domain riverWarp;
    private Network[] networks;
    private long timestamp;
    
    public Rivermap(int x, int z, Network[] networks, GenWarp warp) {
        this.timestamp = System.currentTimeMillis();
        this.x = x;
        this.z = z;
        this.networks = networks;
        this.lakeWarp = warp.lake;
        this.riverWarp = warp.river;
    }
    
    public void apply(Cell cell, float x, float z, int seed) {
        float rx = this.riverWarp.getX(x, z, seed);
        float rz = this.riverWarp.getY(x, z, seed);
        float lx = this.lakeWarp.getOffsetX(rx, rz, seed);
        float lz = this.lakeWarp.getOffsetY(rx, rz, seed);
        for (Network network : this.networks) {
            if (network.contains(rx, rz)) {
                network.carve(cell, rx, rz, lx, lz, seed);
            }
        }
    }
    
    @Override
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public static Rivermap get(Cell cell, Rivermap instance, Heightmap heightmap) {
        return get(cell.continentX, cell.continentZ, instance, heightmap);
    }
    
    public static Rivermap get(int x, int z, Rivermap instance, Heightmap heightmap) {
        if (instance != null && x == instance.getX() && z == instance.getZ()) {
            return instance;
        }
        return heightmap.getContinent().getRivermap(x, z, 0);
    }
}
