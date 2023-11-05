package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.ErosionLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.RidgeLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Levels;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.RegionConfig;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.LandForms;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.Terrain;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator.TerrainPopulator;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.special.VolcanoPopulator;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;

public class StandardTerrainProvider implements TerrainProvider {
    private List<TerrainPopulator> mixable;
    private List<TerrainPopulator> unmixable;
    private Map<Terrain, List<Populator>> populators;
    private Seed seed;
    private Levels levels;
    private LandForms landForms;
    private RegionConfig config;
    private TerrainSettings settings;
    private Populator defaultPopulator;
    
    public StandardTerrainProvider(GeneratorContext context, RegionConfig config, Populator defaultPopulator) {
        this.mixable = new ArrayList<>();
        this.unmixable = new ArrayList<>();
        this.populators = new HashMap<>();
        this.seed = context.seed.offset(context.settings.terrain().general.terrainSeedOffset);
        this.config = config;
        this.levels = context.levels;
        this.settings = context.settings.terrain();
        this.landForms = new LandForms(context.settings.terrain(), context.levels, this.createGroundNoise(context));
        this.defaultPopulator = defaultPopulator;
        this.init(context);
    }
    
    protected Noise createGroundNoise(GeneratorContext context) {
        return Source.constant(context.levels.ground);
    }
    
    protected void init(GeneratorContext context) {
    	Noise valley = RidgeLevel.VALLEYS.source();
    	Noise lowSlice = RidgeLevel.LOW_SLICE.source();
    	Noise midSlice = RidgeLevel.MID_SLICE.source();
    	Noise highSlice = RidgeLevel.HIGH_SLICE.source();
    	Noise peaks = RidgeLevel.PEAKS.source();
    	
    	Noise erosion1 = ErosionLevel.LEVEL_1.source();
    	Noise erosion2 = ErosionLevel.LEVEL_2.source();
    	Noise erosion3 = ErosionLevel.LEVEL_3.source();
    	Noise erosion4 = ErosionLevel.LEVEL_4.source();
    	Noise erosion5 = ErosionLevel.LEVEL_5.source();
    	Noise erosion6 = ErosionLevel.LEVEL_6.source();
    	
        this.registerMixable(TerrainType.FLATS, this.landForms.getLandBase(), this.landForms.steppe(this.seed), lowSlice, erosion4, this.settings.steppe);
        this.registerMixable(TerrainType.FLATS, this.landForms.getLandBase(), this.landForms.plains(this.seed), midSlice, erosion4, this.settings.plains);
        this.registerMixable(TerrainType.HILLS, this.landForms.getLandBase(), this.landForms.dales(this.seed), midSlice, erosion3, this.settings.dales);
        this.registerMixable(TerrainType.HILLS, this.landForms.getLandBase(), this.landForms.hills1(this.seed), midSlice, erosion4, this.settings.hills);
        this.registerMixable(TerrainType.HILLS, this.landForms.getLandBase(), this.landForms.hills2(this.seed), highSlice, erosion3, this.settings.hills);
        this.registerMixable(TerrainType.HILLS, this.landForms.getLandBase(), this.landForms.torridonian(this.seed), highSlice, erosion2, this.settings.torridonian);
        
        //FIXME!!!! plateaus are broken? the variance noise is just 0 for some reason
        this.registerMixable(TerrainType.PLATEAU, this.landForms.getLandBase(), this.landForms.plateau(this.seed), midSlice, erosion2, this.settings.plateau);
        this.registerMixable(TerrainType.BADLANDS, this.landForms.getLandBase(), this.landForms.badlands(this.seed), midSlice, erosion3, this.settings.badlands);
        this.registerUnMixable(TerrainType.BADLANDS, this.landForms.getLandBase(), this.landForms.badlands(this.seed), midSlice, erosion2, this.settings.badlands);
        this.registerUnMixable(TerrainType.MOUNTAINS, this.landForms.getLandBase(), this.landForms.mountains(this.seed), midSlice, erosion4, this.settings.mountains);
        this.registerUnMixable(TerrainType.MOUNTAINS, this.landForms.getLandBase(), this.landForms.mountains2(this.seed), midSlice, erosion4, this.settings.mountains);
        this.registerUnMixable(TerrainType.MOUNTAINS, this.landForms.getLandBase(), this.landForms.mountains3(this.seed), midSlice, erosion4, this.settings.mountains);
        this.registerUnMixable(new VolcanoPopulator(this.seed, this.config, this.levels, this.settings.volcano.weight));
    }
    
