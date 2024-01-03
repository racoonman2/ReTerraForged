package raccoonman.reterraforged.world.worldgen.feature.template.buffer;

import java.util.BitSet;

public class PasteBuffer implements BufferIterator {
    private int index = -1;
    private BitSet placed = null;
    private boolean recordPlaced = false;

    public void reset() {
        this.index = -1;
    }

    public void clear() {
    	this.index = -1;
        if (this.placed != null) {
        	this.placed.clear();
        }
    }

    public void setRecording(boolean recording) {
    	this.recordPlaced = recording;
    }

    @Override
    public boolean isEmpty() {
        return this.placed == null;
    }

    @Override
    public boolean next() {
    	this.index = this.placed.nextSetBit(this.index + 1);
        return this.index != -1;
    }

    @Override
    public int nextIndex() {
        return this.index;
    }

    public void record(int i) {
        if (this.recordPlaced) {
            if (this.placed == null) {
            	this.placed = new BitSet();
            }
            this.placed.set(i);
        }
    }

    public void exclude(int i) {
        if (this.recordPlaced) {
            if (this.placed != null) {
            	this.placed.set(i, false);
            }
        }
    }
}
