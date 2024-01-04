package raccoonman.reterraforged.world.worldgen.cell.heightmap;

import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Tile;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.GenerationStep;
import raccoonman.reterraforged.data.worldgen.NoiseData;
import raccoonman.reterraforged.data.worldgen.TerrainTypeNoise;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.data.worldgen.preset.TerrainSettings;
import raccoonman.reterraforged.data.worldgen.preset.WorldSettings;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.cell.climate.Climate;
import raccoonman.reterraforged.world.worldgen.cell.continent.Continent;
import raccoonman.reterraforged.world.worldgen.cell.continent.ContinentLerper2;
import raccoonman.reterraforged.world.worldgen.cell.continent.ContinentLerper3;
import raccoonman.reterraforged.world.worldgen.cell.rivermap.Rivermap;
import raccoonman.reterraforged.world.worldgen.cell.terrain.Blender;
import raccoonman.reterraforged.world.worldgen.cell.terrain.Populators;
import raccoonman.reterraforged.world.worldgen.cell.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.cell.terrain.populator.VolcanoPopulator;
import raccoonman.reterraforged.world.worldgen.cell.terrain.provider.TerrainProvider;
import raccoonman.reterraforged.world.worldgen.cell.terrain.region.RegionLerper;
import raccoonman.reterraforged.world.worldgen.cell.terrain.region.RegionModule;
import raccoonman.reterraforged.world.worldgen.cell.terrain.region.RegionSelector;
import raccoonman.reterraforged.world.worldgen.noise.function.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.util.Seed;

public record Heightmap(CellPopulator terrain, CellPopulator region, Continent continent, Climate climate, Levels levels, ControlPoints controlPoints, float terrainFrequency, Noise beachNoise) {
	
	public Heightmap cache(Tile tile) {
		return new Heightmap(this.terrain, this.region, this.continent, this.climate, this.levels, this.controlPoints, this.terrainFrequency, this.beachNoise);
	}
	
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
        this.climate.apply(cell, x, z);
        
        if(cell.biomeRegionId > 0.5F) { 
        	cell.weirdness = -cell.weirdness;
        }
	}
	
	public static Heightmap make(GeneratorContext context) {
    	HolderGetter<Noise> noiseLookup = context.noiseLookup;
    	
        Preset preset = context.preset;
        WorldSettings world = context.preset.world();
        ControlPoints controlPoints = ControlPoints.make(world.controlPoints);

        TerrainSettings terrainSettings = preset.terrain();
        TerrainSettings.General general = terrainSettings.general;
        float globalVerticalScale = general.globalVerticalScale;
        
        Seed regionWarp = context.seed.offset(8934);
        int regionWarpScale = 400;
        int regionWarpStrength = 200;
        
        RegionConfig regionConfig = new RegionConfig(
        	context.seed.root() + 789124, 
        	general.terrainRegionSize, 
        	Noises.simplex(regionWarp.next(), regionWarpScale, 1),
        	Noises.simplex(regionWarp.next(), regionWarpScale, 1), 
        	regionWarpStrength
        );
        Levels levels = context.levels;
        float terrainFrequency = 1.0F / terrainSettings.general.globalHorizontalScale;
        CellPopulator region = new RegionModule(regionConfig);

        Seed mountainSeed = context.seed.offset(general.terrainSeedOffset);
        Noise mountainShape = Noises.worleyEdge(mountainSeed.next(), 1000, EdgeFunction.DISTANCE_2_ADD, DistanceFunction.EUCLIDEAN);
        mountainShape = Noises.warpPerlin(mountainShape, mountainSeed.next(), 333, 2, 250.0F);
        mountainShape = Noises.curve(mountainShape, Interpolation.CURVE3);
        mountainShape = Noises.clamp(mountainShape, 0.0F, 0.9F);
        mountainShape = Noises.map(mountainShape, 0.0F, 1.0F);

        Noise ground = NoiseData.getNoise(noiseLookup, TerrainTypeNoise.GROUND);
        
        CellPopulator terrainRegions = new RegionSelector(TerrainProvider.generateTerrain(context.seed, terrainSettings, regionConfig, levels, noiseLookup));
        CellPopulator terrainRegionBorders = Populators.makeBorder(context.seed, ground, terrainSettings.plains, terrainSettings.steppe, globalVerticalScale);
        CellPopulator terrainBlend = new RegionLerper(terrainRegionBorders, terrainRegions);
        CellPopulator mountains = Populators.makeMountainChain(mountainSeed, ground, terrainSettings.mountains, globalVerticalScale, general.fancyMountains);
        Continent continent = world.continent.continentType.create(context.seed, context);
        Climate climate = Climate.make(continent, context);
        CellPopulator land = new Blender(mountainShape, terrainBlend, mountains, 0.3F, 0.8F, 0.575F);
        
        CellPopulator deepOcean = Populators.makeDeepOcean(context.seed.next(), levels.water);
        CellPopulator shallowOcean = Populators.makeShallowOcean(context.levels);
        CellPopulator coast = Populators.makeCoast(context.levels);
        
        CellPopulator oceans = new ContinentLerper3(deepOcean, shallowOcean, coast, controlPoints.deepOcean(), controlPoints.shallowOcean(), controlPoints.coast());
        CellPopulator terrain = new ContinentLerper2(oceans, land, controlPoints.shallowOcean(), controlPoints.inland());

        Noise beachNoise = Noises.perlin2(context.seed.next(), 20, 1);
        beachNoise = Noises.mul(beachNoise, context.levels.scale(5));
        return new Heightmap(terrain, region, continent, climate, levels, controlPoints, terrainFrequency, beachNoise);
	}
}