    @Override
    public void forEach(Consumer<TerrainPopulator> consumer) {
        this.mixable.forEach(consumer);
        this.unmixable.forEach(consumer);
    }
    
    @Override
    public Terrain getTerrain(String name) {
        for (Terrain terrain : this.populators.keySet()) {
            if (terrain.getName().equalsIgnoreCase(name)) {
                return terrain;
            }
        }
        return null;
    }
    
    @Override
    public void registerMixable(TerrainPopulator populator) {
        this.populators.computeIfAbsent(populator.getType(), t -> new ArrayList<>()).add(populator);
        this.mixable.add(populator);
    }
    
    @Override
    public void registerUnMixable(TerrainPopulator populator) {
        this.populators.computeIfAbsent(populator.getType(), t -> new ArrayList<>()).add(populator);
        this.unmixable.add(populator);
    }
    
    @Override
    public int getVariantCount(Terrain terrain) {
        List<Populator> list = this.populators.get(terrain);
        if (list == null) {
            return 0;
        }
        return list.size();
    }
    
    @Override
    public Populator getPopulator(Terrain terrain, int variant) {
        if (variant < 0) {
            return this.defaultPopulator;
        }
        List<Populator> list = this.populators.get(terrain);
        if (list == null) {
            return this.defaultPopulator;
        }
        if (variant >= list.size()) {
            variant = list.size() - 1;
        }
        return list.get(variant);
    }
    
    @Override
    public LandForms getLandforms() {
        return this.landForms;
    }
    
    @Override
    public List<Populator> getPopulators() {
        List<TerrainPopulator> mixed = combine(getMixable(this.mixable), this::combine);
        List<Populator> result = new ArrayList<>(mixed.size() + this.unmixable.size());
        result.addAll(mixed);
        result.addAll(this.unmixable);
        Collections.shuffle(result, new Random(this.seed.next()));
        return result;
    }
    
    public List<TerrainPopulator> getTerrainPopulators() {
        List<TerrainPopulator> populators = new ArrayList<TerrainPopulator>();
        populators.addAll(this.mixable);
        populators.addAll(this.unmixable);
        return populators;
    }
    
    private TerrainPopulator combine(TerrainPopulator tp1, TerrainPopulator tp2) {
        return this.combine(tp1, tp2, this.seed, this.config.scale() / 2);
    }
    
    private TerrainPopulator combine(TerrainPopulator tp1, TerrainPopulator tp2, Seed seed, int scale) {
        Terrain type = TerrainType.registerComposite(tp1.getType(), tp2.getType());
        Noise selector = Source.perlin(seed.next(), scale, 1).warp(seed.next(), scale / 2, 2, scale / 2.0);
        Noise variance = selector.blend(tp1.getVariance(), tp2.getVariance(), 0.5, 0.25).max(Source.constant(0.0));
        Noise ridge = selector.threshold(0.5D, tp1.getRidge(), tp2.getRidge());
        Noise erosion = selector.threshold(0.5D, tp1.getErosion(), tp2.getErosion());
        float weight = (tp1.getWeight() + tp2.getWeight()) / 2.0F;
        return new TerrainPopulator(type, this.landForms.getLandBase(), variance, ridge, erosion, weight);
    }
    
    private static <T> List<T> combine(List<T> input, BiFunction<T, T, T> operator) {
        int length = input.size();
        for (int i = 1; i < input.size(); ++i) {
            length += input.size() - i;
        }
        List<T> result = new ArrayList<T>(length);
        for (int j = 0; j < length; ++j) {
            result.add(null);
        }
        int j = 0;
        int k = input.size();
        while (j < input.size()) {
            T t1 = input.get(j);
            result.set(j, t1);
            for (int l = j + 1; l < input.size(); ++l, ++k) {
                T t2 = input.get(l);
                T t3 = operator.apply(t1, t2);
                result.set(k, t3);
            }
            ++j;
        }
        return result;
    }
    
    private static List<TerrainPopulator> getMixable(List<TerrainPopulator> input) {
        List<TerrainPopulator> output = new ArrayList<TerrainPopulator>(input.size());
        for (TerrainPopulator populator : input) {
            if (populator.getWeight() > 0.0F) {
                output.add(populator);
            }
        }
        return output;
    }
}
