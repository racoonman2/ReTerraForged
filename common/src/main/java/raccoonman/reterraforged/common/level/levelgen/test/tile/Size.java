package raccoonman.reterraforged.common.level.levelgen.test.tile;

public record Size(int size, int mask, int border, int total, int lowerBorder, int upperBorder, int arraySize) {
        
    public int mask(int i) {
        return i & this.mask;
    }
    
    public int indexOf(int x, int z) {
        return z * this.total + x;
    }
    
    public static int chunkToBlock(int i) {
        return i << 4;
    }
    
    public static int blockToChunk(int i) {
        return i >> 4;
    }
    
    public static int count(int minX, int minZ, int maxX, int maxZ) {
        int dx = maxX - minX;
        int dz = maxZ - minZ;
        return dx * dz;
    }
    
    public static Size make(int size, int border) {
    	int total = size + 2 * border;
    	return new Size(size, size - 1, border, total, border, border + size, total * total);
    }
    
    public static Size chunks(int factor, int borderChunks) {
        int chunks = 1 << factor;
        return make(chunks, borderChunks);
    }
    
    public static Size blocks(int factor, int borderChunks) {
        int chunks = 1 << factor;
        int blocks = chunks << 4;
        int borderBlocks = borderChunks << 4;
        return make(blocks, borderBlocks);
    }
}
