package raccoonman.reterraforged.world.worldgen.terrain;

import com.mojang.serialization.Codec;

public class Terrain implements ITerrain.Delegate {
    private int id;
    private String name;
    private TerrainCategory type;
    private ITerrain delegate;

    public static final Codec<Terrain> CODEC = Codec.STRING.xmap(TerrainType::get, Terrain::getName);
    
    Terrain(int id, String name, Terrain terrain) {
        this(id, name, terrain.getCategory(), terrain);
    }
    
    Terrain(int id, String name, TerrainCategory type) {
        this(id, name, type, type);
    }
    
    Terrain(int id, String name, TerrainCategory type, ITerrain delegate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.delegate = delegate;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public TerrainCategory getCategory() {
        return this.type;
    }
    
    @Override
    public ITerrain getDelegate() {
        return this.delegate;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public Terrain withId(int id) {
        ITerrain delegate = (this.delegate instanceof Terrain) ? this.delegate : this;
        return new Terrain(id, this.name, this.type, delegate);
    }
}
