package raccoonman.reterraforged.world.worldgen.heightmap;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import raccoonman.reterraforged.data.preset.PresetTerrainNoise;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.data.preset.settings.TerrainSettings;
import raccoonman.reterraforged.data.preset.settings.WorldSettings;
import raccoonman.reterraforged.data.preset.settings.WorldSettings.ControlPoints;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.biome.Continentalness;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellField;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.cell.noise.CellSampler;
import raccoonman.reterraforged.world.worldgen.climate.Climate;
import raccoonman.reterraforged.world.worldgen.continent.Continent;
import raccoonman.reterraforged.world.worldgen.continent.ContinentLerper2;
import raccoonman.reterraforged.world.worldgen.continent.ContinentLerper3;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;
import raccoonman.reterraforged.world.worldgen.noise.module.Cache2d;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.rivermap.Rivermap;
import raccoonman.reterraforged.world.worldgen.terrain.MountainChainPopulator;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainCategory;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainProvider;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.terrain.populator.Populators;
import raccoonman.reterraforged.world.worldgen.terrain.populator.VolcanoPopulator;
import raccoonman.reterraforged.world.worldgen.terrain.region.RegionLerper;
import raccoonman.reterraforged.world.worldgen.terrain.region.RegionModule;
import raccoonman.reterraforged.world.worldgen.terrain.region.RegionSelector;
import raccoonman.reterraforged.world.worldgen.util.Seed;

//TODO rework this whole class
public record Heightmap(CellSampler.Provider cellProvider, CellPopulator terrain, CellPopulator region, Continent continent, Climate climate, Levels levels, ControlPoints controlPoints, float terrainFrequency, @Deprecated Noise mountainChainAlpha, @Deprecated Noise beachAlpha) { //TODO move noise fields to RegionModule
	
	//TODO move this to a factory or something instead
	public Heightmap cache() {
		//TODO map the rest of the noise as well once this is fully working
		CellSampler.Provider cellProvider = new CellSampler.Provider();
		return new Heightmap(cellProvider, this.terrain.mapNoise((noise) -> {
			if(noise instanceof Cache2d	cache2d) {
				return new Cache2d.Cached(cache2d.noise());
			}
			return cellProvider.apply(noise);
		}), this.region, this.continent, this.climate, this.levels, this.controlPoints, this.terrainFrequency, this.mountainChainAlpha, this.beachAlpha);
	}
	
	public void applyContinent(Cell cell, float x, float z) {
        this.continent.apply(cell, x, z);
	}
	
	public void applyTerrain(Cell cell, float x, float z, Rivermap rivermap) {
        cell.terrain = TerrainType.PLAINS;
        cell.riverDistance = 1.0F;
        cell.mountainChainAlpha = this.mountainChainAlpha.compute(x, z, 0);
        
        rivermap.apply(cell, x, z);
        this.region.apply(cell, x, z);
        
        float mountainMask = NoiseUtil.map(cell.mountainChainAlpha, 0.45F, 0.65F);
        cell.terrainMask = Math.min(cell.terrainMask + mountainMask, 1.0F);
        
        this.terrain.apply(cell, x * this.terrainFrequency, z * this.terrainFrequency);

        VolcanoPopulator.modifyVolcanoType(cell, this.levels);
	}
	
	public void applyClimate(Cell cell, float x, float z) {
    	cell.weirdness = -cell.weirdness;

		float riverValleyThreshold = 0.675F;
//        if(cell.riverDistance < riverValleyThreshold) {
//        	cell.erosion = 0.445F;
//        	cell.weirdness = 0.34F;
//        }
//
//        if(cell.terrain.isRiver()) {
//            cell.erosion = -0.05F;
//            cell.weirdness = -0.03F;
//        }
//
//        if(cell.terrain.isLake() && cell.height < this.levels.water) {
//            cell.erosion = Erosion.LEVEL_4.midpoint();
//            cell.weirdness = -0.03F;
//        }
//        
//        if(cell.terrain.isWetland()) {
//        	cell.erosion = Erosion.LEVEL_6.midpoint();
//        	cell.weirdness = Weirdness.VALLEY.midpoint();
//        }
        
        this.climate.apply(cell, x, z);
	}
	
