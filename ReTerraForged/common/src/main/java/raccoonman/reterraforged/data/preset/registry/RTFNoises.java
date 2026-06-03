package raccoonman.reterraforged.data.preset.registry;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.Seed;
import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.Noises;
import raccoonman.reterraforged.world.worldgen.noise.PseudoErosion.BlendMode;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.curvefunction.Interpolation;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warp;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warps;

public class RTFNoises {
	public static final ResourceKey<Noise> BORDER = createKey("border");
	public static final ResourceKey<Noise> STEPPE = createKey("steppe");
	public static final ResourceKey<Noise> PLAINS = createKey("plains");
	public static final ResourceKey<Noise> DALES = createKey("dales");
	public static final ResourceKey<Noise> HILLS_1 = createKey("hills1");
	public static final ResourceKey<Noise> HILLS_2 = createKey("hills2");
	public static final ResourceKey<Noise> TORRIDONIAN = createKey("torridonian");
	public static final ResourceKey<Noise> PLATEAU = createKey("plateau");
	public static final ResourceKey<Noise> BADLANDS = createKey("badlands");
	public static final ResourceKey<Noise> MOUNTAINS_1 = createKey("mountains1");
	public static final ResourceKey<Noise> MOUNTAINS_2 = createKey("mountains2");
	public static final ResourceKey<Noise> MOUNTAINS_3 = createKey("mountains3");
	public static final ResourceKey<Noise> MOUNTAIN_CHAIN = createKey("mountain_chain");

	public static void bootstrap(Preset preset, BootstrapContext<Noise> ctx) {
		Seed seed = new Seed(0);
		ctx.register(BORDER, steppe(seed));
		ctx.register(STEPPE, steppe(seed));
		ctx.register(PLAINS, plains(seed, 0.98F));
		ctx.register(DALES, dales(seed));
		ctx.register(HILLS_1, hills1(seed, 0.98F));
		ctx.register(HILLS_2, hills2(seed, 0.98F));
		ctx.register(TORRIDONIAN, torridonian(seed));
		ctx.register(PLATEAU, plateau(seed, 0.98F));
		ctx.register(BADLANDS, badlands(seed));
		ctx.register(MOUNTAINS_1, mountains1(seed, 1.0F, 0.98F, true));
		ctx.register(MOUNTAINS_2, mountains2(seed, 0.98F, true));
		ctx.register(MOUNTAINS_3, mountains3(seed, 0.98F, true));
		ctx.register(MOUNTAIN_CHAIN, mountainChain(seed, 1.0F, 0.98F, true));
	}
	
	private static Noise steppe(Seed seed) {
        int scaleH = Math.round(250.0F);

        Noise erosion = Noises.perlin(seed.next(), scaleH * 2, 3, 3.75F);
        erosion = Noises.alpha(erosion, 0.45F);
        
        Noise warpX = Noises.perlin(seed.next(), scaleH / 4, 3, 3.0F);
        Noise warpZ = Noises.perlin(seed.next(), scaleH / 4, 3, 3.0F);
        
        Noise height = Noises.perlin(seed.next(), scaleH, 1);
        height = Noises.mul(height, erosion);
        height = Noises.warp(height, warpX, warpZ, scaleH / 4.0F);
        height = Noises.warpPerlin(height, seed.next(), 256, 1, 200.0F);
        height = Noises.mul(height, 0.08F);
        height = Noises.add(height, -0.02F);
        return height;
	}
    
    private static Noise plains(@Deprecated Seed seed, float verticalScale) {
    	int scaleH = Math.round(250.0F);
      	
		Noise erosion = Noises.perlin(seed.next(), scaleH * 2, 3, 3.75F);
      	erosion = Noises.alpha(erosion, 0.45F);
      	
      	Noise warpX = Noises.perlin(seed.next(), scaleH / 4, 3, 3.5F);
      	Noise warpZ = Noises.perlin(seed.next(), scaleH / 4, 3, 3.5F);
      	
      	Noise height = Noises.perlin(seed.next(), scaleH, 1);
      	height = Noises.mul(height, erosion);
      	height = Noises.warp(height, warpX, warpZ, scaleH / 4.0F);
      	height = Noises.warpPerlin(height, seed.next(), 256, 1, 256.0F);
      	height = Noises.mul(height, 0.15F * verticalScale);
      	height = Noises.add(height, -0.02F);
      	return height;
    }

