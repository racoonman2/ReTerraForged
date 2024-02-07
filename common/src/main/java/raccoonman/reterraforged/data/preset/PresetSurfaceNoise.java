package raccoonman.reterraforged.data.preset;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.data.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.data.preset.settings.SurfaceSettings;
import raccoonman.reterraforged.data.preset.settings.WorldSettings;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.util.Scaling;

public class PresetSurfaceNoise {
	public static final ResourceKey<Noise> RANDOM = createKey("random");

	public static final ResourceKey<Noise> HEIGHT_VARIANCE = createKey("height_variance");
	public static final ResourceKey<Noise> STEEPNESS_VARIANCE = createKey("steepness_variance");

	public static final ResourceKey<Noise> ERODED_DIRT = createKey("eroded_dirt");
	public static final ResourceKey<Noise> ERODED_ROCK = createKey("eroded_rock");
	
	public static final ResourceKey<Noise> DESERT = createKey("desert");
	public static final ResourceKey<Noise> SWAMP = createKey("swamp");
	public static final ResourceKey<Noise> FOREST = createKey("forest");
	public static final ResourceKey<Noise> RIVER_BANK = createKey("river_bank");
		
	public static final ResourceKey<Noise> ICEBERG_DEEP_SHAPE = createKey("iceberg/deep/shape");
	public static final ResourceKey<Noise> ICEBERG_DEEP_MASK = createKey("iceberg/deep/mask");
	public static final ResourceKey<Noise> ICEBERG_DEEP_FADE_DOWN = createKey("iceberg/deep/fade_down");
	public static final ResourceKey<Noise> ICEBERG_DEEP_FADE_UP = createKey("iceberg/deep/fade_up");
	public static final ResourceKey<Noise> ICEBERG_DEEP_UP = createKey("iceberg/deep/up");
	public static final ResourceKey<Noise> ICEBERG_DEEP_DOWN = createKey("iceberg/deep/down");
	public static final ResourceKey<Noise> ICEBERG_DEEP_TOP = createKey("iceberg/deep/top");

	public static final ResourceKey<Noise> ICEBERG_SHALLOW_SHAPE = createKey("iceberg/shallow/shape");
	public static final ResourceKey<Noise> ICEBERG_SHALLOW_MASK = createKey("iceberg/shallow/mask");
	public static final ResourceKey<Noise> ICEBERG_SHALLOW_FADE_DOWN = createKey("iceberg/shallow/fade_down");
	public static final ResourceKey<Noise> ICEBERG_SHALLOW_FADE_UP = createKey("iceberg/shallow/fade_up");
	public static final ResourceKey<Noise> ICEBERG_SHALLOW_UP = createKey("iceberg/shallow/up");
	public static final ResourceKey<Noise> ICEBERG_SHALLOW_DOWN = createKey("iceberg/shallow/down");
	public static final ResourceKey<Noise> ICEBERG_SHALLOW_TOP = createKey("iceberg/shallow/top");

	public static final ResourceKey<Noise> STRATA_REGION = createKey("strata/region");
	public static final ResourceKey<Noise> STRATA_DEPTH = createKey("strata/depth");
	
	public static final int GENERATOR_RESOURCE_SEED_OFFSET = 746382634;
	
	public static void bootstrap(Preset preset, BootstapContext<Noise> ctx) {
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		
		SurfaceSettings surfaceSettings = preset.surface();
		SurfaceSettings.Erosion erosion = surfaceSettings.erosion();
		
		MiscellaneousSettings miscellaneousSettings = preset.miscellaneous();
		
		Scaling scaling = Scaling.make(properties.terrainScaler(), properties.seaLevel);

		Noise random = PresetNoiseData.registerAndWrap(ctx, RANDOM, Noises.white(0, 1)); //TODO give this the correct seed

		ctx.register(HEIGHT_VARIANCE, Noises.mul(random, erosion.heightModifier / 255.0F));
		ctx.register(STEEPNESS_VARIANCE, Noises.mul(random, erosion.slopeModifier / 255.0F));

		ctx.register(ERODED_DIRT, makeErosion(erosion.dirtVariance, erosion.dirtMin));
		ctx.register(ERODED_ROCK, makeErosion(erosion.rockVariance, erosion.rockMin));
		
		ctx.register(DESERT, makeDesert(scaling));
		ctx.register(SWAMP, makeSwamp());
		ctx.register(FOREST, makeForest());
		ctx.register(RIVER_BANK, makeRiverBank());
		
		registerIceberg(ctx, ICEBERG_DEEP_SHAPE, ICEBERG_DEEP_MASK, ICEBERG_DEEP_FADE_DOWN, ICEBERG_DEEP_FADE_UP, ICEBERG_DEEP_UP, ICEBERG_DEEP_DOWN, ICEBERG_DEEP_TOP, scaling, 30, 30, 0);
		registerIceberg(ctx, ICEBERG_SHALLOW_SHAPE, ICEBERG_SHALLOW_MASK, ICEBERG_SHALLOW_FADE_DOWN, ICEBERG_SHALLOW_FADE_UP, ICEBERG_SHALLOW_UP, ICEBERG_SHALLOW_DOWN, ICEBERG_SHALLOW_TOP, scaling, 20, 15, 6);

		ctx.register(STRATA_REGION, makeStrataRegion(miscellaneousSettings.strataRegionSize));
		ctx.register(STRATA_DEPTH, makeStrataDepth());
	}
	
