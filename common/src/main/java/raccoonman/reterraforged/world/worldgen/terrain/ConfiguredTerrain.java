package raccoonman.reterraforged.world.worldgen.terrain;

public class ConfiguredTerrain extends Terrain {
    private float erosionModifier;
    private boolean isMountain;
    private boolean overridesRiver;
    
    ConfiguredTerrain(int id, String name, TerrainCategory category, float erosionModifier) {
        this(id, name, category, erosionModifier, category.isMountain(), category.overridesRiver());
    }
    
    ConfiguredTerrain(int id, String name, TerrainCategory category, boolean overridesRiver) {
        this(id, name, category, category.erosionModifier(), category.isMountain(), overridesRiver);
    }
    
    ConfiguredTerrain(int id, String name, TerrainCategory category, boolean isMountain, boolean overridesRiver) {
        this(id, name, category, category.erosionModifier(), isMountain, overridesRiver);
    }
    
    ConfiguredTerrain(int id, String name, TerrainCategory category, float erosionModifier, boolean isMountain, boolean overridesRiver) {
        super(id, name, category);
        this.erosionModifier = erosionModifier;
        this.isMountain = isMountain;
        this.overridesRiver = overridesRiver;
    }
    
    @Override
    public boolean overridesRiver() {
        return this.overridesRiver;
    }
    
    @Override
    public boolean isMountain() {
        return this.isMountain;
    }
    
    @Override
    public float erosionModifier() {
        return this.erosionModifier;
    }
}
