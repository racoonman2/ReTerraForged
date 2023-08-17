package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Valley;
import raccoonman.reterraforged.common.level.levelgen.util.Seed;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.domain.Domain;
import raccoonman.reterraforged.common.noise.func.EdgeFunc;
import raccoonman.reterraforged.common.noise.func.Interpolation;
import raccoonman.reterraforged.common.registries.RTFRegistries;

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
	
    public static void register(BootstapContext<Noise> ctx) {
        var seed = new Seed(0);
        ctx.register(STEPPE, Terrain.createSteppe(seed));
        ctx.register(PLAINS, Terrain.createPlains(seed));
        ctx.register(HILLS_1, Terrain.createHills1(seed));
        ctx.register(HILLS_2, Terrain.createHills2(seed));
        ctx.register(DALES, Terrain.createDales(seed));
        ctx.register(PLATEAU, Terrain.createPlateau(seed));
        ctx.register(BADLANDS, Terrain.createBadlands(seed));
        ctx.register(TORRIDONIAN, Terrain.createTorridonian(seed));
        ctx.register(MOUNTAINS_1, Terrain.createMountains1(seed));
        ctx.register(MOUNTAINS_2, Terrain.createMountains2(seed, true));
        ctx.register(MOUNTAINS_3, Terrain.createMountains3(seed, true));
        ctx.register(DOLOMITES, Terrain.createDolomite(seed));
        ctx.register(MOUNTAINS_RIDGE_1, Terrain.createMountains2(seed, false));
        ctx.register(MOUNTAINS_RIDGE_2, Terrain.createMountains3(seed, false));
    }
    
    private static ResourceKey<Noise> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.NOISE, path);
	}

    private static final class Terrain {
    	private static final float STEPPE_HSCALE = 1.0F;
    	private static final float STEPPE_VSCALE = 1.0F;
    	private static final float PLAINS_HSCALE = 3.505F;
    	private static final float PLAINS_VSCALE = 1.0F;
        private static final int MOUNTAINS_H = 610;
        private static final float MOUNTAINS_V = 1.3F;
        private static final int MOUNTAINS2_H = 600;
        private static final float MOUNTAINS2_V = 1.185F;
        private static final float TERRAIN_VERTICAL_SCALE = 0.96F;

        public static Noise createSteppe(Seed seedOffset) {
            int scaleH = Math.round(250.0F * STEPPE_HSCALE);
            double erosionAmount = 0.45D;
            Noise erosion = Source.build(scaleH * 2, 3).lacunarity(3.75D).perlin().alpha(erosionAmount).shift(seedOffset.next());
            Noise warpX = Source.build(scaleH / 4, 3).lacunarity(3.0D).perlin().shift(seedOffset.next());
            Noise warpY = Source.build(scaleH / 4, 3).lacunarity(3.0D).perlin().shift(seedOffset.next());
            Noise module = Source.perlin(scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0F)).warp(256, 1, 200.0D).shift(seedOffset.next());
            return module.scale(STEPPE_VSCALE).scale(0.08D).bias(-0.02D);
        }
        
        public static Noise createPlains(Seed seedOffset) {
            int scaleH = Math.round(250.0F * PLAINS_HSCALE);
            double erosionAmount = 0.45D;
            Noise erosion = Source.build(scaleH * 2, 3).lacunarity(3.75D).perlin().alpha(erosionAmount).shift(seedOffset.next());
            Noise warpX = Source.build(scaleH / 4, 3).lacunarity(3.5D).perlin().shift(seedOffset.next());
            Noise warpY = Source.build(scaleH / 4, 3).lacunarity(3.5D).perlin().shift(seedOffset.next());
            Noise module = Source.perlin(scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0F)).shift(seedOffset.next()).warp(256, 1, 256.0D).shift(seedOffset.next());
            return module.scale(PLAINS_VSCALE).scale(0.15F * TERRAIN_VERTICAL_SCALE).bias(-0.02D);
        }
        
        public static Noise createHills1(Seed seedOffset) {
        	return Source.perlin(200, 3).shift(seedOffset.next()).mul(Source.billow(MOUNTAINS2_H, 3).shift(seedOffset.next()).alpha(0.5)).warp(30, 3, 20.0).shift(seedOffset.next()).warp(MOUNTAINS2_H, 3, 200.0).shift(seedOffset.next()).scale(0.6f * TERRAIN_VERTICAL_SCALE);
        }

        public static Noise createHills2(Seed seedOffset) {
            return Source.cubic(128, 2).shift(seedOffset.next()).mul(Source.perlin(32, 4).shift(seedOffset.next()).alpha(0.075)).warp(30, 3, 20.0).shift(seedOffset.next()).warp(MOUNTAINS2_H, 3, 200.0).shift(seedOffset.next()).mul(Source.ridge(512, 2).shift(seedOffset.next()).alpha(0.8)).scale(0.55f * TERRAIN_VERTICAL_SCALE);
        }
        
        public static Noise createPlateau(Seed seedOffset) {
            Noise valley = Source.ridge(500, 1).invert().shift(seedOffset.next()).warp(100, 1, 150.0D).shift(seedOffset.next()).warp(20, 1, 15.0D).shift(seedOffset.next());
            Noise top = Source.build(150, 3).lacunarity(2.45D).ridge().shift(seedOffset.next()).warp(300, 1, 150.0D).shift(seedOffset.next()).warp(40, 2, 20.0D).shift(seedOffset.next()).scale(0.15D).mul(valley.clamp(0.02D, 0.1D).map(0.0D, 1.0D));
            Noise surface = Source.perlin(20, 3).shift(seedOffset.next()).scale(0.05D).warp(40, 2, 20.0D).shift(seedOffset.next());
            Noise module = valley.mul(Source.cubic(500, 1).shift(seedOffset.next()).scale(0.6D).bias(0.3D)).add(top).terrace(Source.constant(0.9D), Source.constant(0.15D), Source.constant(0.35D), 4, 0.4D).add(surface);
            return module.scale(0.475D * TERRAIN_VERTICAL_SCALE);
        }
        
        public static Noise createDales(Seed seedOffset) {
            Noise hills1 = Source.build(300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75).shift(seedOffset.next());
            Noise hills2 = Source.build(350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25).shift(seedOffset.next());
            Noise combined = Source.perlin(MOUNTAINS2_H, 1).shift(seedOffset.next()).clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
            Noise module = combined.pow(1.125).warp(300, 1, 100.0).shift(seedOffset.next());
            return module.scale(0.4);
        }
        
        public static Noise createBadlands(Seed seedOffset) {
            Noise mask = Source.build(270, 3).perlin().shift(seedOffset.next()).clamp(0.35, 0.65).map(0.0, 1.0);
            Noise hills = Source.ridge(275, 4).warp(MOUNTAINS2_H, 2, 100.0).shift(seedOffset.next()).warp(18, 1, 20.0).shift(seedOffset.next()).mul(mask);
            double modulation = 0.4;
            double alpha = 1.0 - modulation;
            Noise mod1 = hills.warp(100, 1, 50.0).scale(modulation).shift(seedOffset.next());
            Noise lowFreq = hills.steps(4, 0.6, MOUNTAINS_V).scale(alpha).add(mod1);
            Noise highFreq = hills.steps(10, 0.6, MOUNTAINS_V).scale(alpha).add(mod1);
            Noise detail = lowFreq.add(highFreq);
            Noise mod2 = hills.mul(Source.perlin(200, 3).scale(modulation).shift(seedOffset.next()));
            Noise shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
            Noise module = shape.mul(detail.alpha(0.5));
            return module.scale(0.55).bias(0.025);
        }
        
        public static Noise createTorridonian(Seed seedOffset) {
            Noise plains = Source.perlin(100, 3).shift(seedOffset.next()).warp(300, 1, 150.0).shift(seedOffset.next()).warp(20, 1, 40.0).shift(seedOffset.next()).scale(0.15);
            Noise hills = Source.perlin(150, 4).shift(seedOffset.next()).warp(300, 1, 200.0).shift(seedOffset.next()).warp(20, 2, 20.0).shift(seedOffset.next()).boost();
            Noise module = Source.perlin(200, 3).shift(seedOffset.next()).blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(120, 1).shift(seedOffset.next()).scale(0.25), Source.perlin(200, 1).shift(seedOffset.next()).scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
            return module.scale(0.5);
        }
        
        public static Noise createMountains1(Seed seedOffset) {
            int scaleH = Math.round(MOUNTAINS_H);
            Noise module = Source.build(scaleH, 4).gain(1.15).lacunarity(2.35).ridge().shift(seedOffset.next()).mul(Source.perlin(24, 4).shift(seedOffset.next()).alpha(0.075)).warp(350, 1, 150.0).shift(seedOffset.next());
            return makeFancy(seedOffset, module).scale(MOUNTAINS_V * TERRAIN_VERTICAL_SCALE);
        }
        
        public static Noise createMountains2(Seed seedOffset, boolean fancy) {
            Noise cell = Source.cellEdge(360, EdgeFunc.DISTANCE_2).shift(seedOffset.next()).scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).shift(seedOffset.next());
            Noise blur = Source.perlin(10, 1).shift(seedOffset.next()).alpha(0.025);
            Noise surface = Source.ridge(125, 4).shift(seedOffset.next()).alpha(0.37);
            Noise module = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
            return (fancy ? makeFancy(seedOffset, module) : module).scale(MOUNTAINS2_V * TERRAIN_VERTICAL_SCALE);
        }
        
        public static Noise createMountains3(Seed seedOffset, boolean fancy) {
            Noise cell = Source.cellEdge(MOUNTAINS2_H, EdgeFunc.DISTANCE_2).shift(seedOffset.next()).scale(1.2).clamp(0.0, 1.0).warp(200, 2, 100.0).shift(seedOffset.next());
            Noise blur = Source.perlin(10, 1).shift(seedOffset.next()).alpha(0.025);
            Noise surface = Source.ridge(125, 4).shift(seedOffset.next()).alpha(0.37);
            Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
            Noise module = mountains.terrace(Source.perlin(50, 1).shift(seedOffset.next()).scale(0.5), Source.perlin(100, 1).shift(seedOffset.next()).clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.2f, 0.45f, 24, 1);
            return (fancy ? makeFancy(seedOffset, module) : module).scale(MOUNTAINS2_V * TERRAIN_VERTICAL_SCALE);
        }
        
        public static Noise makeFancy(Seed seedOffset, Noise module) {
        	Domain warp = Domain.direction(Source.perlin(10, 1).shift(seedOffset.next()), Source.constant(2.0));
        	Valley erosion = new Valley(5, 0.65f, 128.0f, 0.2f, 3.1f, 0.6f, Valley.Mode.CONSTANT);
        	return erosion.wrap(Holder.direct(module)).warp(warp);
        }
        
        public static Noise createDolomite(Seed seedOffset) {
            // Valley floor terrain
            var base = Source.simplex(80, 4).shift(seedOffset.next()).scale(0.1);

            // Controls where the ridges show up
            var shape = Source.simplex(475, 4).shift(seedOffset.next())
                    .clamp(0.3, 1.0).map(0, 1)
                    .warp(10, 2, 8)
                    .shift(seedOffset.next());

            // More gradual slopes up to the ridges
            var slopes = shape.pow(2.2).scale(0.65).add(base);

            // Sharp ridges
            var peaks = Source.build(400, 5).lacunarity(2.7).gain(0.6).simplexRidge()
            		.shift(seedOffset.next())
                    .clamp(0, 0.675).map(0, 1)
                    .warp(Domain.warp(Source.SIMPLEX, seedOffset.next(), 40, 5, 30))
                    .alpha(0.875);

            return shape.mul(peaks).max(slopes)
                    .warp(800, 3, 300)
                    .shift(seedOffset.next())
                    .scale(0.75);
        }
    }
}