	private static Noise dales(@Deprecated Seed seed) {
		Noise hills1 = Noises.billow(seed.next(), 300, 4, 4.0F, 0.8F);
		hills1 = Noises.powCurve(hills1, 0.5F);
		hills1 = Noises.mul(hills1, 0.75F);
		
		Noise hills2 = Noises.billow(seed.next(), 350, 3, 4.0F, 0.8F);
		hills2 = Noises.pow(hills2, 1.25F);
		
		Noise selector = Noises.perlin(seed.next(), 400, 1);
		selector = Noises.clamp(selector, 0.3F, 0.6F);
		selector = Noises.map(selector, 0.0F, 1.0F);
		
		int warpSeed = seed.next();
		
		Noise hillsBlend = Noises.blend(selector, hills1, hills2, 0.4F, 0.75F);
		
		Noise height = hillsBlend;
		height = Noises.pow(height, 1.125F);
		height = Noises.warpPerlin(height, warpSeed, 300, 1, 100.0F);
		height = Noises.mul(height, 0.4F);
		return height;
	}

	private static Noise hills1(@Deprecated Seed seed, float verticalScale) {
		Noise height = Noises.perlin(seed.next(), 200, 3);
		
		Noise scaler = Noises.billow(seed.next(), 400, 3);
		scaler = Noises.alpha(scaler, 0.5F);
		
		height = Noises.mul(height, scaler);
		height = Noises.warpPerlin(height, seed.next(), 30, 3, 20.0F);
		height = Noises.warpPerlin(height, seed.next(), 400, 3, 200.0F);
		height = Noises.mul(height, 0.6F * verticalScale);
		return height;
	}
	
	private static Noise hills2(@Deprecated Seed seed, float verticalScale) {
		Noise height = Noises.cubic(seed.next(), 128, 2);

		Noise scaler1 = Noises.perlin(seed.next(), 32, 4);
		scaler1 = Noises.alpha(scaler1, 0.075F);
		height = Noises.mul(height, scaler1);
		
		height = Noises.warpPerlin(height, seed.next(), 30, 3, 20.0F);
		height = Noises.warpPerlin(height, seed.next(), 400, 3, 200.0F);

		Noise scaler2 = Noises.perlinRidge(seed.next(), 512, 2);
		scaler2 = Noises.alpha(scaler2, 0.8F);
		height = Noises.mul(height, scaler2);
		height = Noises.mul(height, 0.55F * verticalScale);
		return height;
	}
	
	private static Noise torridonian(@Deprecated Seed seed) {
		Noise plains = Noises.perlin(seed.next(), 100, 3);
		plains = Noises.warpPerlin(plains, seed.next(), 300, 1, 150.0F);
		plains = Noises.warpPerlin(plains, seed.next(), 20, 1, 40.0F);
		plains = Noises.mul(plains, 0.15F);
		
		Noise hills = Noises.perlin(seed.next(), 150, 4);
		hills = Noises.warpPerlin(hills, seed.next(), 300, 1, 200.0F);
		hills = Noises.warpPerlin(hills, seed.next(), 20, 2, 20.0F);
		hills = Noises.boost(hills);
		
		Noise selector = Noises.perlin(seed.next(), 200, 3);
		
		Noise modulation = Noises.perlin(seed.next(), 120, 1);
		modulation = Noises.mul(modulation, 0.25F);
		
		Noise mask = Noises.perlin(seed.next(), 200, 1);
		mask = Noises.mul(mask, 0.5F);
		mask = Noises.add(mask, 0.5F);
		
		Noise slope = Noises.constant(0.5F);
		
		Noise blend = Noises.blend(selector, plains, hills, 0.6F, 0.6F);
		blend = Noises.advancedTerrace(blend, modulation, mask, slope, 0.0F, 0.3F, 6, 1);
		Noise height = Noises.boost(blend);
		height = Noises.mul(height, 0.5F);
		return height;
	}
	