	public void applyPost(Cell cell, float x, float z) {
		float deepOcean = this.controlPoints.deepOcean;
		float shallowOcean = this.controlPoints.shallowOcean;
		float beach = this.controlPoints.beach;
		float nearInland = this.controlPoints.nearInland;
		float midInland = this.controlPoints.midInland;
		float farInland = this.controlPoints.farInland;
		
		float continentNoise = cell.continentNoise;
		
		if(continentNoise <= deepOcean || cell.terrain.isDeepOcean()) {
			float alpha = NoiseUtil.map(continentNoise, 0.0F, deepOcean);
			cell.continentalness = Continentalness.DEEP_OCEAN.lerp(alpha);
		} else if(continentNoise <= shallowOcean || cell.terrain.isShallowOcean()) {
			float alpha = NoiseUtil.map(continentNoise, deepOcean, shallowOcean);
			cell.continentalness = Continentalness.OCEAN.lerp(alpha);
		} else if(continentNoise <= nearInland) {
			float alpha = NoiseUtil.map(continentNoise, shallowOcean, nearInland);
			cell.continentalness = Continentalness.NEAR_INLAND.lerp(alpha);
		} else if(continentNoise <= midInland) {
			float alpha = NoiseUtil.map(continentNoise, nearInland, midInland);
			cell.continentalness = NoiseUtil.lerp(Continentalness.MID_INLAND.min(), Continentalness.MID_INLAND.max(), alpha);
		} else {
			float alpha = NoiseUtil.map(continentNoise, midInland, farInland);
			alpha = Math.min(alpha, 1.0F);
			cell.continentalness = NoiseUtil.lerp(Continentalness.FAR_INLAND.min(), Continentalness.FAR_INLAND.max(), alpha);
		}
		
		if(cell.terrain.getDelegate() == TerrainCategory.BEACH && cell.height + this.beachAlpha.compute(x, z, 0) < this.levels.water(5)) {
			float alpha = NoiseUtil.clamp(cell.continentEdge, shallowOcean, beach);
			alpha = NoiseUtil.lerp(alpha, shallowOcean, beach, 0.0F, 1.0F);
			cell.continentalness = NoiseUtil.lerp(Continentalness.COAST.min(), Continentalness.COAST.max(), alpha);
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
        Noise mountainChainAlpha = Noises.worleyEdge(mountainSeed.next(), 1000, EdgeFunction.DISTANCE_2_ADD, DistanceFunction.EUCLIDEAN);
        mountainChainAlpha = Noises.warpPerlin(mountainChainAlpha, mountainSeed.next(), 333, 2, 250.0F);
        mountainChainAlpha = Noises.curve(mountainChainAlpha, Interpolation.CURVE3);
        mountainChainAlpha = Noises.clamp(mountainChainAlpha, 0.0F, 0.9F);
        mountainChainAlpha = Noises.map(mountainChainAlpha, 0.0F, 1.0F);

        int groundVariance = 25;
		Noise ground = Noises.cell(CellField.CONTINENT_NOISE);
		ground = Noises.clamp(ground, controlPoints.coast, controlPoints.farInland);
		ground = Noises.map(ground, 0.0F, 1.0F);
		ground = Noises.mul(ground, levels.scale(groundVariance));
		ground = Noises.add(ground, levels.ground);
		
        CellPopulator terrainRegions = new RegionSelector(TerrainProvider.generateTerrain(ctx.seed, terrainSettings, regionConfig, levels, ground));
        CellPopulator terrainRegionBorders = Populators.makeBorder(ctx.seed, ground, terrainSettings.plains, terrainSettings.steppe, globalVerticalScale);
        
        CellPopulator terrainBlend = new RegionLerper(terrainRegionBorders, terrainRegions);
        CellPopulator mountains = Populators.makeMountainChain(mountainSeed, ground, terrainSettings.mountains, globalVerticalScale, general.fancyMountains);
        Continent continent = worldSettings.continent.continentType.create(ctx.seed, ctx);
        Climate climate = Climate.make(continent, ctx);
        CellPopulator land = new MountainChainPopulator(terrainBlend, mountains, 0.3F, 0.8F);
        
        CellPopulator deepOcean = Populators.makeDeepOcean(ctx.seed.next(), levels.water);
        CellPopulator shallowOcean = Populators.makeShallowOcean(ctx.levels);
        CellPopulator coast = Populators.makeCoast(ctx.levels);
        
        CellPopulator oceans = new ContinentLerper3(deepOcean, shallowOcean, coast, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast);
        CellPopulator terrain = new ContinentLerper2(oceans, land, controlPoints.shallowOcean, controlPoints.nearInland);
       
        Noise beachNoise = Noises.perlin2(ctx.seed.next(), 20, 1);
        beachNoise = Noises.mul(beachNoise, ctx.levels.scale(5));
        
        CellSampler.Provider cellProvider = new CellSampler.Provider();
        return new Heightmap(cellProvider, terrain.mapNoise(cellProvider), region, continent, climate, levels, controlPoints, terrainFrequency, mountainChainAlpha, beachNoise);
	}
}
