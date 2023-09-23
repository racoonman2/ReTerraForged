package raccoonman.reterraforged.common.worldgen.data.noise;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Valley;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.Volcano;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.General;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.Terrain;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

//TODO we might be able to do initial density here too!!!
final class RTFFeatureNoise {
	public static final ResourceKey<Noise> STEPPE_VARIANCE = createKey("steppe/variance");
	public static final ResourceKey<Noise> STEPPE_EROSION = createKey("steppe/erosion");
	public static final ResourceKey<Noise> STEPPE_RIDGE = createKey("steppe/ridge");
	
	public static final ResourceKey<Noise> PLAINS_VARIANCE = createKey("plains/variance");
	public static final ResourceKey<Noise> PLAINS_EROSION = createKey("plains/erosion");
	public static final ResourceKey<Noise> PLAINS_RIDGE = createKey("plains/ridge");

	public static final ResourceKey<Noise> DALES_VARIANCE = createKey("dales/variance");
	public static final ResourceKey<Noise> DALES_EROSION = createKey("dales/erosion");
	public static final ResourceKey<Noise> DALES_RIDGE = createKey("dales/ridge");
	
	public static final ResourceKey<Noise> HILLS_1_VARIANCE = createKey("hills_1/variance");
	public static final ResourceKey<Noise> HILLS_1_EROSION = createKey("hills_1/erosion");
	public static final ResourceKey<Noise> HILLS_1_RIDGE = createKey("hills_1/ridge");
	
	public static final ResourceKey<Noise> HILLS_2_VARIANCE = createKey("hills_2/variance");
	public static final ResourceKey<Noise> HILLS_2_EROSION = createKey("hills_2/erosion");
	public static final ResourceKey<Noise> HILLS_2_RIDGE = createKey("hills_2/ridge");

	public static final ResourceKey<Noise> TORRIDONIAN_VARIANCE = createKey("torridonian/variance");
	public static final ResourceKey<Noise> TORRIDONIAN_EROSION = createKey("torridonian/erosion");
	public static final ResourceKey<Noise> TORRIDONIAN_RIDGE = createKey("torridonian/ridge");
	
	public static final ResourceKey<Noise> PLATEAU_VARIANCE = createKey("plateau/variance");
	public static final ResourceKey<Noise> PLATEAU_EROSION = createKey("plateau/erosion");
	public static final ResourceKey<Noise> PLATEAU_RIDGE = createKey("plateau/ridge");
	
	public static final ResourceKey<Noise> BADLANDS_VARIANCE = createKey("badlands/variance");
	public static final ResourceKey<Noise> BADLANDS_EROSION = createKey("badlands/erosion");
	public static final ResourceKey<Noise> BADLANDS_RIDGE = createKey("badlands/ridge");
	
	public static final ResourceKey<Noise> MOUNTAINS_VARIANCE = createKey("mountains/variance");
	public static final ResourceKey<Noise> MOUNTAINS_EROSION = createKey("mountains/erosion");
	public static final ResourceKey<Noise> MOUNTAINS_RIDGE = createKey("mountains/ridge");
	
	public static final ResourceKey<Noise> MOUNTAINS_2_VARIANCE = createKey("mountains_2/variance");
	public static final ResourceKey<Noise> MOUNTAINS_2_EROSION = createKey("mountains_2/erosion");
	public static final ResourceKey<Noise> MOUNTAINS_2_RIDGE = createKey("mountains_2/ridge");
	
	public static final ResourceKey<Noise> MOUNTAINS_3_VARIANCE = createKey("mountains_3/variance");
	public static final ResourceKey<Noise> MOUNTAINS_3_EROSION = createKey("mountains_3/erosion");
	public static final ResourceKey<Noise> MOUNTAINS_3_RIDGE = createKey("mountains_3/ridge");
	
	public static final ResourceKey<Noise> MOUNTAIN_CHAIN_VARIANCE = createKey("mountain_chain/variance");
	public static final ResourceKey<Noise> MOUNTAIN_CHAIN_EROSION = createKey("mountain_chain/erosion");
	public static final ResourceKey<Noise> MOUNTAIN_CHAIN_RIDGE = createKey("mountain_chain/ridge");
	public static final ResourceKey<Noise> MOUNTAIN_CHAIN_SHAPE = createKey("mountain_chain/shape");
	
	public static final ResourceKey<Noise> VOLCANO_VARIANCE = createKey("volcano/variance");
	public static final ResourceKey<Noise> VOLCANO_EROSION = createKey("volcano/erosion");
	public static final ResourceKey<Noise> VOLCANO_RIDGE = createKey("volcano/ridge");
	