	private static Noise makeErosion(int scale, int bias) {
		Noise noise = Noises.perlin(0, 100, 1);
		noise = Noises.mul(noise, scale / 255.0F);
		noise = Noises.add(noise, bias / 255.0F);
		return noise;
	}
	
	private static Noise makeDesert(Scaling scaling) {
		int seed = 12;
		
		Noise noise = Noises.perlin(seed++, 8, 1);
		noise = Noises.mul(noise, scaling.blocks(16));
		return generatorResource(noise);
	}
	
	private static Noise makeSwamp() {
		int seed = 13;
		
		Noise noise = Noises.simplex(seed++, 40, 2);
		noise = Noises.warpWhite(noise, seed, 2, 4.0F);
		return generatorResource(noise);
	}

	private static Noise makeForest() {
		int seed = 15;
		
		Noise noise = Noises.simplex(seed++, 50, 2);
		noise = Noises.warpWhite(noise, seed, 2, 3);
		return generatorResource(noise);
	}

	private static Noise makeRiverBank() {
		int seed = 15;
		
		Noise noise = Noises.simplex(seed++, 5, 5);
		noise = Noises.warpWhite(noise, seed, 2, 3);
		return generatorResource(noise);
	}
	
	private static void registerIceberg(BootstapContext<Noise> ctx, ResourceKey<Noise> shapeKey, ResourceKey<Noise> maskKey, ResourceKey<Noise> fadeDownKey, ResourceKey<Noise> fadeUpKey, ResourceKey<Noise> upKey, ResourceKey<Noise> downKey, ResourceKey<Noise> topKey, Scaling scaling, int height, int depth, int seedOffset) {
		int seed = GENERATOR_RESOURCE_SEED_OFFSET + seedOffset;
		
		Noise shape = Noises.perlin(seed++, 65, 3);
		shape = Noises.warpPerlin(shape, seed++, 15, 1, 10);
		shape = PresetNoiseData.registerAndWrap(ctx, shapeKey, Noises.cache2d(shape));
		
		Noise mask = PresetNoiseData.registerAndWrap(ctx, maskKey, Noises.threshold(shape, 0.0F, 1.0F, 0.6F));
		
		Noise fadeDown = Noises.clamp(shape, 0.6F, 0.725F);
		fadeDown = PresetNoiseData.registerAndWrap(ctx, fadeDownKey, Noises.map(fadeDown, 0.0F, 1.0F));
		
		Noise fadeUp = Noises.clamp(shape, 0.6F, 0.65F);
		fadeUp = PresetNoiseData.registerAndWrap(ctx, fadeUpKey, Noises.map(fadeUp, 0.0F, 1.0F));

		Noise up = Noises.perlinRidge(seed++, 50, 3);
		up  = Noises.mul(up, mask);
		up = Noises.mul(up, fadeUp);
		ctx.register(upKey, Noises.mul(up, scaling.blocks(height)));
		
		Noise down = Noises.perlinRidge(seed++, 60, 3);
		down = Noises.mul(down, mask);
		down = Noises.mul(down, fadeDown);
		ctx.register(downKey, Noises.mul(down, scaling.blocks(depth)));
		
		Noise top = Noises.perlinRidge(seed++, 25, 2);
		top = Noises.mul(top, scaling.blocks(3));
		ctx.register(topKey, Noises.add(top, scaling.blocks(2)));
	}
	
	private static Noise makeStrataRegion(int regionSize) {
		Noise noise = Noises.worley(0, regionSize);
		noise = Noises.warpPerlin(noise, 1, regionSize / 4, 2, regionSize / 2.0F);
		noise = Noises.warpPerlin(noise, 2, 15, 2, 30);
		return noise;
	}
	
	private static Noise makeStrataDepth() {
		return Noises.perlin(0, 128, 3);
	}
	
	private static Noise generatorResource(Noise noise) {
		return Noises.shiftSeed(noise, GENERATOR_RESOURCE_SEED_OFFSET);
	}
	
	public static ResourceKey<Noise> createKey(String name) {
        return PresetNoiseData.createKey("surface/" + name);
	}
}
