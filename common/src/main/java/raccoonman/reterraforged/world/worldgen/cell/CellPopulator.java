package raccoonman.reterraforged.world.worldgen.cell;

public interface CellPopulator {
    void apply(Cell cell, float x, float z);

    default boolean generateRiver(Cell cell, float x, float z) {
    	return false;
    }

    default boolean generateLake(Cell cell, float x, float z) {
    	return false;
    }
    
    default boolean generateSwamp(Cell cell, float x, float z) {
    	return false;
    }
}
