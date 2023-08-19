package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.cell.CellShape;
import raccoonman.reterraforged.common.level.levelgen.cell.CellSource;
import raccoonman.reterraforged.common.level.levelgen.continent.config.RiverConfig;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.noise.Valley;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Humidity;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Temperature;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Weirdness;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.Continent;
import raccoonman.reterraforged.common.level.levelgen.noise.river.River;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.TerrainBlender;
import raccoonman.reterraforged.common.level.levelgen.settings.ClimateSettings.BiomeShape;
import raccoonman.reterraforged.common.level.levelgen.settings.ClimateSettings.RangeValue;
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings.Terrain;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.domain.Domain;
import raccoonman.reterraforged.common.noise.func.EdgeFunc;
import raccoonman.reterraforged.common.noise.func.Interpolation;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public final class RTFNoise {
	public static final ResourceKey<Noise> STEPPE = resolve("steppe");
	public static final ResourceKey<Noise> PLAINS = resolve("plains");
	public static final ResourceKey<Noise> HILLS_1 = resolve("hills_1");
	public static final ResourceKey<Noise> HILLS_2 = resolve("hills_2");
	public static final ResourceKey<Noise> DALES = resolve("dales");
	public static final ResourceKey<Noise> PLATEAU = resolve("plateau");
	public static final ResourceKey<Noise> BADLANDS = resolve("badlands");
	public static final ResourceKey<Noise> TORRIDONIAN = resolve("torridonian");
	public static final ResourceKey<Noise> MOUNTAINS_1 = resolve("mountains_1");
	public static final ResourceKey<Noise> MOUNTAINS_2 = resolve("mountains_2");
	public static final ResourceKey<Noise> MOUNTAINS_3 = resolve("mountains_3");
	public static final ResourceKey<Noise> DOLOMITES = resolve("dolomites");
	public static final ResourceKey<Noise> MOUNTAINS_RIDGE_1 = resolve("mountains_ridge_1");
	public static final ResourceKey<Noise> MOUNTAINS_RIDGE_2 = resolve("mountains_ridge_2");
	public static final ResourceKey<Noise> CLIMATE_WARP = resolve("climate_warp");
	public static final ResourceKey<Noise> CLIMATE_WARP_STRENGTH = resolve("climate_warp_strength");
	public static final ResourceKey<Noise> TEMPERATURE = resolve("temperature");
	public static final ResourceKey<Noise> HUMIDITY = resolve("humidity");
	public static final ResourceKey<Noise> CONTINENT_WARP = resolve("continent_warp");
	public static final ResourceKey<Noise> CONTINENT_WARP_STRENGTH = resolve("continent_warp_strength");
	public static final ResourceKey<Noise> CONTINENT = resolve("continent");
	public static final ResourceKey<Noise> BASE = resolve("base");
	public static final ResourceKey<Noise> RIVER_WARP = resolve("river_warp");
	public static final ResourceKey<Noise> RIVER_WARP_STRENGTH = resolve("river_warp_strength");
	public static final ResourceKey<Noise> RIVER = resolve("river");
	public static final ResourceKey<Noise> WEIRDNESS = resolve("weirdness");
	public static final ResourceKey<Noise> OCEAN = resolve("ocean");
	public static final ResourceKey<Noise> TERRAIN = resolve("terrain");
	
    public static void register(BootstapContext<Noise> ctx) {
    	HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
    	
        ctx.register(STEPPE, createSteppe());
        ctx.register(PLAINS, createPlains());
        ctx.register(HILLS_1, createHills1());
        ctx.register(HILLS_2, createHills2());
        ctx.register(DALES, createDales());
        ctx.register(PLATEAU, createPlateau());
        ctx.register(BADLANDS, createBadlands());
        ctx.register(TORRIDONIAN, createTorridonian());
        ctx.register(MOUNTAINS_1, createMountains1());
        ctx.register(MOUNTAINS_2, createMountains2(true));
        ctx.register(MOUNTAINS_3, createMountains3(true));
        ctx.register(DOLOMITES, createDolomite());
        ctx.register(MOUNTAINS_RIDGE_1, createMountains2(false));
        ctx.register(MOUNTAINS_RIDGE_2, createMountains3(false));
        
        final int climateWarpStrength = 80;
        ctx.register(CLIMATE_WARP, createClimateWarp(climateWarpStrength));
        ctx.register(CLIMATE_WARP_STRENGTH, createClimateWarpStrength(climateWarpStrength));
        ctx.register(TEMPERATURE, createTemperature(noise, BiomeShape.DEFAULT, new RangeValue(7, 6, 2, 0.0F, 0.98F, 0.05F)));
        ctx.register(HUMIDITY, createHumidity(noise, BiomeShape.DEFAULT, new RangeValue(7, 6, 1, 0.0F, 1.0F, 0.0F)));
        ctx.register(CONTINENT_WARP, createContinentWarp());
        ctx.register(CONTINENT_WARP_STRENGTH, createContinentWarpStrength());
        ctx.register(CONTINENT, createContinent(noise));
        ctx.register(BASE, createBase(noise));
        ctx.register(RIVER_WARP, createRiverWarp());
        ctx.register(RIVER_WARP_STRENGTH, createRiverWarpStrength());
        ctx.register(RIVER, createRiver(noise));
        ctx.register(WEIRDNESS, createWeirdness(noise));
        ctx.register(OCEAN, createOceanTerrain());
        ctx.register(TERRAIN, createBlendedTerrain(noise));
        
    }
    
    private static ResourceKey<Noise> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.NOISE, path);
	}

    private static final float STEPPE_HSCALE = 1.0F;
    private static final float STEPPE_VSCALE = 1.0F;
    private static final float PLAINS_HSCALE = 3.505F;
    private static final float PLAINS_VSCALE = 1.0F;
    private static final int MOUNTAINS_H = 610;
    private static final float MOUNTAINS_V = 1.3F;
    private static final int MOUNTAINS2_H = 600;
    private static final float MOUNTAINS2_V = 1.185F;
    private static final float TERRAIN_VERTICAL_SCALE = 0.96F;
    
    private static Noise createSteppe() {
    	int scaleH = Math.round(250.0F * STEPPE_HSCALE);
    	double erosionAmount = 0.45D;
    	Noise erosion = Source.build(scaleH * 2, 3).lacunarity(3.75D).perlin().alpha(erosionAmount).unique();
    	Noise warpX = Source.build(scaleH / 4, 3).lacunarity(3.0D).perlin().unique();
    	Noise warpY = Source.build(scaleH / 4, 3).lacunarity(3.0D).perlin().unique();
    	Noise module = Source.perlin(scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0F)).warp(256, 1, 200.0D).unique();
    	return module.scale(STEPPE_VSCALE).scale(0.08D).bias(-0.02D);
    }
    
    private static Noise createPlains() {
    	int scaleH = Math.round(250.0F * PLAINS_HSCALE);
    	double erosionAmount = 0.45D;
    	Noise erosion = Source.build(scaleH * 2, 3).lacunarity(3.75D).perlin().alpha(erosionAmount).unique();
    	Noise warpX = Source.build(scaleH / 4, 3).lacunarity(3.5D).perlin().unique();
    	Noise warpY = Source.build(scaleH / 4, 3).lacunarity(3.5D).perlin().unique();
    	Noise module = Source.perlin(scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0F)).unique().warp(256, 1, 256.0D).unique();
    	return module.scale(PLAINS_VSCALE).scale(0.15F * TERRAIN_VERTICAL_SCALE).bias(-0.02D);
    }
        
    private static Noise createHills1() {
    	return Source.perlin(200, 3).unique().mul(Source.billow(MOUNTAINS2_H, 3).unique().alpha(0.5)).warp(30, 3, 20.0).unique().warp(MOUNTAINS2_H, 3, 200.0).unique().scale(0.6f * TERRAIN_VERTICAL_SCALE);
    }
    
    private static Noise createHills2() {
    	return Source.cubic(128, 2).unique().mul(Source.perlin(32, 4).unique().alpha(0.075)).warp(30, 3, 20.0).unique().warp(MOUNTAINS2_H, 3, 200.0).unique().mul(Source.ridge(512, 2).unique().alpha(0.8)).scale(0.55f * TERRAIN_VERTICAL_SCALE);
    }
        
    private static Noise createPlateau() {
    	Noise valley = Source.ridge(500, 1).invert().unique().warp(100, 1, 150.0D).unique().warp(20, 1, 15.0D).unique();
    	Noise top = Source.build(150, 3).lacunarity(2.45D).ridge().unique().warp(300, 1, 150.0D).unique().warp(40, 2, 20.0D).unique().scale(0.15D).mul(valley.clamp(0.02D, 0.1D).map(0.0D, 1.0D));
    	Noise surface = Source.perlin(20, 3).unique().scale(0.05D).warp(40, 2, 20.0D).unique();
    	Noise module = valley.mul(Source.cubic(500, 1).unique().scale(0.6D).bias(0.3D)).add(top).terrace(Source.constant(0.9D), Source.constant(0.15D), Source.constant(0.35D), 4, 0.4D).add(surface);
    	return module.scale(0.475D * TERRAIN_VERTICAL_SCALE);
    }
    
    private static Noise createDales() {
    	Noise hills1 = Source.build(300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75).unique();
    	Noise hills2 = Source.build(350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25).unique();
    	Noise combined = Source.perlin(MOUNTAINS2_H, 1).unique().clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
    	Noise module = combined.pow(1.125).warp(300, 1, 100.0).unique();
    	return module.scale(0.4);
    }
        
    private static Noise createBadlands() {
    	Noise mask = Source.build(270, 3).perlin().unique().clamp(0.35, 0.65).map(0.0, 1.0);
    	Noise hills = Source.ridge(275, 4).warp(MOUNTAINS2_H, 2, 100.0).unique().warp(18, 1, 20.0).unique().mul(mask);
    	double modulation = 0.4;
    	double alpha = 1.0 - modulation;
    	Noise mod1 = hills.warp(100, 1, 50.0).scale(modulation).unique();
    	Noise lowFreq = hills.steps(4, 0.6, MOUNTAINS_V).scale(alpha).add(mod1);
    	Noise highFreq = hills.steps(10, 0.6, MOUNTAINS_V).scale(alpha).add(mod1);
    	Noise detail = lowFreq.add(highFreq);
    	Noise mod2 = hills.mul(Source.perlin(200, 3).scale(modulation).unique());
    	Noise shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
    	Noise module = shape.mul(detail.alpha(0.5));
    	return module.scale(0.55).bias(0.025);
    }
        
    private static Noise createTorridonian() {
    	Noise plains = Source.perlin(100, 3).unique().warp(300, 1, 150.0).unique().warp(20, 1, 40.0).unique().scale(0.15);
    	Noise hills = Source.perlin(150, 4).unique().warp(300, 1, 200.0).unique().warp(20, 2, 20.0).unique().boost();
    	Noise module = Source.perlin(200, 3).unique().blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(120, 1).unique().scale(0.25), Source.perlin(200, 1).unique().scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
    	return module.scale(0.5);
    }
        
    private static Noise createMountains1() {
    	int scaleH = Math.round(MOUNTAINS_H);
    	Noise module = Source.build(scaleH, 4).gain(1.15).lacunarity(2.35).ridge().unique().mul(Source.perlin(24, 4).unique().alpha(0.075)).warp(350, 1, 150.0).unique();
    	return makeFancy(module).scale(MOUNTAINS_V * TERRAIN_VERTICAL_SCALE);
    }
        
    private static Noise createMountains2(boolean fancy) {
    	Noise cell = Source.cellEdge(360, EdgeFunc.DISTANCE_2).unique().scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).unique();
    	Noise blur = Source.perlin(10, 1).unique().alpha(0.025);
    	Noise surface = Source.ridge(125, 4).unique().alpha(0.37);
    	Noise module = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
    	return (fancy ? makeFancy(module) : module).scale(MOUNTAINS2_V * TERRAIN_VERTICAL_SCALE);
    }
        
    private static Noise createMountains3(boolean fancy) {
    	Noise cell = Source.cellEdge(MOUNTAINS2_H, EdgeFunc.DISTANCE_2).unique().scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).unique();
    	Noise blur = Source.perlin(10, 1).unique().alpha(0.025);
    	Noise surface = Source.ridge(125, 4).unique().alpha(0.37);
    	Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
    	Noise module = mountains.terrace(Source.perlin(50, 1).unique().scale(0.5), Source.perlin(100, 1).unique().clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.2f, 0.45f, 24, 1);
    	return (fancy ? makeFancy(module) : module).scale(MOUNTAINS2_V * TERRAIN_VERTICAL_SCALE);
    }
        
    private static Noise makeFancy(Noise module) {
    	Domain warp = Domain.direction(Source.perlin(10, 1).unique(), Source.constant(2.0));
    	Valley erosion = new Valley(5, 0.65f, 128.0f, 0.2f, 3.1f, 0.6f, Valley.Mode.CONSTANT);
    	return erosion.wrap(Holder.direct(module)).warp(warp);
    }
        
    private static Noise createDolomite() {
    	// Valley floor terrain
    	var base = Source.simplex(80, 4).unique().scale(0.1);
    	
    	// Controls where the ridges show up
    	var shape = Source.simplex(475, 4).unique()
    		.clamp(0.3, 1.0).map(0, 1)
    		.warp(10, 2, 8)
    		.unique();
    	
    	// More gradual slopes up to the ridges
    	var slopes = shape.pow(2.2).scale(0.65).add(base);
    	
    	// Sharp ridges
    	var peaks = Source.build(400, 5).lacunarity(2.7).gain(0.6).simplexRidge()
    		.unique()
    		.clamp(0, 0.675).map(0, 1)
    		.warp(Domain.warp(Source.SIMPLEX, 40, 5, 30))
    		.alpha(0.875);

        return shape.mul(peaks).max(slopes)
        	.warp(800, 3, 300)
        	.unique()
        	.scale(0.75);
    }
    
    private static Noise createClimateWarp(int warpScale) {
    	return Source.build(warpScale, 3).lacunarity(2.4).gain(0.3).simplex();
    }

    private static Noise createClimateWarpStrength(float strength) {
    	return Source.constant(strength * 0.75);
    }
    
    private static Noise createTemperature(HolderGetter<Noise> noise, BiomeShape shape, RangeValue settings) {
        float scaler = settings.scale();
        int biomeSize = shape.biomeSize();
        float size = scaler * biomeSize;
        int scale = NoiseUtil.round(size * (1.0F / biomeSize));
        return settings.apply(new Temperature(1.0F / scale, settings.falloff()))
            .freq(1.0F / 500.0F, 1.0F / 500.0F)
            .warp(scale * 4, 2, scale * 4).unique()
        	.warp(scale, 1, scale).unique()
        	.warp(
        		new HolderNoise(noise.getOrThrow(CLIMATE_WARP)).unique(),
        		new HolderNoise(noise.getOrThrow(CLIMATE_WARP)).unique(),
        		new HolderNoise(noise.getOrThrow(CLIMATE_WARP_STRENGTH)).unique()
	        );
    }
    
    private static Noise createHumidity(HolderGetter<Noise> noise, BiomeShape shape, RangeValue settings) {
    	float scaler = settings.scale() * 2.5F;
        int biomeSize = shape.biomeSize();
        float size = scaler * biomeSize;
        int scale = NoiseUtil.round(size * (1.0F / biomeSize));
        return settings.apply(new Humidity(scale, settings.falloff()))
        	.freq(1.0F / 500.0F, 1.0F / 500.0F)
        	.warp(Math.max(1, scale / 2), 1, scale / 4.0D).unique()
        	.warp(Math.max(1, scale / 6), 2, scale / 12.0D).unique()
        	.warp(
            	new HolderNoise(noise.getOrThrow(CLIMATE_WARP)).unique(),
            	new HolderNoise(noise.getOrThrow(CLIMATE_WARP)).unique(),
            	new HolderNoise(noise.getOrThrow(CLIMATE_WARP_STRENGTH)).unique()
        	);
    }

    private static Noise createContinentWarp() {
        return Source.builder()
        	.octaves(3)
            .lacunarity(2.2)
            .frequency(3)
            .gain(0.3)
            .perlin();
    }
    
    private static Noise createContinentWarpStrength() {
    	return Source.constant(0.2D);
    }
        
    private static Noise createContinent(HolderGetter<Noise> noise) {
    	return new Continent(1.0F, 0.525F, 0.75F, CellShape.SQUARE, CellSource.PERLIN, WorldSettings.ControlPoints.DEFAULT)
    		.freq(1.0F / 500.0F, 1.0F / 500.0F)	
    		.warp(
	        	new HolderNoise(noise.getOrThrow(CONTINENT_WARP)).unique(),
	        	new HolderNoise(noise.getOrThrow(CONTINENT_WARP)).unique(),
	        	new HolderNoise(noise.getOrThrow(CONTINENT_WARP_STRENGTH)).unique()
	    	);
    }

    private static Noise createBase(HolderGetter<Noise> noise) {
    	return new Continent(1.5f, 0.525F, 0.75F, CellShape.SQUARE, CellSource.PERLIN, WorldSettings.ControlPoints.DEFAULT)
    		.freq(1.0F / 500.0F, 1.0F / 500.0F)	
    		.warp(
    			new HolderNoise(noise.getOrThrow(CONTINENT_WARP)).unique(),
    	        new HolderNoise(noise.getOrThrow(CONTINENT_WARP)).unique(),
    	        new HolderNoise(noise.getOrThrow(CONTINENT_WARP_STRENGTH)).unique()
    	    );
    }
    
    private static Noise createRiverWarp() {
    	return Source.builder().frequency(30).legacySimplex();
    }
    
    private static Noise createRiverWarpStrength() {
    	return Source.constant(0.004);
    }
    
    private static Noise createRiver(HolderGetter<Noise> noise) {
    	return new River(RiverConfig.river(), RiverConfig.lake(), 0.75F, 1.0F / 400.0F)
    		.warp(
    			new HolderNoise(noise.getOrThrow(RIVER_WARP)).unique(),
    			new HolderNoise(noise.getOrThrow(RIVER_WARP)).unique(),
    			new HolderNoise(noise.getOrThrow(RIVER_WARP_STRENGTH)).unique()
    		);
    }
    
    private static Noise createWeirdness(HolderGetter<Noise> noise) {
    	return new Weirdness(CellShape.SQUARE, 1.0F, 0.8F)
    		.shift(1235785)
    		.freq(1.0F / 500.0F, 1.0F / 500.0F)	
    		.warp(
            	new HolderNoise(noise.getOrThrow(CLIMATE_WARP)).unique(),
            	new HolderNoise(noise.getOrThrow(CLIMATE_WARP)).unique(),
            	new HolderNoise(noise.getOrThrow(CLIMATE_WARP_STRENGTH)).unique()
        	);
    }
    
    private static Noise createOceanTerrain() {
        return Source.simplex(64, 3).shift(8763214).scale(0.4);
    }
    
    private static Noise createBlendedTerrain(HolderGetter<Noise> noise) {
    	Noise land = new TerrainBlender(0.8F, 0.4F, 
    		new WeightMap.Builder<>()
    			.entry(0.55F, noise.getOrThrow(RTFNoise.STEPPE))
    			.entry(0.6F,  noise.getOrThrow(RTFNoise.PLAINS))
    			.entry(0.55F, noise.getOrThrow(RTFNoise.HILLS_1))
    			.entry(0.55F, noise.getOrThrow(RTFNoise.HILLS_2))
    			.entry(0.45F, noise.getOrThrow(RTFNoise.DALES))
    			.entry(0.45F, noise.getOrThrow(RTFNoise.PLATEAU))
    			.entry(0.65F, noise.getOrThrow(RTFNoise.BADLANDS))
    			.entry(0.65F, noise.getOrThrow(RTFNoise.TORRIDONIAN))
    			.entry(0.55F, noise.getOrThrow(RTFNoise.MOUNTAINS_1))
    			.entry(0.45F, noise.getOrThrow(RTFNoise.MOUNTAINS_2))
    			.entry(0.45F, noise.getOrThrow(RTFNoise.MOUNTAINS_3))
    			.entry(0.45F, noise.getOrThrow(RTFNoise.DOLOMITES))
    			.entry(0.45F, noise.getOrThrow(RTFNoise.MOUNTAINS_RIDGE_1))
    			.entry(0.45F, noise.getOrThrow(RTFNoise.MOUNTAINS_RIDGE_2))
    			.build()
    	);
    	
    	NoiseLevels levels = NoiseLevels.create(Terrain.DEFAULT, 62);
    	return land.freq(levels.frequency, levels.frequency).mul(Source.constant(1024.0F));
    }
}