	public static final ResourceKey<Noise> BORDER_VARIANCE = createKey("border/variance");
	public static final ResourceKey<Noise> BORDER_EROSION = createKey("border/erosion");
	public static final ResourceKey<Noise> BORDER_RIDGE = createKey("border/ridge");
	
	public static final ResourceKey<Noise> GROUND = createKey("ground");
	
	public static void bootstrap(BootstapContext<Noise> ctx, General general, WorldSettings world, TerrainSettings terrain, Seed featureSeed, Seed regionSeed, Seed mountainSeed, int regionWarpStrength) {
		registerSteppe(ctx, terrain.steppe, featureSeed);
		registerPlains(ctx, terrain.general, terrain.plains, featureSeed);
		registerDales(ctx, featureSeed);
		registerHills1(ctx, terrain.general, featureSeed);
		registerHills2(ctx, terrain.general, featureSeed);
		registerTorridonian(ctx, terrain.general, featureSeed);
		registerPlateau(ctx, terrain.general, featureSeed);
		registerBadlands(ctx, featureSeed);
		registerMountains(ctx, terrain.general, terrain.mountains, featureSeed, MOUNTAINS_VARIANCE, MOUNTAINS_EROSION, MOUNTAINS_RIDGE);
		registerMountains2(ctx, terrain.general, featureSeed);
		registerMountains3(ctx, terrain.general, featureSeed);
		registerMountainChain(ctx, terrain.general, terrain.mountains, mountainSeed, featureSeed);
		registerVolcano(ctx, terrain.general, terrain.volcano, regionSeed, featureSeed, regionWarpStrength);
		registerBorder();

		ctx.register(GROUND, Source.constant((float) world.properties.seaLevel / (float) general.yScale));
	}
	
	private static void registerSteppe(BootstapContext<Noise> ctx, Terrain settings, Seed seed) {
        int scaleH = Math.round(250.0f * settings.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise module = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 200.0);
        Noise scaled = module.scale(0.08).bias(-0.02);
		
		ctx.register(STEPPE_VARIANCE, scaled);
		ctx.register(STEPPE_EROSION, Source.constant(0.45D));
		ctx.register(STEPPE_RIDGE, Source.constant(-0.2D));
	}
	
	private static void registerPlains(BootstapContext<Noise> ctx, General general, Terrain settings, Seed seed) {
        int scaleH = Math.round(250.0f * settings.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        Noise module = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 256.0);
        Noise scaled = module.scale(0.15f * general.globalVerticalScale).bias(-0.02);
        
        ctx.register(PLAINS_VARIANCE, scaled);
		ctx.register(PLAINS_EROSION, Source.constant(0.45D));
		ctx.register(PLAINS_RIDGE, Source.constant(-0.2D));
	}
	
    private static void registerDales(BootstapContext<Noise> ctx, Seed seed) {
        Noise hills1 = Source.build(seed.next(), 300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75);
        Noise hills2 = Source.build(seed.next(), 350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25);
        Noise combined = Source.perlin(seed.next(), 400, 1).clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
        Noise hills3 = combined.pow(1.125).warp(seed.next(), 300, 1, 100.0);
        Noise scaled = hills3.scale(0.4);
        
        ctx.register(DALES_VARIANCE, scaled);
		ctx.register(DALES_EROSION, Source.constant(0.45D));
		ctx.register(DALES_RIDGE, Source.constant(-0.2D));
    }
    
    private static void registerHills1(BootstapContext<Noise> ctx, General general, Seed seed) {
    	Noise scaled = Source.perlin(seed.next(), 200, 3).mul(Source.billow(seed.next(), 400, 3).alpha(0.5)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).scale(0.6f * general.globalVerticalScale);
    
        ctx.register(HILLS_1_VARIANCE, scaled);
		ctx.register(HILLS_1_EROSION, Source.constant(0.45D));
		ctx.register(HILLS_1_RIDGE, Source.constant(-0.2D));
	}
    
    private static void registerHills2(BootstapContext<Noise> ctx, General general, Seed seed) {
        Noise scaled = Source.cubic(seed.next(), 128, 2).mul(Source.perlin(seed.next(), 32, 4).alpha(0.075)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).mul(Source.ridge(seed.next(), 512, 2).alpha(0.8)).scale(0.55f * general.globalVerticalScale);

        ctx.register(HILLS_2_VARIANCE, scaled);
		ctx.register(HILLS_2_EROSION, Source.constant(0.45D));
		ctx.register(HILLS_2_RIDGE, Source.constant(-0.2D));
    }
    
