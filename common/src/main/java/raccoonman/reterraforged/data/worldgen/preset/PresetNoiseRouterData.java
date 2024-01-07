package raccoonman.reterraforged.data.worldgen.preset;

import java.util.stream.Stream;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.settings.CaveSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldSettings;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.densityfunction.CellSampler;
import raccoonman.reterraforged.world.worldgen.densityfunction.RTFDensityFunctions;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.terrablender.TBCompat;

public class PresetNoiseRouterData {
	public static final ResourceKey<DensityFunction> HEIGHT = createKey("height");
	public static final ResourceKey<DensityFunction> GRADIENT = createKey("gradient");
	public static final ResourceKey<DensityFunction> EROSION = createKey("erosion");
	public static final ResourceKey<DensityFunction> SEDIMENT = createKey("sediment");
	
	private static final float SCALER = 128.0F;
	private static final float UNIT = 1.0F / SCALER;
	
    public static void bootstrap(Preset preset, BootstapContext<DensityFunction> ctx) {
        HolderGetter<Noise> noises = ctx.lookup(RTFRegistries.NOISE);
        HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
        
    	WorldSettings worldSettings = preset.world();
    	WorldSettings.Properties properties = worldSettings.properties;
    	
    	CaveSettings caveSettings = preset.caves();
    	
        int worldHeight = properties.worldHeight;
        int worldDepth = properties.worldDepth;
        
        ctx.register(NoiseRouterData.CONTINENTS, RTFDensityFunctions.cell(CellSampler.Field.CONTINENT));
        ctx.register(NoiseRouterData.EROSION, RTFDensityFunctions.cell(CellSampler.Field.EROSION));
        ctx.register(NoiseRouterData.RIDGES, RTFDensityFunctions.cell(CellSampler.Field.WEIRDNESS));

        DensityFunction height = NoiseRouterData.registerAndWrap(ctx, HEIGHT, RTFDensityFunctions.cell(CellSampler.Field.HEIGHT));
        DensityFunction offset = NoiseRouterData.registerAndWrap(ctx, NoiseRouterData.OFFSET, DensityFunctions.add(DensityFunctions.constant(-1.0F), DensityFunctions.mul(RTFDensityFunctions.clampToNearestUnit(height, properties.terrainScaler()), DensityFunctions.constant(2.0D))));
        ctx.register(NoiseRouterData.DEPTH, DensityFunctions.add(DensityFunctions.yClampedGradient(-worldDepth, worldHeight, yGradientRange(-worldDepth), yGradientRange(worldHeight)), offset));
        ctx.register(NoiseRouterData.BASE_3D_NOISE_OVERWORLD, DensityFunctions.zero());
        ctx.register(NoiseRouterData.JAGGEDNESS, jaggednessPerformanceHack());
                
        ctx.register(NoiseRouterData.NOODLE, noodle(-worldDepth, worldHeight, 1.0F - caveSettings.noodleCaveProbability, densityFunctions, noiseParams));
        ctx.register(NoiseRouterData.ENTRANCES, probabilityDensity(caveSettings.entranceCaveProbability, NoiseRouterData.entrances(densityFunctions, noiseParams)));
        ctx.register(NoiseRouterData.SPAGHETTI_2D, probabilityDensity(caveSettings.spaghettiCaveProbability, spaghetti2D(-worldDepth, worldHeight, densityFunctions, noiseParams)));
        
        ctx.register(GRADIENT, RTFDensityFunctions.cell(CellSampler.Field.GRADIENT));
        ctx.register(EROSION, RTFDensityFunctions.cell(CellSampler.Field.HEIGHT_EROSION));
        ctx.register(SEDIMENT, RTFDensityFunctions.cell(CellSampler.Field.SEDIMENT));
        ctx.register(TBCompat.uniquenessKey(), RTFDensityFunctions.cell(CellSampler.Field.BIOME_REGION));
    }
    
    protected static NoiseRouter overworld(Preset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, HolderGetter<Noise> noises) {
    	WorldSettings worldSettings = preset.world();
    	WorldSettings.Properties properties = worldSettings.properties;
    	int worldDepth = properties.worldDepth;
    	
    	CaveSettings caves = preset.caves();
    	float cheeseCaveDepthOffset = caves.cheeseCaveDepthOffset;
    	
    	DensityFunction aquiferBarrier = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction aquiferFluidLevelFloodedness = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction aquiferFluidLevelSpread = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction aquiferLava = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction temperature = RTFDensityFunctions.cell(CellSampler.Field.TEMPERATURE);
        DensityFunction vegetation = RTFDensityFunctions.cell(CellSampler.Field.MOISTURE);
        DensityFunction factor = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.FACTOR);
        DensityFunction depth = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.DEPTH);
        DensityFunction initialDensity = NoiseRouterData.noiseGradientDensity(DensityFunctions.cache2d(factor), depth);
        DensityFunction slopedCheese = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.SLOPED_CHEESE);
//        DensityFunction entrances = DensityFunctions.min(slopedCheese, DensityFunctions.mul(DensityFunctions.constant(5.0), NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.ENTRANCES)));
//        DensityFunction slopedCheeseCaves = DensityFunctions.rangeChoice(slopedCheese, -1000000.0, 1.5625, entrances, NoiseRouterData.underground(densityFunctions, noiseParams, slopedCheese));
//        DensityFunction finalDensity = DensityFunctions.min(NoiseRouterData.postProcess(slideOverworld(slopedCheeseCaves, -worldDepth)), NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.NOODLE));

