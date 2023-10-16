package raccoonman.reterraforged.common.worldgen.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionBorder;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionCell;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionLerper;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionSelector;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.General;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.Terrain;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public final class RTFTerrainNoise {
	protected static final ResourceKey<Noise> UNIT = createKey("unit");

	private static final ResourceKey<Noise> TERRAIN_WARP_X = createKey("terrain_warp_x");
	private static final ResourceKey<Noise> TERRAIN_WARP_Y = createKey("terrain_warp_y");
	private static final ResourceKey<Noise> TERRAIN_WARP_POWER = createKey("terrain_warp_power");
	private static final ResourceKey<Noise> TERRAIN_CELL = createKey("terrain_cell");
	private static final ResourceKey<Noise> TERRAIN_BORDER = createKey("terrain_border");
	private static final ResourceKey<Noise> TERRAIN_BLENDER = createKey("blender");
	
	public static final ResourceKey<Noise> CONTINENT = createKey("continent");
	public static final ResourceKey<Noise> HEIGHT = createKey("height");
	public static final ResourceKey<Noise> EROSION = createKey("erosion");
	public static final ResourceKey<Noise> RIDGE = createKey("ridge");
	
	public static void bootstrap(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, WorldSettings worldSettings, TerrainSettings terrainSettings, Seed seed) {
		WorldSettings.Continent continentSettings = worldSettings.continent;
		WorldSettings.ControlPoints controlPoints = worldSettings.controlPoints;
		General generalSettings = terrainSettings.general;
		
		ctx.register(UNIT, Source.constant(1.0F / 256.0F));
		
		List<TerrainType> mixable = new ArrayList<>();
		List<TerrainType> unmixable = new ArrayList<>();
		ErosionLevels.bootstrap(ctx);
		RidgeLevels.bootstrap(ctx);
		TerrainTypes.bootstrap(ctx, noise, worldSettings, terrainSettings, seed, mixable, unmixable);
		
		int terrainRegionSize = generalSettings.terrainRegionSize;
		float terrainRegionFrequency = 1.0F / terrainRegionSize;
		int terrainRegionScale = terrainRegionSize / 2;
		Seed regionWarp = seed.offset(8934);
		Noise terrainWarpX = RTFNoise.registerAndWrap(ctx, TERRAIN_WARP_X, Source.simplex(regionWarp.next(), 400, 1));
		Noise terrainWarpY = RTFNoise.registerAndWrap(ctx, TERRAIN_WARP_Y, Source.simplex(regionWarp.next(), 400, 1));
		Noise terrainWarpPower = RTFNoise.registerAndWrap(ctx, TERRAIN_WARP_POWER, Source.constant(200));
		Domain terrainRegionWarp = Domain.warp(terrainWarpX, terrainWarpY, terrainWarpPower);
		
		Noise terrainCell = RTFNoise.registerAndWrap(ctx, TERRAIN_CELL, new RegionCell(terrainRegionWarp, terrainRegionFrequency));
		Noise terrainBorder = RTFNoise.registerAndWrap(ctx, TERRAIN_BORDER, new RegionBorder(terrainRegionWarp, terrainRegionFrequency, 0.0F, 0.5F));
		Noise terrainBlender = RTFNoise.registerAndWrap(ctx, TERRAIN_BLENDER, Source.perlin(seed.next(), terrainRegionScale, 1).warp(seed.next(), terrainRegionScale / 2, 2, terrainRegionScale / 2.0));
		
		//TODO convert everything below this to density functions so we can use caches
		Noise heightBlend = blend(terrainCell, terrainBlender, seed, terrainRegionScale, mixable, unmixable, (type) -> type.scaledHeight(RTFNoise.lookup(noise, TerrainTypes.GROUND)));
		Noise erosionBlend = blend(terrainCell, terrainBlender, seed, terrainRegionScale, mixable, unmixable, TerrainType::erosion);
		Noise ridgeBlend = blend(terrainCell, terrainBlender, seed, terrainRegionScale, mixable, unmixable, TerrainType::ridge);
		
		TerrainType borders = TerrainTypes.registerBorders(ctx, noise, seed, generalSettings, terrainSettings.steppe);
		Noise height = new RegionLerper(terrainBorder, borders.scaledHeight(RTFNoise.lookup(noise, TerrainTypes.GROUND)), heightBlend);
		Noise erosion = terrainBorder.threshold(0.5D, borders.erosion(), erosionBlend);
		Noise ridge = terrainBorder.threshold(0.5D, borders.ridge(), ridgeBlend);
		
		Noise continent = RTFNoise.registerAndWrap(ctx, CONTINENT, continentSettings.continentType.create(worldSettings, seed.offset(21345)));
		ctx.register(HEIGHT, new ContinentLerper(continent, RTFNoise.lookup(noise, TerrainTypes.DEEP_OCEAN_HEIGHT), RTFNoise.lookup(noise, TerrainTypes.SHALLOW_OCEAN_HEIGHT), RTFNoise.lookup(noise, TerrainTypes.COAST_HEIGHT), Interpolation.CURVE3, height, Interpolation.LINEAR, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast, controlPoints.inland));
		ctx.register(EROSION, erosion);
		ctx.register(RIDGE, ridge);
	}

	private static Noise blend(Noise terrainCell, Noise terrainBlender, Seed seed, int scale, List<TerrainType> mixable, List<TerrainType> unmixable, Function<TerrainType, Noise> getter) {
		mixable = mixable.stream().filter((region) -> region.weight() > 0.0F).toList();
		
		List<TerrainType> regions = new ArrayList<>();
		regions.addAll(combine(mixable, terrainBlender, seed, scale));
		regions.addAll(unmixable);
		return new RegionSelector(terrainCell, regions.stream().map((type) -> {
			return new RegionSelector.Region(type.weight(), getter.apply(type));
		}).toList());
	}
	
	private static List<TerrainType> combine(List<TerrainType> input, Noise terrainBlender, Seed seed, int scale) {
        int length = input.size();
        for (int i = 1; i < input.size(); ++i) {
            length += input.size() - i;
        }
        final List<TerrainType> result = new ArrayList<>(length);
        for (int j = 0; j < length; ++j) {
            result.add(null);
        }
        int j = 0;
        int k = input.size();
        while (j < input.size()) {
            final TerrainType t1 = input.get(j);
            result.set(j, t1);
            for (int l = j + 1; l < input.size(); ++l, ++k) {
                TerrainType t2 = input.get(l);
                TerrainType t3 = combine(t1, t2, terrainBlender, seed, scale);
                result.set(k, t3);
            }
            ++j;
        }
        return result;
    }
	
    private static TerrainType combine(TerrainType t1, TerrainType t2, Noise terrainBlender, Seed seed, int scale) {
        Noise variance = terrainBlender.blend(t1.variance(), t2.variance(), 0.5, 0.25);
        Noise erosion = terrainBlender.threshold(0.5D, t1.erosion(), t2.erosion());
        Noise ridge = terrainBlender.threshold(0.5D, t1.ridge(), t2.ridge());
        float weight = (t1.weight() + t2.weight()) / 2.0F;
        return new TerrainType(weight, variance, erosion, ridge, 1.0F, 1.0F);
    }
    
    protected record TerrainType(float weight, Noise variance, Noise erosion, Noise ridge, float baseScale, float verticalScale) {
    	
    	public Noise scaledHeight(Noise ground) {
    		return ground.mul(Source.constant(this.baseScale)).add(this.variance.mul(Source.constant(this.verticalScale)));
    	}
    	
		public static TerrainType make(Noise variance, Noise erosion, Noise ridge, Terrain settings) {
			return new TerrainType(settings.weight, variance, erosion, ridge, settings.baseScale, settings.verticalScale);
		}
    }
	
	protected static ResourceKey<Noise> createKey(String name) {
		return RTFNoise.createKey("terrain/" + name);
	}
}
