package raccoonman.reterraforged.common.registries.data;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.Blender;
import raccoonman.reterraforged.common.level.levelgen.noise.Choice;
import raccoonman.reterraforged.common.level.levelgen.noise.Falloff;
import raccoonman.reterraforged.common.level.levelgen.noise.Floor;
import raccoonman.reterraforged.common.level.levelgen.noise.Fractal;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.MapRange;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.noise.SampleAtNearestCell;
import raccoonman.reterraforged.common.level.levelgen.noise.Valley;
import raccoonman.reterraforged.common.level.levelgen.noise.Weirdness;
import raccoonman.reterraforged.common.level.levelgen.noise.cell.CellShape;
import raccoonman.reterraforged.common.level.levelgen.noise.cell.CellSource;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Humidity;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Temperature;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.Continent;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerp;
import raccoonman.reterraforged.common.level.levelgen.noise.river.River;
import raccoonman.reterraforged.common.level.levelgen.noise.river.RiverConfig;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.domain.Domain;
import raccoonman.reterraforged.common.noise.func.EdgeFunc;
import raccoonman.reterraforged.common.noise.func.Interpolation;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public final class RTFNoise {
	public static final ResourceKey<Noise> STEPPE = resolve("terrain/steppe");
	public static final ResourceKey<Noise> PLAINS = resolve("terrain/plains");
	public static final ResourceKey<Noise> HILLS_1 = resolve("terrain/hills_1");
	public static final ResourceKey<Noise> HILLS_2 = resolve("terrain/hills_2");
	public static final ResourceKey<Noise> DALES = resolve("terrain/dales");
	public static final ResourceKey<Noise> PLATEAU = resolve("terrain/plateau");
	public static final ResourceKey<Noise> BADLANDS = resolve("terrain/badlands");
	public static final ResourceKey<Noise> TORRIDONIAN = resolve("terrain/torridonian");
	public static final ResourceKey<Noise> MOUNTAINS_1 = resolve("terrain/mountains_1");
	public static final ResourceKey<Noise> MOUNTAINS_2 = resolve("terrain/mountains_2");
	public static final ResourceKey<Noise> MOUNTAINS_3 = resolve("terrain/mountains_3");
	public static final ResourceKey<Noise> DOLOMITES = resolve("terrain/dolomites");
	public static final ResourceKey<Noise> MOUNTAINS_RIDGE_1 = resolve("terrain/mountains_ridge_1");
	public static final ResourceKey<Noise> MOUNTAINS_RIDGE_2 = resolve("terrain/mountains_ridge_2");
	public static final ResourceKey<Noise> TEMPERATURE = resolve("climate/temperature");
	public static final ResourceKey<Noise> HUMIDITY = resolve("climate/humidity");
	public static final ResourceKey<Noise> CONTINENT = resolve("continent");
	public static final ResourceKey<Noise> RIVER = resolve("terrain/river");
	public static final ResourceKey<Noise> WEIRDNESS = resolve("weirdness");
	public static final ResourceKey<Noise> TERRAIN = resolve("terrain/blender");
	
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

        final int continentScale = 500;
        ctx.register(CONTINENT, createContinent(continentScale));
        ctx.register(RIVER, createRiver());
    	NoiseLevels levels = NoiseLevels.create(true, 1.0F, -64, 480, 128, 40, 62);
        ctx.register(TERRAIN, createTerrainBlender(noise, levels, createBase(continentScale), createOcean()));        
        ctx.register(WEIRDNESS, createWeirdness());
        ctx.register(TEMPERATURE, createTemperature(levels, 225, new RangeValue(6, 2, 0.0F, 0.98F, 0.05F)));
        ctx.register(HUMIDITY, createHumidity(levels, 225, new RangeValue(6, 1, 0.0F, 1.0F, 0.0F)));
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
    	return new Valley(module, 5, 0.65f, 128.0f, 0.2f, 3.1f, 0.6f, Valley.Mode.CONSTANT).warp(warp);
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
    		.warp(Domain.warp(Source.LEGACY_SIMPLEX, 40, 5, 30))
    		.alpha(0.875);

        return shape.mul(peaks).max(slopes)
        	.warp(800, 3, 300)
        	.unique()
        	.scale(0.75);
    }
    
    private static Noise createOcean() {
        return Source.simplex(64, 3).shift(8763214).scale(0.4);
    }
    
    @Deprecated
    private record RangeValue(int scale, int falloff, float min, float max, float bias) {

        public Noise apply(Noise module) {
            float min = NoiseUtil.clamp(Math.min(this.min, this.max), 0.0f, 1.0f);
            float max = NoiseUtil.clamp(Math.max(this.min, this.max), min, 1.0f);
            return module.bias(NoiseUtil.clamp(this.bias, -1.0f, 1.0f) / 2.0F).clamp(min, max);
        }
    }
    
    private static Noise createTemperature(NoiseLevels levels, int biomeSize, RangeValue settings) {
        float scaler = settings.scale();
        float size = scaler * biomeSize;
        int scale = NoiseUtil.round(size * (1.0F / biomeSize));
        Noise temperature = settings.apply(new Temperature(1.0F / scale, settings.falloff()))
        	.warp(scale * 4, 2, scale * 4)
        	.warp(scale, 1, scale);;
    	return new SampleAtNearestCell(temperature, CellShape.SQUARE, 0.8F).freq(1.0F / biomeSize, 1.0F / biomeSize).warp(
    		Source.build(80, 3).lacunarity(2.4).gain(0.3).simplex(),
    		Source.build(80, 3).lacunarity(2.4).gain(0.3).simplex(),
    		Source.constant(80.0F * 0.75)		
    	).freq(levels.frequency, levels.frequency);
    }
    
    private static Noise createHumidity(NoiseLevels levels, int biomeSize, RangeValue settings) {
        float scaler = settings.scale() * 2.5F;
        float freq = 1F / biomeSize;
        float size = scaler * biomeSize;
        int moistScale = NoiseUtil.round(size * freq);
    	Noise humidity = new Humidity(Source.simplex(moistScale, 1).clamp(0.125, 0.875).map(0.0, 1.0).freq(0.5F, 1.0F), settings.falloff());
    	humidity = settings.apply(humidity)
    		.warp(Math.max(1, moistScale / 2), 1, moistScale / 4.0D)
    		.warp(Math.max(1, moistScale / 6), 2, moistScale / 12.0D);
    	return new SampleAtNearestCell(humidity, CellShape.SQUARE, 0.8F).freq(1.0F / biomeSize, 1.0F / biomeSize).warp(
    		Source.build(80, 3).lacunarity(2.4).gain(0.3).simplex(),
    		Source.build(80, 3).lacunarity(2.4).gain(0.3).simplex(),
    		Source.constant(80.0F * 0.75)		
    	).freq(levels.frequency, levels.frequency);
    }

    private static Noise createBase(int scale) {
    	Noise warp = Source.builder()
    		.octaves(3)
    		.lacunarity(2.2)
    		.frequency(3)
    		.gain(0.3)
    		.perlin();
    	final float threshold = 0.525F;
    	return new MapRange(new Continent(1.0F, 0.75F, CellShape.SQUARE, CellSource.PERLIN.freq(1.0F / 5.0F, 1.0F / 5.0F).shift(656)).freq(1.0F / scale, 1.0F / scale).warp(warp.unique(), warp.unique(), Source.constant(0.2D)), threshold + 0.01F, threshold + 0.25F);
    }
    
    private static Noise createContinent(int scale) {
    	Noise warp = Source.builder()
    		.octaves(3)
    		.lacunarity(2.2)
    		.frequency(3)
    		.gain(0.3)
    		.perlin();
    	final float threshold = 0.525F;
    	return new Falloff(
    		new Continent(1.0F, 0.75F, CellShape.SQUARE, new Choice(new Fractal(CellSource.PERLIN, 2, 2.75F, 0.3F).freq(1.0F / 5.0F, 1.0F / 5.0F).shift(6569), Source.constant(threshold), Source.constant(0.0F), Source.constant(1.0F))),
    		0.1F,
    		ImmutableList.of(
	    		new Falloff.Point(0.8F, 1.0F, 1.0F),   // inland
	    		new Falloff.Point(0.75F, 0.55F, 1.0F), // coast
	    		new Falloff.Point(0.45F, 0.5F, 0.55F), // beach
	    		new Falloff.Point(0.3F, 0.25F, 0.5F),  // shallow ocean
	    		new Falloff.Point(0.05F, 0.1F, 0.25F)  // deep ocean
    		)
    	).freq(1.0F / scale, 1.0F / scale).warp(warp.unique(), warp.unique(), Source.constant(0.2D));
    }
    
    private static Noise createRiver() {
    	Noise warp = Source.builder().frequency(30).legacySimplex();
    	return new River(RiverConfig.river(), RiverConfig.lake(), 0.75F, 1.0F / 400.0F)
    		.warp(
    			warp.unique(),
    			warp.unique(),
    			Source.constant(0.004)
    		);
    }
    
    private static Noise createWeirdness() {
        Noise warp = Source.build(80, 3).lacunarity(2.4).gain(0.3).simplex();
    	return new Weirdness(CellShape.SQUARE, 1.0F, 0.8F)
    		.shift(1235785)
    		.freq(1.0F / 500.0F, 1.0F / 500.0F)	
        	.warp(
        		warp.unique(),
        		warp.unique(),
        		Source.constant(80 * 0.75)
            );
    }
    
    //TODO we should do the block height scaling in the density function instead
    private static Noise createTerrainBlender(HolderGetter<Noise> noise, NoiseLevels levels, Noise base, Noise ocean) {
    	return new Floor(new ContinentLerp(
    		new HolderNoise(noise.getOrThrow(CONTINENT)),
    			Source.constant(levels.heightMin)
    			.add(base.mul(Source.constant(levels.baseRange)))
    			.add(new Blender(Domain.warp(Source.LEGACY_SIMPLEX, 800, 3, 800 / 2.5F).shift(12678), 0.8F, 800.0F, 0.4F,
    				 new WeightMap.Builder<>()
	    	    		.entry(0.55F, new HolderNoise(noise.getOrThrow(RTFNoise.STEPPE)))
	    	    		.entry(0.6F,  new HolderNoise(noise.getOrThrow(RTFNoise.PLAINS)))
	    	    		.entry(0.55F, new HolderNoise(noise.getOrThrow(RTFNoise.HILLS_1)))
	    	    		.entry(0.55F, new HolderNoise(noise.getOrThrow(RTFNoise.HILLS_2)))
	    	    		.entry(0.45F, new HolderNoise(noise.getOrThrow(RTFNoise.DALES)))
	    	    		.entry(0.45F, new HolderNoise(noise.getOrThrow(RTFNoise.PLATEAU)))
	    	    		.entry(0.65F, new HolderNoise(noise.getOrThrow(RTFNoise.BADLANDS)))
	    	    		.entry(0.65F, new HolderNoise(noise.getOrThrow(RTFNoise.TORRIDONIAN)))
	    	    		.entry(0.55F, new HolderNoise(noise.getOrThrow(RTFNoise.MOUNTAINS_1)))
	    	    		.entry(0.45F, new HolderNoise(noise.getOrThrow(RTFNoise.MOUNTAINS_2)))
	    	    		.entry(0.45F, new HolderNoise(noise.getOrThrow(RTFNoise.MOUNTAINS_3)))
	    	    		.entry(0.45F, new HolderNoise(noise.getOrThrow(RTFNoise.DOLOMITES)))
	    	    		.entry(0.45F, new HolderNoise(noise.getOrThrow(RTFNoise.MOUNTAINS_RIDGE_1)))
	    	    		.entry(0.45F, new HolderNoise(noise.getOrThrow(RTFNoise.MOUNTAINS_RIDGE_2)))
	    	    		.build()
    				 ).mul(Source.constant(levels.heightRange)).mul(Source.constant(1.2F))
    			),
    		ocean.mul(Source.constant(levels.depthRange)).add(Source.constant(levels.depthMin)),
    		levels.heightMin, 0.25F, 0.5F, 0.55F
    	).freq(levels.frequency, levels.frequency).mul(Source.constant(levels.maxY)));
    }
}
