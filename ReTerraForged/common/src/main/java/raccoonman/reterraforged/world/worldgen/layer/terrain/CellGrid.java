package raccoonman.reterraforged.world.worldgen.layer.terrain;

import net.minecraft.util.Mth;

public class CellGrid {
	public static final int NEIGHBOR_RADIUS = 1;
	public static final int LENGTH = NEIGHBOR_RADIUS * 2 + 1;
	public static final int NEIGHBOR_COUNT = (LENGTH * LENGTH) - 1;
	public static final int INVALID_INDEX = Integer.MIN_VALUE;
	private static final int[] NEIGHBOR_X_OFFSETS;
	private static final int[] NEIGHBOR_Y_OFFSETS;
	
	protected int size;
	protected int sizeBlocks;
	protected int cellCount;
	protected int cellSize;
	protected int cellArea;
	protected int maxCellCoord;
	private int zBorderCoord;
	
	private int[] neighborOffsets;
	private float[] neighborDistances;

	public CellGrid(int size, int cellSize) {
		this.size = size;
		this.sizeBlocks = size * cellSize;
		this.cellCount = size * size;
		this.cellSize = cellSize;
		this.cellArea = cellSize * cellSize;
		this.maxCellCoord = size - 1;
		this.zBorderCoord = this.cellCount - this.size;
		
		this.neighborOffsets = new int[this.cellCount];
		this.neighborDistances = new float[this.cellCount];
		this.fillDistanceArray(1);//distanceScale);
	}

	public int size() {
		return this.size;
	}
	
	public int sizeBlocks() {
		return this.sizeBlocks;
	}

	public int cellCount() {
		return this.cellCount;
	}
	
	public int cellSize() {
		return this.cellSize;
	}
	
	public int cellArea() {
		return this.cellArea;
	}
	
	public int neighborCellIndex(int cellIndex, int neighborIndex) {
		return cellIndex + this.neighborOffsets[neighborIndex];
	}
	
	public float neighborDistance(int neighborIndex) {
		return this.neighborDistances[neighborIndex];
	}
	
	public int neighborCellX(int cellX, int neighborIndex) {
		return cellX + neighborXOffset(neighborIndex);
	}
	
	public int neighborCellY(int cellY, int neighborIndex) {
		return cellY + neighborYOffset(neighborIndex);
	}
	
	public int blockToCell(int blockCoord) {
		return Math.floorDiv(blockCoord, this.cellSize);
	}
	
	public int cellToBlock(int cellCoord) {
		return cellCoord * this.cellSize;
	}
	
	public int cellX(int cellIndex) {
		return cellIndex % this.size;
	}
	
	public int cellY(int cellIndex) {
		return cellIndex / this.size;
	}
	
	public int cellIndex(int cellX, int cellY) {
		return cellY * this.size + cellX;
	}

	public boolean contains(int cellIndex) {
		return cellIndex >= 0 && cellIndex < this.cellCount;
	}
	
	public boolean contains(int cellX, int cellY) {
		return cellX >= 0 && cellX < this.size && cellY >= 0 && cellY < this.size;
	}
	
	public boolean isBorder(int cellIndex) {
		int cellX;
		return cellIndex < this.size || cellIndex >= this.zBorderCoord || (cellX = this.cellX(cellIndex)) == 0 || cellX == this.maxCellCoord;
	}
	
	public boolean isBorder(int cellX, int cellY) {
		return cellX == 0 || cellY == 0 || cellX == this.maxCellCoord || cellY == this.maxCellCoord;
	}
	
	private void fillDistanceArray(int distanceScale) {
		int index = 0;
		for(int offsetX = -NEIGHBOR_RADIUS; offsetX <= NEIGHBOR_RADIUS; offsetX++) {
			for(int offsetY = -NEIGHBOR_RADIUS; offsetY <= NEIGHBOR_RADIUS; offsetY++) {
				if(offsetX == 0 && offsetY == 0) {
					continue;
				}
			
				this.neighborOffsets[index] = offsetY * this.size + offsetX;
				int blockX = offsetX * (this.cellSize * distanceScale);
				int blockZ = offsetY * (this.cellSize * distanceScale);
				this.neighborDistances[index] = Mth.sqrt(blockX * blockX + blockZ * blockZ);
				index++;
			}
		}
	}
	
	public static int neighborXOffset(int neighborIndex) {
		return NEIGHBOR_X_OFFSETS[neighborIndex];
	}
	
	public static int neighborYOffset(int neighborIndex) {
		return NEIGHBOR_Y_OFFSETS[neighborIndex];
	}
	
	public static boolean isValidIndex(int cellIndex) {
		return cellIndex != INVALID_INDEX;
	}
	
	static {
		NEIGHBOR_X_OFFSETS = new int[NEIGHBOR_COUNT];
		NEIGHBOR_Y_OFFSETS = new int[NEIGHBOR_COUNT];
		
		int index = 0;
		for(int offsetX = -NEIGHBOR_RADIUS; offsetX <= NEIGHBOR_RADIUS; offsetX++) {
			for(int offsetY = -NEIGHBOR_RADIUS; offsetY <= NEIGHBOR_RADIUS; offsetY++) {
				if(offsetX == 0 && offsetY == 0) {
					continue;
				}
				
				NEIGHBOR_X_OFFSETS[index] = offsetX;
				NEIGHBOR_Y_OFFSETS[index] = offsetY;
				index++;
			}
		}
	}
}