    private static void registerTorridonian(BootstapContext<Noise> ctx, General general, Seed seed) {
    	Noise plains = Source.perlin(seed.next(), 100, 3).warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 20, 1, 40.0).scale(0.15);
    	Noise hills = Source.perlin(seed.next(), 150, 4).warp(seed.next(), 300, 1, 200.0).warp(seed.next(), 20, 2, 20.0).boost();
    	Noise module = Source.perlin(seed.next(), 200, 3).blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(seed.next(), 120, 1).scale(0.25), Source.perlin(seed.next(), 200, 1).scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
    	Noise scaled = module.scale(0.5);
    	
        ctx.register(TORRIDONIAN_VARIANCE, scaled);
		ctx.register(TORRIDONIAN_EROSION, Source.constant(0.45D));
		ctx.register(TORRIDONIAN_RIDGE, Source.constant(-0.2D));
    }
    
    private static void registerPlateau(BootstapContext<Noise> ctx, General general, Seed seed) {
    	Noise valley = Source.ridge(seed.next(), 500, 1).invert().warp(seed.next(), 100, 1, 150.0).warp(seed.next(), 20, 1, 15.0);
    	Noise top = Source.build(seed.next(), 150, 3).lacunarity(2.45).ridge().warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 40, 2, 20.0).scale(0.15).mul(valley.clamp(0.02, 0.1).map(0.0, 1.0));
    	Noise surface = Source.perlin(seed.next(), 20, 3).scale(0.05).warp(seed.next(), 40, 2, 20.0);
    	Noise module = valley.mul(Source.cubic(seed.next(), 500, 1).scale(0.6).bias(0.3)).add(top).terrace(Source.constant(0.9), Source.constant(0.15), Source.constant(0.35), 4, 0.4).add(surface);
    	Noise scaled = module.scale(0.475 * general.globalVerticalScale);
    	
        ctx.register(PLATEAU_VARIANCE, scaled);
		ctx.register(PLATEAU_EROSION, Source.constant(0.45D));
		ctx.register(PLATEAU_RIDGE, Source.constant(-0.2D));
    }
	
    private static void registerBadlands(BootstapContext<Noise> ctx, Seed seed) {
    	Noise mask = Source.build(seed.next(), 270, 3).perlin().clamp(0.35, 0.65).map(0.0, 1.0);
    	Noise hills = Source.ridge(seed.next(), 275, 4).warp(seed.next(), 400, 2, 100.0).warp(seed.next(), 18, 1, 20.0).mul(mask);
        double modulation = 0.4;
        double alpha = 1.0 - modulation;
        Noise mod1 = hills.warp(seed.next(), 100, 1, 50.0).scale(modulation);
        Noise lowFreq = hills.steps(4, 0.6, 0.7).scale(alpha).add(mod1);
        Noise highFreq = hills.steps(10, 0.6, 0.7).scale(alpha).add(mod1);
        Noise detail = lowFreq.add(highFreq);
        Noise mod2 = hills.mul(Source.perlin(seed.next(), 200, 3).scale(modulation));
        Noise shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
        Noise badlands = shape.mul(detail.alpha(0.5));
        Noise scaled = badlands.scale(0.55).bias(0.025);
    	
        ctx.register(BADLANDS_VARIANCE, scaled);
		ctx.register(BADLANDS_EROSION, Source.constant(0.45D));
		ctx.register(BADLANDS_RIDGE, Source.constant(-0.2D));
    }
    
    // TODO base erosion / ridge per mountain type
    // that way we can have a variation of mountain configurations
    
    private static void registerMountains(BootstapContext<Noise> ctx, General general, Terrain terrain, Seed seed, ResourceKey<Noise> variance, ResourceKey<Noise> erosion, ResourceKey<Noise> ridge) {
        int scaleH = Math.round(410.0f * terrain.horizontalScale);
        Noise module = Source.build(seed.next(), scaleH, 4).gain(1.15).lacunarity(2.35).ridge().mul(Source.perlin(seed.next(), 24, 4).alpha(0.075)).warp(seed.next(), 350, 1, 150.0);
        Noise scaled = makeFancyAndRegisterRidge(general, seed, module, ctx, ridge, erosion).scale(0.7 * general.globalVerticalScale);
    	
        ctx.register(variance, scaled);
//		ctx.register(erosion, Source.constant(-1.0D));
    }
    
    private static void registerMountains2(BootstapContext<Noise> ctx, General general, Seed seed) {
        Noise cell = Source.cellEdge(seed.next(), 360, EdgeFunction.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        Noise blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        Noise surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        Noise scaled = makeFancyAndRegisterRidge(general, seed, mountains, ctx, MOUNTAINS_2_RIDGE, MOUNTAINS_2_EROSION).scale(0.645 * general.globalVerticalScale);
    	
        ctx.register(MOUNTAINS_2_VARIANCE, scaled);
//		ctx.register(MOUNTAINS_2_EROSION, Source.constant(-1.0D));
    }
    
    private static void registerMountains3(BootstapContext<Noise> ctx, General general, Seed seed) {
        Noise cell = Source.cellEdge(seed.next(), 400, EdgeFunction.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        Noise blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        Noise surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        Noise terraced = mountains.terrace(Source.perlin(seed.next(), 50, 1).scale(0.5), Source.perlin(seed.next(), 100, 1).clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.20000000298023224, 0.44999998807907104, 24, 1);
        Noise scaled = makeFancyAndRegisterRidge(general, seed, terraced, ctx, MOUNTAINS_3_RIDGE, MOUNTAINS_3_EROSION).scale(0.645 * general.globalVerticalScale);

        ctx.register(MOUNTAINS_3_VARIANCE, scaled);
//		ctx.register(MOUNTAINS_3_EROSION, Source.constant(-1.0D));
    }
    
    //lol wtf is this
    private static Noise makeFancyAndRegisterRidge(General general, Seed seed, Noise noise, BootstapContext<Noise> ctx, ResourceKey<Noise> ridge, ResourceKey<Noise> erosion) {
        if (general.fancyMountains) {
            Domain warp = Domain.direction(Source.perlin(seed.next(), 10, 1), Source.constant(2.0));
            int shift = seed.next();
            Noise valley = new Valley(noise, 2, 0.65F, 128.0F, 0.15F, 3.1F, 0.8F, Valley.Mode.CONSTANT).shift(shift).warp(warp);
            //FIXME ridge shouldnt be tied to terrain height
            ctx.register(ridge, valley);
        	ctx.register(erosion, valley.invert());
            return valley;
        } else {
        	ctx.register(ridge, noise);
        	ctx.register(erosion, noise);
        }
        return noise;
    }
   
    private static void registerMountainChain(BootstapContext<Noise> ctx, General general, Terrain settings, Seed mountainSeed, Seed featureSeed) {
    	registerMountains(ctx, general, settings, featureSeed, MOUNTAIN_CHAIN_VARIANCE, MOUNTAIN_CHAIN_EROSION, MOUNTAIN_CHAIN_RIDGE);

        Noise mountainShapeBase = Source.cellEdge(mountainSeed.next(), 1000, EdgeFunction.DISTANCE_2_ADD).warp(mountainSeed.next(), 333, 2, 250.0);
    	ctx.register(MOUNTAIN_CHAIN_SHAPE, mountainShapeBase.curve(Interpolation.CURVE3).clamp(0.0, 0.9).map(0.0, 1.0));
    }
    
    private static void registerVolcano(BootstapContext<Noise> ctx, General general, Terrain settings, Seed regionSeed, Seed featureSeed, int warpStrength) {
    	HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
    	Noise warpX = new HolderNoise(noise.getOrThrow(RTFTerrainNoise2.REGION_WARP_X));
    	Noise warpZ = new HolderNoise(noise.getOrThrow(RTFTerrainNoise2.REGION_WARP_Z));
    	
        float midpoint = 0.3f;
        float range = 0.3f;
        Noise heightNoise = Source.perlin(featureSeed.next(), 2, 1).map(0.45, 0.65);
        Noise height = Source.cellNoise(regionSeed.get(), general.terrainRegionSize, heightNoise).warp(warpX, warpZ, warpStrength);
        Noise cone = Source.cellEdge(regionSeed.get(), general.terrainRegionSize, EdgeFunction.DISTANCE_2_DIV).invert().warp(warpX, warpZ, warpStrength).powCurve(11.0).clamp(0.475, 1.0).map(0.0, 1.0).grad(0.0, 0.5, 0.5).warp(featureSeed.next(), 15, 2, 10.0).scale(height);
        Noise lowlands = Source.ridge(featureSeed.next(), 150, 3).warp(featureSeed.next(), 30, 1, 30.0).scale(0.1);
        float inversionPoint = 0.94f;
        float blendLower = midpoint - range / 2.0f;
        float blendUpper = blendLower + range;
        Noise scaled = new Volcano(cone, height, lowlands, inversionPoint, blendLower, blendUpper);
    	
        ctx.register(VOLCANO_VARIANCE, scaled);
		ctx.register(VOLCANO_EROSION, Source.constant(0.45D));
		ctx.register(VOLCANO_RIDGE, Source.constant(-0.2D));
    }

    private static void registerBorder() {
    	
    }
    
	private static ResourceKey<Noise> createKey(String string) {
		return RTFTerrainNoise2.createKey("feature/" + string);
	}
}