	private static Noise plateau(Seed seed, float verticalScale) {
		Noise valley = Noises.perlinRidge(seed.next(), 500, 1);
		valley = Noises.invert(valley);
		valley = Noises.warpPerlin(valley, seed.next(), 100, 1, 150.0F);
		valley = Noises.warpPerlin(valley, seed.next(), 20, 1, 15.0F);
		
		Noise top = Noises.perlinRidge(seed.next(), 150, 3, 2.45F);
		top = Noises.warpPerlin(top, seed.next(), 300, 1, 150.0F);
		top = Noises.warpPerlin(top, seed.next(), 40, 2, 20.0F);
		top = Noises.mul(top, 0.15F);
		
		Noise valleyScaler = Noises.clamp(valley, 0.02F, 0.1F);
		valleyScaler = Noises.map(valleyScaler, 0.0F, 1.0F);
		
		top = Noises.mul(top, valleyScaler);
		
		Noise surface = Noises.perlin(seed.next(), 20, 3);
		surface = Noises.mul(surface, 0.05F);
		surface = Noises.warpPerlin(surface, seed.next(), 40, 2, 20.0F);
		
		Noise cubic = Noises.cubic(seed.next(), 500, 1);
		cubic = Noises.mul(cubic, 0.6F);
		cubic = Noises.add(cubic, 0.3F);
		
		Noise valleyBase = Noises.mul(valley, cubic);
		valleyBase = Noises.add(valleyBase, top);
		
		Noise height = Noises.terrace(valleyBase, 0.9F, 0.15F, 0.35F, 0.4F, 4);
		height = Noises.add(height, surface);
		height = Noises.mul(height, 0.475F * verticalScale);
		return height;
	}

	public static Noise badlands(@Deprecated Seed seed) {
		Noise mask = Noises.perlin(seed.next(), 270, 3);
		mask = Noises.clamp(mask, 0.35F, 0.65F);
		mask = Noises.map(mask, 0.0F, 1.0F);
		
		Noise hills = Noises.perlinRidge(seed.next(), 275, 4);
		hills = Noises.warpPerlin(hills, seed.next(), 400, 2, 100.0F);
		hills = Noises.warpPerlin(hills, seed.next(), 18, 1, 20.0F);
		hills = Noises.mul(hills, mask);
		
		float modulation = 0.4F;
		float alpha = 1.0F - modulation;
		
		Noise mod1 = Noises.warpPerlin(hills, seed.next(), 100, 1, 50.0F);
		mod1 = Noises.mul(mod1, modulation);
		
		Noise lowFreq = Noises.steps(hills, 4, 0.6F, 0.7F);
		lowFreq = Noises.mul(lowFreq, alpha);
		lowFreq = Noises.add(lowFreq, mod1);
		
		Noise highFreq = Noises.steps(hills, 10, 0.6F, 0.7F);
		highFreq = Noises.mul(highFreq, alpha);
		highFreq = Noises.add(highFreq, mod1);
		
		Noise detail = Noises.add(lowFreq, highFreq);
		detail = Noises.alpha(detail, 0.5F);
		
		Noise scaler = Noises.perlin(seed.next(), 200, 3);
		scaler = Noises.mul(scaler, modulation);
		
		Noise mod2 = Noises.mul(hills, scaler);
		
		Noise shape = Noises.steps(hills, 4, 0.65F, 0.75F, Interpolation.CURVE3);
		shape = Noises.mul(shape, alpha);
		shape = Noises.add(shape, mod2);
		shape = Noises.mul(shape, alpha);
		
		Noise height = Noises.mul(shape, detail);
		height = Noises.mul(height, 0.55F);
		height = Noises.add(height, 0.025F);
		return height;
	}

	private static Noise makeMountains(@Deprecated Seed seed, float horizontalScale, float verticalScale, boolean makeFancy) {
		int scaleH = Math.round(410.0F);

		Noise height = Noises.perlinRidge(seed.next(), scaleH, 4, 2.35F, 1.15F);

		Noise scaler = Noises.perlin(seed.next(), 24, 4);
		scaler = Noises.alpha(scaler, 0.075F);
		
		height = Noises.mul(height, scaler);
		height = Noises.warpPerlin(height, seed.next(), 350, 1, 150.0F);
		if(makeFancy) {
			height = makeFancy(seed, height);
		}
		height = Noises.mul(height, 0.7F * verticalScale);
		return height;
	}
	
