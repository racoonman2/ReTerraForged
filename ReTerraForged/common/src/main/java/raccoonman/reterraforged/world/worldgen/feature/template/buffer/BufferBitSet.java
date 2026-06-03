package raccoonman.reterraforged.world.worldgen.feature.template.buffer;

import java.util.BitSet;

public class BufferBitSet {
    private int minX;
    private int minY;
    private int minZ;
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private int sizeXZ;
    private BitSet bitSet;

    public void set(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.sizeX = Math.max(x1, x2) - this.minX;
        this.sizeY = Math.max(y1, y2) - this.minY;
        this.sizeZ = Math.max(z1, z2) - this.minZ;
        this.sizeXZ = this.sizeX * this.sizeZ;
        int size = this.sizeX * this.sizeY * this.sizeZ;
        if (this.bitSet == null || this.bitSet.length() < size) {
        	this.bitSet = new BitSet(size);
        } else {
        	this.bitSet.clear();
        }
    }

    public void clear() {
        if (this.bitSet != null) {
        	this.bitSet.clear();
        }
    }

    public void set(int x, int y, int z) {
        int index = this.indexOf(x - this.minX, y - this.minY, z - this.minZ);
        this.bitSet.set(index);
    }

    public void unset(int x, int y, int z) {
        int index = this.indexOf(x - this.minX, y - this.minY, z - this.minZ);
        this.bitSet.set(index, false);
    }

    public boolean test(int x, int y, int z) {
        int index = this.indexOf(x - this.minX, y - this.minY, z - this.minZ);
        if (index < 0 || index >= this.bitSet.length()) {
            return false;
        }
        return this.bitSet.get(index);
    }

    private int indexOf(int x, int y, int z) {
        return (y * this.sizeXZ) + (z * this.sizeX) + x;
    }
}
