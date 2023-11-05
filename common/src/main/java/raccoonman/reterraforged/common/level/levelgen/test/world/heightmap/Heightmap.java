package raccoonman.reterraforged.common.level.levelgen.test.world.heightmap;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.test.module.Blender;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.ErosionLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.RidgeLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.Continent;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.ContinentLerper2;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.ContinentLerper3;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.Terrain;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator.TerrainPopulator;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.provider.TerrainProvider;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.region.RegionLerper;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.region.RegionModule;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.region.RegionSelector;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.special.VolcanoPopulator;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public class Heightmap implements Populator {
	protected Continent continentGenerator;
    protected Populator regionModule;
    private Levels levels;
    private Climate climate;
    private Populator root;
    private TerrainProvider terrainProvider;
    private float terrainFrequency;
    
    private Noise mountainNoise;
    private float mountainChance;
    
    public Heightmap(GeneratorContext context) {
        Preset settings = context.settings;
        WorldSettings world = context.settings.world();
        ControlPoints controlPoints = ControlPoints.make(world.controlPoints);
        Seed regionSeed = context.seed.offset(789124);
        Seed regionWarp = context.seed.offset(8934);
        int regionWarpScale = 400;
        int regionWarpStrength = 200;
        RegionConfig regionConfig = new RegionConfig(regionSeed.get(), context.settings.terrain().general.terrainRegionSize, Source.simplex(regionWarp.next(), regionWarpScale, 1), Source.simplex(regionWarp.next(), regionWarpScale, 1), regionWarpStrength);
        this.levels = context.levels;
        this.terrainFrequency = 1.0F / settings.terrain().general.globalHorizontalScale;
        this.regionModule = new RegionModule(regionConfig);
        Seed mountainSeed = context.seed.offset(context.settings.terrain().general.terrainSeedOffset);
        Noise mountainShapeBase = Source.cellEdge(mountainSeed.next(), 1000, EdgeFunction.DISTANCE_2_ADD).warp(mountainSeed.next(), 333, 2, 250.0);
        Noise mountainShape = mountainShapeBase.curve(Interpolation.CURVE3).clamp(0.0, 0.9).map(0.0, 1.0);
        this.terrainProvider = context.terrainFactory.create(context, regionConfig, this);
        Populator terrainRegions = new RegionSelector(this.terrainProvider.getPopulators());
        Populator terrainRegionBorders = TerrainPopulator.of(TerrainType.FLATS, this.terrainProvider.getLandforms().getLandBase(), this.terrainProvider.getLandforms().plains(context.seed), RidgeLevel.LOW_SLICE.source(), ErosionLevel.LEVEL_4.source(), settings.terrain().steppe);
        Populator terrain = new RegionLerper(terrainRegionBorders, terrainRegions);
        Populator mountains = this.register(TerrainType.MOUNTAIN_CHAIN, this.terrainProvider.getLandforms().getLandBase(), this.terrainProvider.getLandforms().mountains(mountainSeed), RidgeLevel.HIGH_SLICE.source(), ErosionLevel.LEVEL_6.source(), settings.terrain().mountains);
        this.continentGenerator = world.continent.continentType.create(context.seed, context);
        this.climate = new Climate(this.continentGenerator, context);
        Populator land = new Blender(mountainShape, terrain, mountains, 0.3F, 0.8F, 0.575F);
        
        Noise oceanRidge = Source.ZERO;
        Noise oceanErosion = Source.ZERO;
        ContinentLerper3 oceans = new ContinentLerper3(this.register(TerrainType.DEEP_OCEAN, this.terrainProvider.getLandforms().deepOcean(context.seed.next()), oceanRidge, oceanErosion), this.register(TerrainType.SHALLOW_OCEAN, Source.constant(context.levels.water(-7)), oceanRidge, oceanErosion), this.register(TerrainType.COAST, Source.constant(context.levels.water), oceanRidge, oceanErosion), controlPoints.deepOcean(), controlPoints.shallowOcean(), controlPoints.coast());
        this.root = new ContinentLerper2(oceans, land, controlPoints.shallowOcean(), controlPoints.inland());
        
        this.mountainNoise = Source.perlin(context.seed.next(), 80, 2).scale(this.levels.scale(10));
        this.mountainChance = settings.miscellaneous().mountainBiomeUsage;
    }
    
    public TerrainProvider getTerrainProvider() {
        return this.terrainProvider;
    }
    
    public Populator getRegionModule() {
        return this.regionModule;
    }
    
    public Levels getLevels() {
        return this.levels;
    }
    
    @Override
    public void apply(Cell cell, float x, float z, int seed) {
        this.applyBase(cell, x, z);
        this.applyRivers(cell, x, z);
        this.applyClimate(cell, x, z);
    }
    
    public void applyBase(Cell cell, float x, float z) {
        cell.terrain = TerrainType.FLATS;
        cell.ridgeLevel = RidgeLevel.MID_SLICE.mid();
        cell.erosionLevel = ErosionLevel.LEVEL_4.mid();
        
        this.continentGenerator.apply(cell, x, z, 0);
        this.regionModule.apply(cell, x, z, 0);
        x *= this.terrainFrequency;
        z *= this.terrainFrequency;
        this.root.apply(cell, x, z, 0);
    }
    
    public void applyRivers(Cell cell, float x, float z, Rivermap rivermap) {
        rivermap.apply(cell, x, z, 0);
        VolcanoPopulator.modifyVolcanoType(cell, this.levels);
    }
    
    public void applyClimate(Cell cell, float x, float z) {
        this.climate.apply(cell, x, z, 0);

        if(cell.terrain.isMountain() && cell.macroBiomeId < this.mountainChance) {
        	float mountainNoise = this.mountainNoise.compute(x, z, 0);
    		cell.ridgeLevel = RidgeLevel.PEAKS.mid();
        	if(this.isAbove(cell, this.levels.ground(86), mountainNoise)) {
        		if(this.isAbove(cell, this.levels.ground(111), mountainNoise)) {
            		cell.erosionLevel = ErosionLevel.LEVEL_0.mid();
            	} else {
            		cell.ridgeLevel = RidgeLevel.HIGH_SLICE.mid();
            		cell.erosionLevel = ErosionLevel.LEVEL_1.mid();
            	}
        	} else {
        		cell.erosionLevel = ErosionLevel.LEVEL_2.mid();
        	}
        }
        
        if(cell.biomeRegionId > 0.5F) {
        	//this doubles as weirdness noise,
        	//so inverting it means thats
        	//the PV level stays the same but 
        	//weird biomes get used instead
        	cell.ridgeLevel = -cell.ridgeLevel;
        }
    }
    
    private boolean isAbove(Cell cell, float height, float noise) {
    	return cell.value > height || (cell.value + this.levels.scale(10) >= height && cell.value + noise > height);
    }
    
    public Climate getClimate() {
        return this.climate;
    }
    
    public Continent getContinent() {
        return this.continentGenerator;
    }
    
    public Rivermap getRivermap(int x, int z) {
        return this.continentGenerator.getRivermap(x, z, 0);
    }
    
    public Populator getPopulator(Terrain terrain, int id) {
        return this.terrainProvider.getPopulator(terrain, id);
    }
    
    private void applyRivers(Cell cell, float x, float z) {
        this.applyRivers(cell, x, z, this.continentGenerator.getRivermap(cell, 0));
    }
    
    private TerrainPopulator register(Terrain terrain, Noise variance, Noise ridge, Noise erosion) {
        TerrainPopulator populator = TerrainPopulator.of(terrain, variance, ridge, erosion);
        this.terrainProvider.registerMixable(populator);
        return populator;
    }
    
    private TerrainPopulator register(Terrain terrain, Noise base, Noise variance, Noise ridge, Noise erosion, TerrainSettings.Terrain settings) {
        TerrainPopulator populator = TerrainPopulator.of(terrain, base, variance, ridge, erosion, settings);
        this.terrainProvider.registerMixable(populator);
        return populator;
    }
}
