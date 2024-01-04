package raccoonman.reterraforged.world.worldgen.util;

public class Seed {
    private int root;
    private int value;
    
    public Seed(int value) {
        this.root = value;
        this.value = value;
    }
    
    public int next() {
        return this.value++;
    }
    
    public int get() {
        return this.value;
    }
    
    public int root() {
        return this.root;
    }
    
    public Seed split() {
        return new Seed(this.root);
    }
    
    public Seed offset(int offset) {
        return new Seed(this.root + offset);
    }
}
