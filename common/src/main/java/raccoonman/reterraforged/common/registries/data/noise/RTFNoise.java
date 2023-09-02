package raccoonman.reterraforged.common.registries.data.noise;

import net.minecraft.data.worldgen.BootstapContext;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

//
//import com.google.common.collect.ImmutableList;
//
//import net.minecraft.data.worldgen.BootstapContext;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.world.level.levelgen.DensityFunctions;
//import raccoonman.reterraforged.common.ReTerraForged;
//import raccoonman.reterraforged.common.level.levelgen.noise.Blender;
//import raccoonman.reterraforged.common.level.levelgen.noise.Falloff;
//import raccoonman.reterraforged.common.level.levelgen.noise.Fractal;
//import raccoonman.reterraforged.common.level.levelgen.noise.Hash;
//import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
//import raccoonman.reterraforged.common.level.levelgen.noise.LerpAlpha;
//import raccoonman.reterraforged.common.level.levelgen.noise.MapToRange;
//import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
//import raccoonman.reterraforged.common.level.levelgen.noise.Round;
//import raccoonman.reterraforged.common.level.levelgen.noise.Source;
//import raccoonman.reterraforged.common.level.levelgen.noise.Threshold;
//import raccoonman.reterraforged.common.level.levelgen.noise.ThresholdList;
//import raccoonman.reterraforged.common.level.levelgen.noise.Valley;
//import raccoonman.reterraforged.common.level.levelgen.noise.cell.CellShape;
//import raccoonman.reterraforged.common.level.levelgen.noise.cell.CellSource;
//import raccoonman.reterraforged.common.level.levelgen.noise.cell.SampleAtNearestCell;
//import raccoonman.reterraforged.common.level.levelgen.noise.climate.Temperature;
//import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
//import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
//import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
//import raccoonman.reterraforged.common.registries.RTFRegistries;
//import raccoonman.reterraforged.common.util.storage.WeightMap;
//
//public final class RTFNoise {
//	public static final ResourceKey<Noise> SCALE_FREQUENCY = resolve("scale_frequency");
//	public static final ResourceKey<Noise> BASE_FREQUENCY = resolve("base/base_frequency");
//	public static final ResourceKey<Noise> BASE_DEPTH = resolve("base/base_depth");
//	public static final ResourceKey<Noise> BASE_WARP = resolve("base/base_warp");
//	public static final ResourceKey<Noise> BASE = resolve("base/base");
//
//	public static final ResourceKey<Noise> BIOME_SIZE = resolve("climate/biome_size");
//	public static final ResourceKey<Noise> BIOME_FREQUENCY = resolve("climate/biome_frequency");
//	public static final ResourceKey<Noise> TEMPERATURE_SCALE = resolve("climate/temperature_scale");
//	public static final ResourceKey<Noise> TEMPERATURE_FREQUENCY = resolve("climate/temperature_frequency");
//	public static final ResourceKey<Noise> TEMPERATURE = resolve("climate/temperature");
//	public static final ResourceKey<Noise> HUMIDITY = resolve("climate/humidity");
//	
//	public static final ResourceKey<Noise> MUSHROOM_FIELDS = resolve("continent/mushroom_fields");
//	public static final ResourceKey<Noise> CONTINENT_CELL = resolve("continent/continent_cell");
//	public static final ResourceKey<Noise> CONTINENT_WARP = resolve("continent/continent_warp");
//	public static final ResourceKey<Noise> CONTINENT_THRESHOLD = resolve("continent/continent_threshold");
//	public static final ResourceKey<Noise> CONTINENT = resolve("continent/continent");
//	public static final ResourceKey<Noise> COAST_MAX = resolve("continent/points/coast");
//	public static final ResourceKey<Noise> SHORE_MAX = resolve("continent/points/shore");
//	public static final ResourceKey<Noise> OCEAN_MAX = resolve("continent/points/ocean");
//	public static final ResourceKey<Noise> STEPPE = resolve("terrain/land/steppe");
//	public static final ResourceKey<Noise> PLAINS = resolve("terrain/land/plains");
//	public static final ResourceKey<Noise> HILLS_1 = resolve("terrain/land/hills_1");
//	public static final ResourceKey<Noise> HILLS_2 = resolve("terrain/land/hills_2");
//	public static final ResourceKey<Noise> DALES = resolve("terrain/land/dales");
//	public static final ResourceKey<Noise> PLATEAU = resolve("terrain/land/plateau");
//	public static final ResourceKey<Noise> BADLANDS = resolve("terrain/land/badlands");
//	public static final ResourceKey<Noise> TORRIDONIAN = resolve("terrain/land/torridonian");
//	public static final ResourceKey<Noise> MOUNTAINS_1 = resolve("terrain/land/mountains_1");
//	public static final ResourceKey<Noise> MOUNTAINS_2 = resolve("terrain/land/mountains_2");
//	public static final ResourceKey<Noise> MOUNTAINS_3 = resolve("terrain/land/mountains_3");
//	public static final ResourceKey<Noise> DOLOMITES = resolve("terrain/land/dolomites");
//	public static final ResourceKey<Noise> MOUNTAINS_RIDGE_1 = resolve("terrain/land/mountains_ridge_1");
//	public static final ResourceKey<Noise> MOUNTAINS_RIDGE_2 = resolve("terrain/land/mountains_ridge_2");
//	public static final ResourceKey<Noise> MIN_DEPTH = resolve("terrain/min_depth");
//	public static final ResourceKey<Noise> LAND = resolve("terrain/land/land");
//	public static final ResourceKey<Noise> RIVER_WARP = resolve("terrain/land/river_warp");
//	public static final ResourceKey<Noise> RIVER = resolve("terrain/land/river");
//	public static final ResourceKey<Noise> COAST = resolve("terrain/land/coast");
//	public static final ResourceKey<Noise> SHORE = resolve("terrain/ocean/shore");
//	public static final ResourceKey<Noise> OCEAN = resolve("terrain/ocean/ocean");
//	public static final ResourceKey<Noise> TERRAIN = resolve("terrain/terrain");
//	
//	public static void register(BootstapContext<Noise> ctx) {
//    	Noise scaleFrequency = register(ctx, SCALE_FREQUENCY, Source.constant(0.002F));
//    	
//    	Noise baseFrequency = register(ctx, BASE_FREQUENCY, Source.constant(0.4298F));
//    	Noise baseDepth = register(ctx, BASE_DEPTH, Source.constant(0.123F));
//    	Noise baseWarp = register(ctx, BASE_WARP, createBaseWarp());
//    	Noise base = register(ctx, BASE, createBase(baseFrequency, baseWarp, scaleFrequency));
//    	
//    	Noise biomeSize = register(ctx, BIOME_SIZE, Source.constant(225));
//    	Noise temperatureScale = register(ctx, TEMPERATURE_SCALE, Source.constant(2));
//    	Noise temperature = register(ctx, TEMPERATURE, createTemperature(ctx, baseFrequency, climateFrequency, climateWarp));
//    	Noise humidity = register(ctx, HUMIDITY, createHumidity(baseFrequency, climateFrequency, climateWarp));
//    	
//    	Noise continentCell = register(ctx, CONTINENT_CELL, createContinentCell());
//    	Noise continentWarp = register(ctx, CONTINENT_WARP, createContinentWarp());
//    	Noise continentThreshold = register(ctx, CONTINENT_THRESHOLD, Source.constant(0.525F));
//    	Noise continent = register(ctx, CONTINENT, createContinent(baseFrequency, scaleFrequency, continentCell, continentWarp, continentThreshold));
//    	
//    	Noise coastMax = register(ctx, COAST_MAX, Source.constant(0.55F));
//    	Noise shoreMax = register(ctx, SHORE_MAX, Source.constant(0.5F));
//    	Noise oceanMax = register(ctx, OCEAN_MAX, Source.constant(0.25F));
//    	WeightMap<Noise> regions = createRegions(ctx);
//    	Noise riverWarp = register(ctx, RIVER_WARP, Source.constant(0.0D)); // TODO
//    	Noise river = register(ctx, RIVER, createRiver(riverWarp)); // TODO
//
//    	Noise minDepth = register(ctx, MIN_DEPTH, Source.constant(0.0449F));
//    	Noise land = register(ctx, LAND, createLand(baseFrequency, baseDepth, base, regions));
//    	Noise coast = register(ctx, COAST, createCoast(shoreMax, coastMax, baseDepth, continent, land));
//    	Noise ocean = register(ctx, OCEAN, createOcean(minDepth));
//    	Noise shore = register(ctx, SHORE, createShore(oceanMax, shoreMax, baseDepth, continent, ocean));
//    	Noise terrain = register(ctx, TERRAIN, createTerrain(oceanMax, shoreMax, coastMax, continent, land, coast, shore, ocean));
//    }
//    
//    private static Noise createSteppe() {
//    	int scaleH = Math.round(250.0F);
//    	double erosionAmount = 0.45D;
//    	Noise erosion = Source.build(scaleH * 2, 3).lacunarity(3.75D).perlin().alpha(erosionAmount).unique();
//    	Noise warpX = Source.build(scaleH / 4, 3).lacunarity(3.0D).perlin().unique();
//    	Noise warpY = Source.build(scaleH / 4, 3).lacunarity(3.0D).perlin().unique();
//    	Noise module = Source.perlin(scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0F)).warp(256, 1, 200.0D).unique();
//    	return module.scale(0.08D).bias(-0.02D);
//    }
//    
//    private static Noise createPlains() {
//    	//TODO test this cause i think 3.505F is too high
//    	int scaleH = Math.round(250.0F * 3.505F); 
//    	double erosionAmount = 0.45D;
//    	Noise erosion = Source.build(scaleH * 2, 3).lacunarity(3.75D).perlin().alpha(erosionAmount).unique();
//    	Noise warpX = Source.build(scaleH / 4, 3).lacunarity(3.5D).perlin().unique();
//    	Noise warpY = Source.build(scaleH / 4, 3).lacunarity(3.5D).perlin().unique();
//    	Noise module = Source.perlin(scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0F)).unique().warp(256, 1, 256.0D).unique();
//    	return module.scale(1.0F).scale(0.15F * 0.98F).bias(-0.02D);
//    }
//        
//    private static Noise createHills1() {
//    	return Source.perlin(200, 3).unique().mul(Source.billow(600, 3).unique().alpha(0.5)).warp(30, 3, 20.0).unique().warp(600, 3, 200.0).unique().scale(0.6f * 0.98F);
//    }
//    
//    private static Noise createHills2() {
//    	return Source.cubic(128, 2).unique().mul(Source.perlin(32, 4).unique().alpha(0.075)).warp(30, 3, 20.0).unique().warp(600, 3, 200.0).unique().mul(Source.ridge(512, 2).unique().alpha(0.8)).scale(0.55f * 0.98F);
//    }
//        
//    private static Noise createPlateau() {
//    	Noise valley = Source.ridge(500, 1).invert().unique().warp(100, 1, 150.0D).unique().warp(20, 1, 15.0D).unique();
//    	Noise top = Source.build(150, 3).lacunarity(2.45D).ridge().unique().warp(300, 1, 150.0D).unique().warp(40, 2, 20.0D).unique().scale(0.15D).mul(valley.clamp(0.02D, 0.1D).map(0.0D, 1.0D));
//    	Noise surface = Source.perlin(20, 3).unique().scale(0.05D).warp(40, 2, 20.0D).unique();
//    	Noise module = valley.mul(Source.cubic(500, 1).unique().scale(0.6D).bias(0.3D)).add(top).terrace(Source.constant(0.9D), Source.constant(0.15D), Source.constant(0.35D), 4, 0.4D).add(surface);
//    	return module.scale(0.475D * 0.98F);
//    }
//    
//    private static Noise createDales() {
//    	Noise hills1 = Source.build(300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75).unique();
//    	Noise hills2 = Source.build(350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25).unique();
//    	Noise combined = Source.perlin(600, 1).unique().clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
//    	Noise module = combined.pow(1.125).warp(300, 1, 100.0).unique();
//    	return module.scale(0.4);
//    }
//        
//    private static Noise createBadlands() {
//    	Noise mask = Source.build(270, 3).perlin().unique().clamp(0.35, 0.65).map(0.0, 1.0);
//    	Noise hills = Source.ridge(275, 4).warp(600, 2, 100.0).unique().warp(18, 1, 20.0).unique().mul(mask);
//    	double modulation = 0.4;
//    	double alpha = 1.0 - modulation;
//    	Noise mod1 = hills.warp(100, 1, 50.0).scale(modulation).unique();
//    	Noise lowFreq = hills.steps(4, 0.6, 1.3F).scale(alpha).add(mod1);
//    	Noise highFreq = hills.steps(10, 0.6, 1.3F).scale(alpha).add(mod1);
//    	Noise detail = lowFreq.add(highFreq);
//    	Noise mod2 = hills.mul(Source.perlin(200, 3).scale(modulation).unique());
//    	Noise shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
//    	Noise module = shape.mul(detail.alpha(0.5));
//    	return module.scale(0.55).bias(0.025);
//    }
//        
//    private static Noise createTorridonian() {
//    	Noise plains = Source.perlin(100, 3).unique().warp(300, 1, 150.0).unique().warp(20, 1, 40.0).unique().scale(0.15);
//    	Noise hills = Source.perlin(150, 4).unique().warp(300, 1, 200.0).unique().warp(20, 2, 20.0).unique().boost();
//    	Noise module = Source.perlin(200, 3).unique().blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(120, 1).unique().scale(0.25), Source.perlin(200, 1).unique().scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
//    	return module.scale(0.5);
//    }
//        
//    private static Noise createMountains1() {
//    	int scaleH = Math.round(610);
//    	Noise module = Source.build(scaleH, 4).gain(1.15).lacunarity(2.35).ridge().unique().mul(Source.perlin(24, 4).unique().alpha(0.075)).warp(350, 1, 150.0).unique();
//    	return makeFancy(module).scale(1.3F * 0.98F);
//    }
//        
//    private static Noise createMountains2(boolean fancy) {
//    	Noise cell = Source.cellEdge(360, EdgeFunction.DISTANCE_2).unique().scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).unique();
//    	Noise blur = Source.perlin(10, 1).unique().alpha(0.025);
//    	Noise surface = Source.ridge(125, 4).unique().alpha(0.37);
//    	Noise module = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
//    	return (fancy ? makeFancy(module) : module).scale(1.185F * 0.98F);
//    }
//        
//    private static Noise createMountains3(boolean fancy) {
//    	Noise cell = Source.cellEdge(600, EdgeFunction.DISTANCE_2).unique().scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).unique();
//    	Noise blur = Source.perlin(10, 1).unique().alpha(0.025);
//    	Noise surface = Source.ridge(125, 4).unique().alpha(0.37);
//    	Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
//    	Noise module = mountains.terrace(Source.perlin(50, 1).unique().scale(0.5), Source.perlin(100, 1).unique().clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.2f, 0.45f, 24, 1);
//    	return (fancy ? makeFancy(module) : module).scale(1.185F * 0.98F);
//    }
//        
//    private static Noise makeFancy(Noise module) {
//    	Domain warp = Domain.direction(Source.perlin(10, 1).unique(), Source.constant(2.0));
//    	return new Valley(module, 5, 0.65f, 128.0f, 0.2f, 3.1f, 0.6f, Valley.Mode.CONSTANT).warp(warp);
//    }
//        
//    private static Noise createDolomite() {
//    	// Valley floor terrain
//    	Noise base = Source.simplex(80, 4).unique().scale(0.1);
//    	
//    	// Controls where the ridges show up
//    	Noise shape = Source.simplex(475, 4).unique()
//    		.clamp(0.3, 1.0).map(0, 1)
//    		.warp(10, 2, 8)
//    		.unique();
//    	
//    	// More gradual slopes up to the ridges
//    	Noise slopes = shape.pow(2.2).scale(0.65).add(base);
//    	
//    	// Sharp ridges
//    	Noise peaks = Source.build(400, 5).lacunarity(2.7).gain(0.6).simplexRidge()
//    		.unique()
//    		.clamp(0, 0.675F).map(0, 1)
//    		.warp(Domain.warp(Source.LEGACY_SIMPLEX, 40, 5, 30))
//    		.alpha(0.875F);
//
//        return shape.mul(peaks).max(slopes)
//        	.warp(800, 3, 300)
//        	.unique()
//        	.scale(0.75F);
//    }
//    
//    private static Noise createRiver(Noise riverWarp) {
////    	return new River(RiverConfig.river(), RiverConfig.lake(), 0.75F, 1.0F / 400.0F, Source.builder().frequency(128).octaves(2).ridge().shift(21221))
////    		.warp(
////    			Source.builder().frequency(30).legacySimplex().unique(),
////    			Source.builder().frequency(30).legacySimplex().unique(),
////    			Source.constant(0.004)
////    		);
//    	return Source.constant(0.0D);
//    }
//    
//    //TODO
//    private static Noise createMushroomFields(Noise baseFrequency, Noise warp, float threshold, float scale) {
//    	throw new UnsupportedOperationException();
//    }
//    
//    private static Noise createContinentCell() {
//    	return new Fractal(CellSource.PERLIN, 2, 2.75F, 0.3F).freq(1.0F / 5.0F, 1.0F / 5.0F).shift(6569);
//    }
//    
//    private static Noise createContinentWarp() {
//    	return Source.builder()
//        	.octaves(3)
//        	.lacunarity(2.2)
//        	.frequency(3)
//        	.gain(0.3)
//        	.perlin();
//    }
//    
//    private static Noise createContinent(Noise baseFrequency, Noise scaleFrequency, Noise cell, Noise warp, Noise threshold) {
////    	threshold = NoiseUtil.map(threshold, -3.8F, 3.8F);
//    	return new Falloff(
//    		new SampleAtNearestCell(new Threshold(cell, threshold, cell.sub(Source.constant(0.055F)), cell), 1.0F, 0.75F, 2, CellShape.SQUARE),
//    		0.1F,
//    		ImmutableList.of(
//    			new Falloff.Point(0.8F, 1.0F, 1.0F),   // inland
//    			new Falloff.Point(0.75F, 0.55F, 1.0F), // coast
//    			new Falloff.Point(0.45F, 0.5F, 0.55F), // beach
//    			new Falloff.Point(0.3F, 0.25F, 0.5F),  // shallow ocean
//    			new Falloff.Point(0.05F, 0.1F, 0.25F)  // deep ocean
//    		)
//    	).freq(scaleFrequency, scaleFrequency).warp(warp.unique(), warp.unique(), Source.constant(0.2D)).freq(baseFrequency, baseFrequency).clamp(0.0D, 1.0D);
//    }
//    
//    private static WeightMap<Noise> createRegions(BootstapContext<Noise> ctx) {
//    	return new WeightMap.Builder<>()
//			.entry(0.55F, new HolderNoise(ctx.register(STEPPE, createSteppe())))
//			.entry(0.6F,  new HolderNoise(ctx.register(PLAINS, createPlains())))
//			.entry(0.55F, new HolderNoise(ctx.register(HILLS_1, createHills1())))
//			.entry(0.55F, new HolderNoise(ctx.register(HILLS_2, createHills2())))
//			.entry(0.45F, new HolderNoise(ctx.register(DALES, createDales())))
//			.entry(0.45F, new HolderNoise(ctx.register(PLATEAU, createPlateau())))
//			.entry(0.65F, new HolderNoise(ctx.register(BADLANDS, createBadlands())))
//			.entry(0.65F, new HolderNoise(ctx.register(TORRIDONIAN, createTorridonian())))
//			.entry(0.55F, new HolderNoise(ctx.register(MOUNTAINS_1, createMountains1())))
//			.entry(0.45F, new HolderNoise(ctx.register(MOUNTAINS_2, createMountains2(true))))
//			.entry(0.45F, new HolderNoise(ctx.register(MOUNTAINS_3, createMountains3(true))))
//			.entry(0.45F, new HolderNoise(ctx.register(DOLOMITES, createDolomite())))
//			.entry(0.45F, new HolderNoise(ctx.register(MOUNTAINS_RIDGE_1, createMountains2(false))))
//			.entry(0.45F, new HolderNoise(ctx.register(MOUNTAINS_RIDGE_2, createMountains3(false))))
//			.build();
//    }
//    
//    private static Noise createBaseWarp() {
//    	return Source.builder()
//        	.octaves(3)
//        	.lacunarity(2.2)
//        	.frequency(3)
//        	.gain(0.3)
//        	.perlin();
//    }
//    
//    private static Noise createBase(Noise baseFrequency, Noise warp, Noise scaleFrequency) {
//    	final float threshold = 0.525F;
//        return new MapToRange(new SampleAtNearestCell(CellSource.PERLIN.freq(1.0F / 5.0F, 1.0F / 5.0F).shift(656), 1.0F, 0.75F, 2, CellShape.SQUARE).freq(scaleFrequency, scaleFrequency).warp(warp.unique(), warp.unique(), Source.constant(0.2D)), threshold + 0.01F, threshold + 0.25F).freq(baseFrequency, baseFrequency).mul(Source.constant(0.25F));
//    }
//    
//    private static Noise createLand(Noise baseFrequency, Noise baseDepth, Noise base, WeightMap<Noise> regions) {
//    	return baseDepth.add(base.add(new Blender(new Hash(), Domain.warp(Source.LEGACY_SIMPLEX, 800, 3, 800 / 2.5F).shift(12678), 0.8F, 800.0F, 0.4F, regions).freq(baseFrequency, baseFrequency)).mul(Source.constant(0.627F)).mul(Source.constant(1.2F)));
//    }
//    
//    private static Noise createCoast(Noise shoreMax, Noise coastMax, Noise baseDepth, Noise continent, Noise land) {
//    	return new LerpAlpha(baseDepth, land, continent, shoreMax, coastMax);
//    }
//    
//    private static Noise createShore(Noise oceanMax, Noise shoreMax, Noise baseDepth, Noise continent, Noise ocean) {
//    	return new LerpAlpha(ocean, baseDepth, continent, oceanMax, shoreMax);
//    }
//    
//    private static Noise createOcean(Noise minDepth) {
//        return Source.simplex(64, 3).shift(8763214).scale(0.4F).mul(Source.constant(0.078F)).add(minDepth);
//    }
//
//    private static Noise createTerrain(Noise oceanMax, Noise shoreMax, Noise coastMax, Noise continent, Noise land, Noise coast, Noise shore, Noise ocean) {
//    	return new ThresholdList(continent, ImmutableList.of(
//    		new ThresholdList.Point(ocean, oceanMax),
//    		new ThresholdList.Point(shore, shoreMax),
//    		new ThresholdList.Point(coast, coastMax),
//    		new ThresholdList.Point(land, Source.constant(DensityFunctions.MAX_REASONABLE_NOISE_VALUE))
//    	));
//    }
//    
//    private static Noise createTemperature(BootstapContext<Noise> ctx, Noise baseFrequency, Noise climateFrequency, Noise climateWarp) {
//        final float temperatureSize = tempScaler * biomeSize;
//    	Noise scale = register(ctx, TEMPERATURE_SCALE, new Round(tempSize.mul(climateFrequency)));
//    	
//    	Noise temperature = new Temperature(1.0F / tempScale, settings.climate.temperature.falloff);
//    	return settings.climate.temperature.apply(temperature).warp(tempScale * 4, 2, tempScale * 4).warp(tempScale, 1, tempScale);
//    }
//    
//    private static Noise createHumidity(Noise baseFrequency, Noise climateFrequency, Noise climateWarp) {
//    	return Source.constant(0.0D);
//    }
//}
