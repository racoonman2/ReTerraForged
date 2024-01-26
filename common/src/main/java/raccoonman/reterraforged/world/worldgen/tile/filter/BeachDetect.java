package raccoonman.reterraforged.world.worldgen.tile.filter;

import raccoonman.reterraforged.data.preset.settings.WorldSettings.ControlPoints;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.tile.Size;

public record BeachDetect(Levels levels, ControlPoints transition) implements Filter {
    
    @Override
    public void apply(Filterable map, int seedX, int seedZ, int iterations) {
        Size size = map.getBlockSize();
        int total = size.total();
        
        for(int x = 0; x < total; x++) {
        	for(int z = 0; z < total; z++) {
        		Cell cell = map.getCellRaw(x, z);
	        	if (cell.terrain.isCoast() && !cell.terrain.isWetland() && cell.continentEdge < this.transition.beach) {
	                Cell n = map.getCellRaw(x, z - 8);
	                Cell s = map.getCellRaw(x, z + 8);
	                Cell e = map.getCellRaw(x + 8, z);
	                Cell w = map.getCellRaw(x - 8, z);
	                float gx = this.grad(e, w, cell);
	                float gz = this.grad(n, s, cell);
	                float d2 = gx * gx + gz * gz;
	                if (d2 < 0.275F) {
                		map.getCellRaw(x, z).terrain = TerrainType.BEACH;
	                }
	            }
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
        return (a.height - b.height) / distance;
    }
    
    public static BeachDetect make(GeneratorContext ctx) {
    	Levels levels = ctx.levels;
    	ControlPoints transition = ctx.preset.world().controlPoints;
    	return new BeachDetect(levels, transition);
    }
}