//        
//        
        DensityFunction entrances = caves.entranceCaveProbability > 0.0F ? DensityFunctions.min(slopedCheese, DensityFunctions.mul(DensityFunctions.constant(5.0D), DensityFunctions.interpolated(NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.ENTRANCES)))) : slopedCheese;

        DensityFunction slopedCheeseRange = DensityFunctions.mul(DensityFunctions.rangeChoice(slopedCheese, -1000000.0D, cheeseCaveDepthOffset, entrances, DensityFunctions.interpolated(slideOverworld(underground(caves.cheeseCaveProbability, densityFunctions, noiseParams, slopedCheese), -worldDepth))), DensityFunctions.constant(0.64)).squeeze();
        DensityFunction finalDensity = DensityFunctions.min(slopedCheeseRange, NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.NOODLE));
        
        DensityFunction y = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.Y);
        int minY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int maxY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction oreVeininess = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), minY, maxY, 0);
        DensityFunction oreVeinA = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), minY, maxY, 0).abs();
        DensityFunction oreVeinB = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), minY, maxY, 0).abs();
        DensityFunction oreVein = DensityFunctions.add(DensityFunctions.constant(-0.08F), DensityFunctions.max(oreVeinA, oreVeinB));
        DensityFunction oreGap = DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_GAP));
        return new NoiseRouter(aquiferBarrier, aquiferFluidLevelFloodedness, aquiferFluidLevelSpread, aquiferLava, temperature, vegetation, NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.CONTINENTS), NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.EROSION), DensityFunctions.add(depth, DensityFunctions.constant(-0.275D)), NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.RIDGES), slideOverworld(DensityFunctions.add(initialDensity, DensityFunctions.constant(UNIT * -90)).clamp(-64.0, 64.0), -worldDepth), finalDensity, oreVeininess, oreVein, oreGap);
	}

    private static DensityFunction underground(float cheeseCaveProbability, HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction slopedCheese) {
        DensityFunction spaghetti2d = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.SPAGHETTI_2D);
        DensityFunction spaghettiRoughnessFunction = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction caveLayerNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction caveLayer = DensityFunctions.mul(DensityFunctions.constant(4.0), caveLayerNoise.square());
        DensityFunction caveCheese = probabilityDensity(cheeseCaveProbability, DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666));
        DensityFunction slopedCaves = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), caveCheese).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), slopedCheese)).clamp(0.0, 0.5));
        DensityFunction slopedCaveLayered = DensityFunctions.add(caveLayer, slopedCaves);
        DensityFunction underground = DensityFunctions.min(DensityFunctions.min(slopedCaveLayered, NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.ENTRANCES)), DensityFunctions.add(spaghetti2d, spaghettiRoughnessFunction));
        DensityFunction pillars = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.PILLARS);
        DensityFunction pillarRange = DensityFunctions.rangeChoice(pillars, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), pillars);
        return DensityFunctions.max(underground, pillarRange);
    }

    private static DensityFunction spaghetti2D(int minY, int maxY, HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction modulator = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction sampler = DensityFunctions.weirdScaledSampler(modulator, noiseParams.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction elevation = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(minY, 8), 8.0);
        DensityFunction thicknessModulator = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction elevationGradient = DensityFunctions.add(elevation, DensityFunctions.yClampedGradient(minY, maxY, minY / -8.0D, maxY / -8.0D)).abs();
        DensityFunction normal = DensityFunctions.add(elevationGradient, thicknessModulator).cube();
        DensityFunction weird = DensityFunctions.add(sampler, DensityFunctions.mul(DensityFunctions.constant(0.083D), thicknessModulator));
        return DensityFunctions.max(weird, normal).clamp(-1.0D, 1.0D);
    }

    private static DensityFunction noodle(int minY, int maxY, float threshold, HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
    	int baseY = minY + 4;
        
    	DensityFunction y = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.Y);
        DensityFunction selector = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE), 1.0, 1.0), baseY, maxY, -1);
        DensityFunction thickness = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), baseY, maxY, 0);
        DensityFunction ridgeA = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), baseY, maxY, 0);
        DensityFunction ridgeB = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), baseY, maxY, 0);
        DensityFunction ridge = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(ridgeA.abs(), ridgeB.abs()));
        return DensityFunctions.rangeChoice(selector, -1000000.0, threshold, DensityFunctions.constant(64.0), DensityFunctions.add(thickness, ridge));
    }
    
    private static DensityFunction slideOverworld(DensityFunction function, int minY) {
        return slide(function, minY, 0, 24, UNIT * 15);
    }
    
    private static DensityFunction slide(DensityFunction function, int minY, int bottomGradientStart, int bottomGradientEnd, double bottomGradientTarget) {
        DensityFunction bottomGradient = DensityFunctions.yClampedGradient(minY + bottomGradientStart, minY + bottomGradientEnd, 0.0, 1.0);
        return DensityFunctions.lerp(bottomGradient, bottomGradientTarget, function);
    }
    
    /* 
     * the multiply function doesnt sample the second input
     * if the first input is zero, however this optimization doesn't get
     * applied if either input is a Constant, so if we use 
     * use DensityFunctions.zero() Noises.JAGGED will still get sampled
     */
    private static DensityFunction jaggednessPerformanceHack() {
    	return DensityFunctions.add(DensityFunctions.zero(), DensityFunctions.zero());
    }

    // do this a different way, since this affects the size of the cave as well
    @Deprecated
    private static DensityFunction probabilityDensity(float probability, DensityFunction function) {
    	if(probability == 0.0F) {
    		return DensityFunctions.constant(1.0F);
    	}
    	return DensityFunctions.add(DensityFunctions.constant(1.0F - probability), function);
    }
    
    private static float yGradientRange(float range) {
    	return 1.0F + (-range / SCALER);
    }
    
    private static ResourceKey<DensityFunction> createKey(String string) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, RTFCommon.location(string));
    }
}