	private static Noise mountainChain(@Deprecated Seed seed, float horizontalScale, float verticalScale, boolean makeFancy) { 
		return makeMountains(seed, horizontalScale * 2.25F, verticalScale, makeFancy);
	}
	
	private static Noise mountains1(@Deprecated Seed seed, float horizontalScale, float verticalScale, boolean makeFancy) { 
		return makeMountains(seed, horizontalScale, verticalScale, makeFancy);
	}
	
	private static Noise mountains2(@Deprecated Seed seed, float verticalScale, boolean makeFancy) {
		Noise cell = Noises.worleyEdge(seed.next(), 360, EdgeFunction.DISTANCE_2, DistanceFunction.EUCLIDEAN);
		cell = Noises.mul(cell, 1.2F);
		cell = Noises.clamp(cell, 0.0F, 1.0F);
		cell = Noises.warpPerlin(cell, seed.next(), 200, 2, 100.0F);
		
		Noise blur = Noises.perlin(seed.next(), 10, 1);
		blur = Noises.alpha(blur, 0.025F);
		
		Noise surface = Noises.perlinRidge(seed.next(), 125, 4);
		surface = Noises.alpha(surface, 0.37F);
		
		Noise height = Noises.clamp(cell, 0.0F, 1.0F);
		height = Noises.mul(height, blur);
		height = Noises.mul(height, surface);
		height = Noises.pow(height, 1.1F);
		if(makeFancy) { 
			height = makeFancy(seed, height);
		}
		height = Noises.mul(height, 0.645F * verticalScale);
		return height;
	}
	
	private static Noise mountains3(@Deprecated Seed seed, float verticalScale, boolean makeFancy) {
    	Noise cell = Noises.worleyEdge(seed.next(), 400, EdgeFunction.DISTANCE_2, DistanceFunction.EUCLIDEAN);
    	cell = Noises.mul(cell, 1.2F);
    	cell = Noises.clamp(cell, 0.0F, 1.0F);
    	cell = Noises.warpPerlin(cell, seed.next(), 200, 2, 100.0F);

    	Noise blur = Noises.perlin(seed.next(), 10, 1);
    	blur = Noises.alpha(blur, 0.025F);
    	
    	Noise surface = Noises.perlinRidge(seed.next(), 125, 4);
    	surface = Noises.alpha(surface, 0.37F);
    	
    	Noise mountains = Noises.clamp(cell, 0.0F, 1.0F);
    	mountains = Noises.mul(mountains, blur);
    	mountains = Noises.mul(mountains, surface);
    	mountains = Noises.pow(mountains, 1.1F);
    	
    	Noise modulation = Noises.perlin(seed.next(), 50, 1);
    	modulation = Noises.mul(modulation, 0.5F);
    	
    	Noise mask = Noises.perlin(seed.next(), 100, 1);
    	mask = Noises.clamp(mask, 0.5F, 0.95F);
    	mask = Noises.map(mask, 0.0F, 1.0F);
    	
    	Noise slope = Noises.constant(0.45F);
    	
    	Noise height = Noises.advancedTerrace(mountains, modulation, mask, slope, 0.20000000298023224F, 0.44999998807907104F, 24, 1);
    	if(makeFancy) {
        	height = makeFancy(seed, height);
    	}
		height = Noises.mul(height, 0.645F * verticalScale);
		return height;
    }
    
	private static Noise makeFancy(@Deprecated Seed seed, Noise input) {
		Warp domain = Warps.direction(
			Noises.perlin(seed.next(), 10, 1),
			Noises.constant(2.0F)
		);
		Noise erosion = Noises.erosion(input, seed.next(), 2, 0.65F, 128.0F, 0.15F, 3.1F, 0.8F, BlendMode.CONSTANT);
		erosion = Noises.warp(erosion, domain);
		return erosion;
	}
	
	private static ResourceKey<Noise> createKey(String name) {
		return RTFRegistries.createKey(RTFRegistries.NOISE, name);
	}
}
