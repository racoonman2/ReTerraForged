package raccoonman.reterraforged.world.worldgen.cell.terrain;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.List;

public class TerrainType {
    private static final Object LOCK = new Object();
    private static final List<Terrain> REGISTRY = new CopyOnWriteArrayList<>();
    public static final Terrain NONE = register("none", TerrainCategory.NONE);
    public static final Terrain DEEP_OCEAN = register("deep_ocean", TerrainCategory.DEEP_OCEAN);
    public static final Terrain SHALLOW_OCEAN = register("ocean", TerrainCategory.SHALLOW_OCEAN);
    public static final Terrain COAST = register("coast", TerrainCategory.COAST);
    public static final Terrain BEACH = register("beach", TerrainCategory.BEACH);
    public static final Terrain RIVER = register("river", TerrainCategory.RIVER);
    public static final Terrain LAKE = register("lake", TerrainCategory.LAKE);
    public static final Terrain WETLAND = registerWetlands("wetland", TerrainCategory.WETLAND);
    public static final Terrain FLATS = register("flats", TerrainCategory.FLATLAND);
    public static final Terrain BADLANDS = registerBadlands("badlands", TerrainCategory.FLATLAND);
    public static final Terrain PLATEAU = register("plateau", TerrainCategory.LOWLAND);
    public static final Terrain HILLS = register("hills", TerrainCategory.LOWLAND);
    public static final Terrain MOUNTAINS = registerMountain("mountains", TerrainCategory.HIGHLAND);
    public static final Terrain MOUNTAIN_CHAIN = registerMountain("mountain_chain", TerrainCategory.HIGHLAND);
    public static final Terrain VOLCANO = registerVolcano("volcano", TerrainCategory.HIGHLAND);
    public static final Terrain VOLCANO_PIPE = registerVolcano("volcano_pipe", TerrainCategory.HIGHLAND);
    
    public static void forEach(Consumer<Terrain> action) {
        TerrainType.REGISTRY.forEach(action);
    }
    
    public static Optional<Terrain> find(Predicate<Terrain> filter) {
        return TerrainType.REGISTRY.stream().filter(filter).findFirst();
    }
    
    public static Terrain getOrCreate(String name, Terrain parent) {
        if (parent == null || parent == TerrainType.NONE) {
            return TerrainType.NONE;
        }
        Terrain current = get(name);
        if (current != null) {
            return current;
        }
        return register(new Terrain(0, name, parent));
    }
    
    public static Terrain get(String name) {
        for (Terrain terrain : TerrainType.REGISTRY) {
            if (terrain.getName().equalsIgnoreCase(name)) {
                return terrain;
            }
        }
        return null;
    }
    
    public static Terrain get(int id) {
        synchronized (TerrainType.LOCK) {
            if (id >= 0 && id < TerrainType.REGISTRY.size()) {
                return TerrainType.REGISTRY.get(id);
            }
            return TerrainType.NONE;
        }
    }
    
    public static Terrain register(Terrain instance) {
        synchronized (TerrainType.LOCK) {
            Terrain current = get(instance.getName());
            if (current != null) {
                return current;
            }
            Terrain terrain = instance.withId(TerrainType.REGISTRY.size());
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    public static Terrain registerComposite(Terrain a, Terrain b) {
        if (a == b) {
            return a;
        }
        synchronized (TerrainType.LOCK) {
            Terrain min = (a.getId() < b.getId()) ? a : b;
            Terrain max = (a.getId() > b.getId()) ? a : b;
            Terrain current = get(min.getName() + "-" + max.getName());
            if (current != null) {
                return current;
            }
            CompositeTerrain mix = new CompositeTerrain(TerrainType.REGISTRY.size(), min, max);
            TerrainType.REGISTRY.add(mix);
            return mix;
        }
    }
    
    private static Terrain register(String name, TerrainCategory type) {
        synchronized (TerrainType.LOCK) {
            Terrain terrain = new Terrain(TerrainType.REGISTRY.size(), name, type);
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    private static Terrain registerWetlands(String name, TerrainCategory type) {
        synchronized (TerrainType.LOCK) {
            Terrain terrain = new ConfiguredTerrain(TerrainType.REGISTRY.size(), name, type, true);
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    private static Terrain registerBadlands(String name, TerrainCategory type) {
        synchronized (TerrainType.LOCK) {
            Terrain terrain = new ConfiguredTerrain(TerrainType.REGISTRY.size(), name, type, 0.3f);
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    private static Terrain registerMountain(String name, TerrainCategory type) {
        synchronized (TerrainType.LOCK) {
            Terrain terrain = new ConfiguredTerrain(TerrainType.REGISTRY.size(), name, type, true, true);
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    private static Terrain registerVolcano(String name, TerrainCategory type) {
        synchronized (TerrainType.LOCK) {
            Terrain terrain = new ConfiguredTerrain(TerrainType.REGISTRY.size(), name, type, true, true) {
                @Override
                public boolean isVolcano() {
                    return true;
                }
                
                @Override
                public boolean overridesCoast() {
                    return true;
                }
            };
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
}
