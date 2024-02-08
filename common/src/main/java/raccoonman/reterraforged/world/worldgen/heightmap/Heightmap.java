package raccoonman.reterraforged.world.worldgen.heightmap;

import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.data.preset.settings.TerrainSettings;
import raccoonman.reterraforged.data.preset.settings.WorldSettings;
import raccoonman.reterraforged.data.preset.settings.WorldSettings.ControlPoints;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.biome.Continentalness;
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
        cell.terrain = TerrainType.PLAINS;
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
        if(cell.riverDistance < riverValleyThreshold) {
        	cell.erosion = 0.4F;
        	cell.weirdness = 0.1F;// NoiseUtil.lerp(cell.weirdness, Weirdness.LOW_SLICE_NORMAL_DESCENDING.midpoint(), 1.0F - cell.riverDistance);
//        	cell.erosion = NoiseUtil.lerp(cell.erosion, Erosion.LEVEL_3.mid(), 1.0F - cell.riverMask);
        }

        if(cell.terrain.isRiver()) {
        	cell.weirdness = 0.0F;
        }
//        
        if(cell.terrain.isLake() && cell.height < this.levels.water) {
            cell.erosion = Erosion.LEVEL_4.midpoint();
            cell.weirdness = -0.03F;
        }
        this.climate.apply(cell, x, z);

		float deepOcean = this.controlPoints.deepOcean;
		float shallowOcean = this.controlPoints.shallowOcean;
		float beach = this.controlPoints.beach;
		float coast = this.controlPoints.coast;
		float nearInland = this.controlPoints.nearInland;
		float midInland = this.controlPoints.midInland;
		float farInland = this.controlPoints.farInland;
		
		float continentalness = cell.continentalness;
		
		if(continentalness <= deepOcean) {
			float alpha = NoiseUtil.map(continentalness, 0.0F, deepOcean);
			cell.continentalness = Continentalness.DEEP_OCEAN.lerp(alpha);
		} else if(continentalness <= shallowOcean) {
			float alpha = NoiseUtil.map(continentalness, deepOcean, shallowOcean);
			cell.continentalness = Continentalness.OCEAN.lerp(alpha);
		} else if(continentalness <= nearInland) {
			float alpha = NoiseUtil.map(continentalness, shallowOcean, nearInland);
			cell.continentalness = Continentalness.NEAR_INLAND.lerp(alpha);
		} else if(continentalness <= midInland) {
			float alpha = NoiseUtil.map(continentalness, nearInland, midInland);
			cell.continentalness = NoiseUtil.lerp(Continentalness.MID_INLAND.min(), Continentalness.MID_INLAND.max(), alpha);
		} else {
			float alpha = NoiseUtil.map(continentalness, midInland, farInland);
			alpha = Math.min(alpha, 1.0F);
			cell.continentalness = NoiseUtil.lerp(Continentalness.FAR_INLAND.min(), Continentalness.FAR_INLAND.max(), alpha);
		}
	}
	
	public static Heightmap make(GeneratorContext ctx) {
        Preset preset = ctx.preset;
        WorldSettings worldSettings = ctx.preset.world();
        ControlPoints controlPoints = worldSettings.controlPoints;

        TerrainSettings terrainSettings = preset.terrain();
        TerrainSettings.General general = terrainSettings.general;
        float globalVerticalScale = general.globalVerticalScale;
        
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

        CellPopulator terrainRegions = new RegionSelector(TerrainProvider.generateTerrain(ctx.seed, terrainSettings, regionConfig, levels, ground));
//        terrainRegions = Populators.makeMountainCliffs(TerrainType.MOUNTAIN_CLIFFS, new Seed(0), ground, terrainSettings.mountains, globalVerticalScale);
        CellPopulator terrainRegionBorders = Populators.makeBorder(ctx.seed, ground, terrainSettings.plains, terrainSettings.steppe, globalVerticalScale);
        
        CellPopulator terrainBlend = new RegionLerper(terrainRegionBorders, terrainRegions);
        CellPopulator mountains = Populators.makeMountainChain(mountainSeed, ground, terrainSettings.mountains, terrainSettings.general.legacyWorldGen ? globalVerticalScale : globalVerticalScale * terrainSettings.mountains.verticalScale, general.fancyMountains, general.legacyWorldGen);
        Continent continent = worldSettings.continent.continentType.create(ctx.seed, ctx);
        Climate climate = Climate.make(continent, ctx);
        CellPopulator land = new Blender(mountainShape, terrainBlend, mountains, 0.3F, 0.8F, 0.575F);
        
        CellPopulator deepOcean = Populators.makeDeepOcean(ctx.seed.next(), levels.water);
        CellPopulator shallowOcean = Populators.makeShallowOcean(ctx.levels);
        CellPopulator coast = Populators.makeCoast(ctx.levels);
        
        CellPopulator oceans = new ContinentLerper3(deepOcean, shallowOcean, coast, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast);
        CellPopulator terrain = new ContinentLerper2(oceans, land, controlPoints.shallowOcean, controlPoints.nearInland);
       
        Noise beachNoise = Noises.perlin2(ctx.seed.next(), 20, 1);
        beachNoise = Noises.mul(beachNoise, ctx.levels.scale(5));
        return new Heightmap(terrain, region, continent, climate, levels, controlPoints, terrainFrequency, beachNoise);
	}
}
