package raccoonman.reterraforged.common.registries.data;

import com.google.common.collect.ImmutableList;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.Blender;
import raccoonman.reterraforged.common.level.levelgen.noise.Falloff;
import raccoonman.reterraforged.common.level.levelgen.noise.Floor;
import raccoonman.reterraforged.common.level.levelgen.noise.Fractal;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.MapToRange;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Threshold;
import raccoonman.reterraforged.common.level.levelgen.noise.Valley;
import raccoonman.reterraforged.common.level.levelgen.noise.cell.CellShape;
import raccoonman.reterraforged.common.level.levelgen.noise.cell.CellSource;
import raccoonman.reterraforged.common.level.levelgen.noise.cell.SampleAtNearestCell;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerp;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public final class RTFNoise {
	private static final ResourceKey<Noise> STEPPE = resolve("steppe");
	private static final ResourceKey<Noise> PLAINS = resolve("plains");
	private static final ResourceKey<Noise> HILLS_1 = resolve("hills_1");
	private static final ResourceKey<Noise> HILLS_2 = resolve("hills_2");
	private static final ResourceKey<Noise> DALES = resolve("dales");
	private static final ResourceKey<Noise> PLATEAU = resolve("plateau");
	private static final ResourceKey<Noise> BADLANDS = resolve("badlands");
	private static final ResourceKey<Noise> TORRIDONIAN = resolve("torridonian");
	private static final ResourceKey<Noise> MOUNTAINS_1 = resolve("mountains_1");
	private static final ResourceKey<Noise> MOUNTAINS_2 = resolve("mountains_2");
	private static final ResourceKey<Noise> MOUNTAINS_3 = resolve("mountains_3");
	private static final ResourceKey<Noise> DOLOMITES = resolve("dolomites");
	private static final ResourceKey<Noise> MOUNTAINS_RIDGE_1 = resolve("mountains_ridge_1");
	private static final ResourceKey<Noise> MOUNTAINS_RIDGE_2 = resolve("mountains_ridge_2");
	private static final ResourceKey<Noise> TERRAIN = resolve("terrain");
	private static final ResourceKey<Noise> OCEAN = resolve("ocean");
	public static final ResourceKey<Noise> RIVER = resolve("river");
	public static final ResourceKey<Noise> FINAL_BLEND = resolve("final_blend");
	public static final ResourceKey<Noise> CONTINENTS = resolve("continents");
	public static final ResourceKey<Noise> RIDGES = resolve("ridges");
	
    public static void register(BootstapContext<Noise> ctx) {
    	Noise finalBlend = createFinalBlend(ctx);
    	ctx.register(FINAL_BLEND, finalBlend);
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
    
    private static final float DEPTH_MIN = 0.0449F;
    private static final float DEPTH_RANGE = 0.078F;
    private static final float HEIGHT_MIN = 0.123F;
    private static final float HEIGHT_RANGE = 0.627F;
    private static final float BASE_RANGE = 0.25F;
    private static final float MAX_Y = 512;
    private static final float FREQUENCY = 0.4298F;
    
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
    	Noise cell = Source.cellEdge(360, EdgeFunction.DISTANCE_2).unique().scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).unique();
    	Noise blur = Source.perlin(10, 1).unique().alpha(0.025);
    	Noise surface = Source.ridge(125, 4).unique().alpha(0.37);
    	Noise module = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
    	return (fancy ? makeFancy(module) : module).scale(MOUNTAINS2_V * TERRAIN_VERTICAL_SCALE);
    }
        
    private static Noise createMountains3(boolean fancy) {
    	Noise cell = Source.cellEdge(MOUNTAINS2_H, EdgeFunction.DISTANCE_2).unique().scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).unique();
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
    		.clamp(0, 0.675F).map(0, 1)
    		.warp(Domain.warp(Source.LEGACY_SIMPLEX, 40, 5, 30))
    		.alpha(0.875F);

        return shape.mul(peaks).max(slopes)
        	.warp(800, 3, 300)
        	.unique()
        	.scale(0.75F);
    }
    
    private static Noise createOcean() {
        return Source.simplex(64, 3).shift(8763214).scale(0.4F);
    }

    private static Noise createBase(int scale) {
    	Noise warp = Source.builder()
    		.octaves(3)
    		.lacunarity(2.2)
    		.frequency(3)
    		.gain(0.3)
    		.perlin();
    	final float threshold = 0.525F;
    	return new MapToRange(new SampleAtNearestCell(CellSource.PERLIN.freq(1.0F / 5.0F, 1.0F / 5.0F).shift(656), 1.0F, 0.75F, 2, CellShape.SQUARE).freq(1.0F / scale, 1.0F / scale).warp(warp.unique(), warp.unique(), Source.constant(0.2D)), threshold + 0.01F, threshold + 0.25F);
    }
    
    private static Noise createContinent(int scale) {
    	Noise warp = Source.builder()
    		.octaves(3)
    		.lacunarity(2.2)
    		.frequency(3)
    		.gain(0.3)
    		.perlin();
    	final float threshold = NoiseUtil.map(0.525F, -3.8F, 3.8F);
    	Noise continent = new Fractal(CellSource.PERLIN, 2, 2.75F, 0.3F).freq(1.0F / 5.0F, 1.0F / 5.0F).shift(6569);
    	return new Falloff(
    		new SampleAtNearestCell(new Threshold(continent, Source.constant(threshold), continent.sub(Source.constant(0.55F)), continent), 1.0F, 0.75F, 2, CellShape.SQUARE),
    		-0.5F,
    		ImmutableList.of(
    			new Falloff.Point(0.6F, 1.0F, 3.8F),
    			new Falloff.Point(0.25F, -0.19F, 1.0F),
    			new Falloff.Point(0.5F, -0.35F, -0.19F)
//    			new Falloff.Point(0.25F, 0.6F, 1.0F),
//    			new Falloff.Point(-0.11F, 0.25F, 0.6F),
//    			new Falloff.Point(-0.19F, 0.1F, 0.25F),
//    			new Falloff.Point(-0.5F, -1.1F, 0.1F)
    			//TODO blend into ocean
    		)
    	).freq(1.0F / scale, 1.0F / scale).warp(warp.unique(), warp.unique(), Source.constant(0.2D)).freq(FREQUENCY, FREQUENCY).clamp(-3.8F, 3.8F);
    }
    
    private static Noise createRiver() {
//    	return new River(RiverConfig.river(), RiverConfig.lake(), 0.75F, 1.0F / 400.0F, Source.builder().frequency(128).octaves(2).ridge().shift(21221))
//    		.warp(
//    			Source.builder().frequency(30).legacySimplex().unique(),
//    			Source.builder().frequency(30).legacySimplex().unique(),
//    			Source.constant(0.004)
//    		);
    	return Source.constant(0.0D);
    }
    
    private static Noise createTerrain(BootstapContext<Noise> ctx) {
    	return new Blender(Domain.warp(Source.LEGACY_SIMPLEX, 800, 3, 800 / 2.5F).shift(12678), 0.8F, 800.0F, 0.4F, 
    		new WeightMap.Builder<>()
    			.entry(0.55F, new HolderNoise(ctx.register(STEPPE, createSteppe())))
    			.entry(0.6F,  new HolderNoise(ctx.register(PLAINS, createPlains())))
    			.entry(0.55F, new HolderNoise(ctx.register(HILLS_1, createHills1())))
    			.entry(0.55F, new HolderNoise(ctx.register(HILLS_2, createHills2())))
    			.entry(0.45F, new HolderNoise(ctx.register(DALES, createDales())))
    			.entry(0.45F, new HolderNoise(ctx.register(PLATEAU, createPlateau())))
    			.entry(0.65F, new HolderNoise(ctx.register(BADLANDS, createBadlands())))
    			.entry(0.65F, new HolderNoise(ctx.register(TORRIDONIAN, createTorridonian())))
    			.entry(0.55F, new HolderNoise(ctx.register(MOUNTAINS_1, createMountains1())))
    			.entry(0.45F, new HolderNoise(ctx.register(MOUNTAINS_2, createMountains2(true))))
    			.entry(0.45F, new HolderNoise(ctx.register(MOUNTAINS_3, createMountains3(true))))
    			.entry(0.45F, new HolderNoise(ctx.register(DOLOMITES, createDolomite())))
    			.entry(0.45F, new HolderNoise(ctx.register(MOUNTAINS_RIDGE_1, createMountains2(false))))
    			.entry(0.45F, new HolderNoise(ctx.register(MOUNTAINS_RIDGE_2, createMountains3(false))))
    			.build()
    	).freq(FREQUENCY, FREQUENCY);
    }
    
    //TODO
    private static Noise createMushroomIslandSelector() {
    	throw new UnsupportedOperationException();
    }
    
	//TODO
    private static Noise createMushroomIslandContinent() {
    	throw new UnsupportedOperationException();
    }
    
    private static Noise createFinalBlend(BootstapContext<Noise> ctx) {
    	final int continentScale = 700;
    	Noise continent = new HolderNoise(ctx.register(CONTINENTS, createContinent(continentScale)));
    	Noise ocean = new HolderNoise(ctx.register(OCEAN, createOcean()));
    	Noise river = new HolderNoise(ctx.register(RIVER, createRiver()));
    	Noise terrain = new HolderNoise(ctx.register(TERRAIN, createTerrain(ctx)));
    	return new Floor(new ContinentLerp(
    		continent,
    		Source.constant(HEIGHT_MIN)
    			.add(createBase(continentScale).freq(FREQUENCY, FREQUENCY).mul(Source.constant(BASE_RANGE)))
    			.add(terrain.mul(Source.constant(HEIGHT_RANGE)).mul(Source.constant(1.2F))),
    		ocean.mul(Source.constant(DEPTH_RANGE)).add(Source.constant(DEPTH_MIN)).freq(FREQUENCY, FREQUENCY),
    		HEIGHT_MIN, -0.35F, -0.19F, 0.3F
    	).mul(Source.constant(MAX_Y)));
    }
    
    private static ResourceKey<Noise> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.NOISE, path);
	}
}
