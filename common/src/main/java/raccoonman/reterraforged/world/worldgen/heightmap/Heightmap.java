package raccoonman.reterraforged.world.worldgen.heightmap;

import raccoonman.reterraforged.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.data.preset.Preset;
import raccoonman.reterraforged.data.preset.TerrainSettings;
import raccoonman.reterraforged.data.preset.WorldSettings;
import raccoonman.reterraforged.data.preset.WorldSettings.ControlPoints;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.biome.Erosion;
import raccoonman.reterraforged.world.worldgen.biome.Weirdness;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.climate.Climate;
import raccoonman.reterraforged.world.worldgen.continent.Continent;
import raccoonman.reterraforged.world.worldgen.continent.ContinentLerper2;
import raccoonman.reterraforged.world.worldgen.continent.ContinentLerper3;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.rivermap.Rivermap;
import raccoonman.reterraforged.world.worldgen.terrain.Blender;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.terrain.populator.Populators;
import raccoonman.reterraforged.world.worldgen.terrain.populator.VolcanoPopulator;
import raccoonman.reterraforged.world.worldgen.terrain.provider.TerrainProvider;
import raccoonman.reterraforged.world.worldgen.terrain.region.RegionLerper;
import raccoonman.reterraforged.world.worldgen.terrain.region.RegionModule;
import raccoonman.reterraforged.world.worldgen.terrain.region.RegionSelector;
import raccoonman.reterraforged.world.worldgen.util.Seed;

public record Heightmap(CellPopulator terrain, CellPopulator region, Continent continent, Climate climate, Levels levels, ControlPoints controlPoints, float terrainFrequency, Noise beachNoise) {
	
	public void apply(Cell cell, float x, float z) {
		this.applyTerrain(cell, x, z);
		this.applyRivers(cell, x, z, this.continent.getRivermap(cell));
		this.applyClimate(cell, x, z);
	}
	
	public void applyTerrain(Cell cell, float x, float z) {
        cell.terrain = TerrainType.FLATS;
        cell.beachNoise = this.beachNoise.compute(x, z, 0);
        this.continent.apply(cell, x, z);
        this.region.apply(cell, x, z);
        this.terrain.apply(cell, x * this.terrainFrequency, z * this.terrainFrequency);
	}
	
	public void applyRivers(Cell cell, float x, float z, Rivermap rivermap) {
        rivermap.apply(cell, x, z);
        VolcanoPopulator.modifyVolcanoType(cell, this.levels);
	}
	
	public void applyClimate(Cell cell, float x, float z) {
    	cell.weirdness = -cell.weirdness;
    	
		float riverValleyThreshold = 0.675F;
        if(cell.riverMask < riverValleyThreshold) {
        	cell.weirdness = NoiseUtil.lerp(cell.weirdness, Weirdness.LOW_SLICE_NORMAL_DESCENDING.mid(), 1.0F - cell.riverMask);
        	cell.erosion = NoiseUtil.lerp(cell.erosion, Erosion.LEVEL_3.mid(), 1.0F - cell.riverMask);
        }
//        if(cell.terrain.isWetland()) {
//        	cell.erosion = Erosion.LEVEL_6.mid();
//        }
        if(cell.terrain.isRiver()) {
        	cell.weirdness = 0.0F;
        }
//        
//        if(cell.terrain.isLake() && cell.height < this.levels.water) {
//            cell.erosion = Erosion.LEVEL_4.mid();
//            cell.weirdness = -0.03F;
//        }
        this.climate.apply(cell, x, z);
//
//        if(cell.riverMask >= riverValleyThreshold && cell.macroBiomeId > 0.5F) { 
//        }
	}
	
	public static Heightmap make(GeneratorContext ctx) {
        Preset preset = ctx.preset;
        WorldSettings worldSettings = ctx.preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
        ControlPoints controlPoints = worldSettings.controlPoints;

        TerrainSettings terrainSettings = preset.terrain();
        TerrainSettings.General general = terrainSettings.general;
        float globalVerticalScale = general.globalVerticalScale;
        
        MiscellaneousSettings miscellaneousSettings = preset.miscellaneous();
        
        Seed regionWarp = ctx.seed.offset(8934);
        int regionWarpScale = 400;
        int regionWarpStrength = 200;
        
        RegionConfig regionConfig = new RegionConfig(
        	ctx.seed.root() + 789124, 
        	general.terrainRegionSize, 
        	Noises.simplex(regionWarp.next(), regionWarpScale, 1),
        	Noises.simplex(regionWarp.next(), regionWarpScale, 1), 
        	regionWarpStrength
        );
        Levels levels = ctx.levels;
        float terrainFrequency = 1.0F / terrainSettings.general.globalHorizontalScale;
        CellPopulator region = new RegionModule(regionConfig);

        Seed mountainSeed = ctx.seed.offset(general.terrainSeedOffset);
        Noise mountainShape = Noises.worleyEdge(mountainSeed.next(), 1000, EdgeFunction.DISTANCE_2_ADD, DistanceFunction.EUCLIDEAN);
        mountainShape = Noises.warpPerlin(mountainShape, mountainSeed.next(), 333, 2, 250.0F);
        mountainShape = Noises.curve(mountainShape, Interpolation.CURVE3);
        mountainShape = Noises.clamp(mountainShape, 0.0F, 0.9F);
        mountainShape = Noises.map(mountainShape, 0.0F, 1.0F);

		Noise ground = Noises.constant(levels.ground);

        CellPopulator terrainRegions = new RegionSelector(TerrainProvider.generateTerrain(ctx.seed, terrainSettings, miscellaneousSettings, regionConfig, levels, ground));
        CellPopulator terrainRegionBorders = Populators.makeBorder(ctx.seed, ground, terrainSettings.plains, terrainSettings.steppe, globalVerticalScale);
        CellPopulator terrainBlend = new RegionLerper(terrainRegionBorders, terrainRegions);
        CellPopulator mountains = Populators.makeMountainChain(mountainSeed, ground, terrainSettings.mountains, miscellaneousSettings, terrainSettings.general.legacyWorldGen ? globalVerticalScale : globalVerticalScale * terrainSettings.mountains.verticalScale, general.fancyMountains, general.legacyWorldGen);
        Continent continent = worldSettings.continent.continentType.create(ctx.seed, ctx);
        Climate climate = Climate.make(continent, ctx);
        CellPopulator land = new Blender(mountainShape, terrainBlend, mountains, 0.3F, 0.8F, 0.575F);
        
        CellPopulator deepOcean = Populators.makeDeepOcean(ctx.seed.next(), levels.water);
        CellPopulator shallowOcean = Populators.makeShallowOcean(ctx.levels);
        CellPopulator coast = Populators.makeCoast(ctx.levels);
        
        CellPopulator oceans = new ContinentLerper3(deepOcean, shallowOcean, coast, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast);
        CellPopulator terrain = new ContinentLerper2(oceans, land, controlPoints.shallowOcean, controlPoints.inland);
        
        Noise beachNoise = Noises.perlin2(ctx.seed.next(), 20, 1);
        beachNoise = Noises.mul(beachNoise, ctx.levels.scale(5));
        return new Heightmap(terrain, region, continent, climate, levels, controlPoints, terrainFrequency, beachNoise);
	}
}
