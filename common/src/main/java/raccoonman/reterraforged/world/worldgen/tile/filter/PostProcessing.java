package raccoonman.reterraforged.world.worldgen.tile.filter;

import raccoonman.reterraforged.world.worldgen.heightmap.Heightmap;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.tile.Size;
import raccoonman.reterraforged.world.worldgen.tile.Tile;

public record PostProcessing(Heightmap heightmap, Levels levels) implements Filter {
     
	@Override
    public void apply(Tile tile, int seedX, int seedZ, int iterations) {
        Size size = tile.getBlockSize();
        int total = size.total();
        
//        int quartTotal = QuartPos.fromBlock(total);
//        for(int quartX = 0; quartX < quartTotal; quartX++) {
//        	for(int quartZ = 0; quartZ < quartTotal; quartZ++) {
//        		int startX = QuartPos.toBlock(quartX);
//        		int startZ = QuartPos.toBlock(quartZ);
//        		int endX = QuartPos.toBlock(quartX + 1);
//        		int endZ = QuartPos.toBlock(quartZ + 1);
//        		
//        		boolean isBeach = false;
//        		
//        		beachTest:
//        		for(int x = startX; x < endX; x++) {
//        			for(int z = startZ; z < endZ; z++) {
//        				Cell cell = map.getCellRaw(x, z);
//        				if(cell.terrain.getDelegate() == TerrainCategory.BEACH || ((cell.terrain.isShallowOcean() || cell.terrain.isDeepOcean()) && cell.height > this.levels.water)) {            				
//        					isBeach = true;
//        					break beachTest;
//        				}
//            		}
//        		}
//        		
//        		if(isBeach) {
//            		for(int x = startX; x < endX; x++) {
//            			for(int z = startZ; z < endZ; z++) {
//            				map.getCellRaw(x, z).terrain = TerrainType.BEACH;
//            			}
//            		}
//        		}
//            }
//        }
//     	
        Size chunkSize = tile.getChunkSize();
        int chunkTotal = chunkSize.total();
        for(int chunkX = 0; chunkX < chunkTotal; chunkX++) {
            for(int chunkZ = 0; chunkZ < chunkTotal; chunkZ++) {
            	Tile.Chunk chunk = tile.getChunkReader(chunkX, chunkZ);
            	
	        	for (int dz = 0; dz < 16; dz++) {
	        		for (int dx = 0; dx < 16; dx++) {
	        			chunk.updateHighestPoint(chunk.getCell(dx, dz));
	        		}
	        	}
            }
        }
    }
}
