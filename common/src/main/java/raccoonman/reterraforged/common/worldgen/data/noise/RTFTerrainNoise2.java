package raccoonman.reterraforged.common.worldgen.data.noise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionBorder;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionCell;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionSelector;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionSelector.Region;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.General;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.Terrain;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public final class RTFTerrainNoise2 {
	public static final ResourceKey<Noise> REGION_WARP_X = createKey("region_warp_x");
	public static final ResourceKey<Noise> REGION_WARP_Z = createKey("region_warp_z");
	public static final ResourceKey<Noise> REGION_BORDER = createKey("region_border");
	public static final ResourceKey<Noise> REGION_CELL = createKey("region_cell");
	
	public static final ResourceKey<Noise> VARIANCE = createKey("variance");
	public static final ResourceKey<Noise> EROSION = createKey("erosion");
	public static final ResourceKey<Noise> RIDGE = createKey("ridge");
	
	public static void bootstrap(BootstapContext<Noise> ctx, WorldSettings world, TerrainSettings terrain, Seed seed) {
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
		
		General general = terrain.general;
        int regionWarpScale = 400;
        int regionWarpStrength = 200;
		float regionFrequency = 1.0F / general.terrainRegionSize;

		Seed regionSeed = seed.offset(789124);
		Seed regionWarpSeed = seed.offset(8934);
        Seed terrainSeed = seed.offset(general.terrainSeedOffset);
        Seed mountainSeed = seed.offset(general.terrainSeedOffset);
        Seed blendSeed = seed.offset(general.terrainSeedOffset);
		Seed regionCellSeed = seed.offset(7);

		Noise regionWarpX = registerAndWrap(ctx, REGION_WARP_X, Source.simplex(regionWarpSeed.next(), regionWarpScale, 1));
		Noise regionWarpZ = registerAndWrap(ctx, REGION_WARP_Z, Source.simplex(regionWarpSeed.next(), regionWarpScale, 1));

		Domain regionDomain = Domain.warp(regionWarpX, regionWarpZ, Source.constant(regionWarpStrength));
		Noise regionBorder = registerAndWrap(ctx, REGION_BORDER, new RegionBorder(regionDomain, regionFrequency, 0.0F, 0.5F).shift(regionCellSeed.get()));
		Noise regionCell = registerAndWrap(ctx, REGION_CELL, new RegionCell(regionDomain, regionFrequency).shift(regionCellSeed.get()));
		
		RTFFeatureNoise.bootstrap(ctx, general, world, terrain, regionSeed, terrainSeed, mountainSeed, regionWarpStrength);

		Noise ground = getNoise(noise, RTFFeatureNoise.GROUND);
		
		ctx.register(VARIANCE, createFeatureBlend(noise, blendSeed.split(), general, terrain, 
			regionCell,
			ground,
			RTFFeatureNoise.STEPPE_VARIANCE,
			RTFFeatureNoise.PLAINS_VARIANCE,
			RTFFeatureNoise.DALES_VARIANCE,
			RTFFeatureNoise.HILLS_1_VARIANCE,
			RTFFeatureNoise.HILLS_2_VARIANCE,
			RTFFeatureNoise.TORRIDONIAN_VARIANCE,
			RTFFeatureNoise.PLATEAU_VARIANCE,
			RTFFeatureNoise.BADLANDS_VARIANCE,
			RTFFeatureNoise.MOUNTAINS_VARIANCE,
			RTFFeatureNoise.MOUNTAINS_2_VARIANCE,
			RTFFeatureNoise.MOUNTAINS_3_VARIANCE,
			RTFFeatureNoise.VOLCANO_VARIANCE,
			true
		));
		ctx.register(EROSION, createFeatureBlend(noise, blendSeed.split(), general, terrain, 
			regionCell,
			Source.ZERO,
			RTFFeatureNoise.STEPPE_EROSION,
			RTFFeatureNoise.PLAINS_EROSION,
			RTFFeatureNoise.DALES_EROSION,
			RTFFeatureNoise.HILLS_1_EROSION,
			RTFFeatureNoise.HILLS_2_EROSION,
			RTFFeatureNoise.TORRIDONIAN_EROSION,
			RTFFeatureNoise.PLATEAU_EROSION,
			RTFFeatureNoise.BADLANDS_EROSION,
			RTFFeatureNoise.MOUNTAINS_EROSION,
			RTFFeatureNoise.MOUNTAINS_2_EROSION,
			RTFFeatureNoise.MOUNTAINS_3_EROSION,
			RTFFeatureNoise.VOLCANO_EROSION,
			false
		));
		ctx.register(RIDGE, createFeatureBlend(noise, blendSeed.split(), general, terrain, 
			regionCell,
			Source.ZERO,
			RTFFeatureNoise.STEPPE_RIDGE,
			RTFFeatureNoise.PLAINS_RIDGE,
			RTFFeatureNoise.DALES_RIDGE,
			RTFFeatureNoise.HILLS_1_RIDGE,
			RTFFeatureNoise.HILLS_2_RIDGE,
			RTFFeatureNoise.TORRIDONIAN_RIDGE,
			RTFFeatureNoise.PLATEAU_RIDGE,
			RTFFeatureNoise.BADLANDS_RIDGE,
			RTFFeatureNoise.MOUNTAINS_RIDGE,
			RTFFeatureNoise.MOUNTAINS_2_RIDGE,
			RTFFeatureNoise.MOUNTAINS_3_RIDGE,
			RTFFeatureNoise.VOLCANO_RIDGE,
			false
		));
	}

    private static Noise createFeatureBlend(HolderGetter<Noise> noise, Seed seed, General general, TerrainSettings terrain, Noise regionCell, Noise ground, ResourceKey<Noise> steppeKey, ResourceKey<Noise> plainsKey, ResourceKey<Noise> dalesKey, ResourceKey<Noise> hills1Key, ResourceKey<Noise> hills2Key, ResourceKey<Noise> torridonianKey, ResourceKey<Noise> plateauKey, ResourceKey<Noise> badlandsKey, ResourceKey<Noise> mountainsKey, ResourceKey<Noise> mountains2Key, ResourceKey<Noise> mountains3Key, ResourceKey<Noise> volcanoKey, boolean scale) {
    	List<Region> regions = new ArrayList<>();
		regions.add(makeRegion(ground, getNoise(noise, steppeKey), terrain.steppe, scale));
		regions.add(makeRegion(ground, getNoise(noise, plainsKey), terrain.plains, scale));
		regions.add(makeRegion(ground, getNoise(noise, dalesKey), terrain.dales, scale));
		regions.add(makeRegion(ground, getNoise(noise, hills1Key), terrain.hills, scale));
		regions.add(makeRegion(ground, getNoise(noise, hills2Key), terrain.hills, scale));
		regions.add(makeRegion(ground, getNoise(noise, torridonianKey), terrain.torridonian, scale));
		regions.add(makeRegion(ground, getNoise(noise, plateauKey), terrain.plateau, scale));
		regions.add(makeRegion(ground, getNoise(noise, badlandsKey), terrain.badlands, scale));
		regions = blendRegions(regions, ground, seed, general.terrainRegionSize / 2);
		regions.add(makeRegion(ground, getNoise(noise, badlandsKey), terrain.badlands, scale));
		regions.add(makeRegion(ground, getNoise(noise, mountainsKey), terrain.mountains, scale));
		regions.add(makeRegion(ground, getNoise(noise, mountains2Key), terrain.mountains, scale));
		regions.add(makeRegion(ground, getNoise(noise, mountains3Key), terrain.mountains, scale));
		regions.add(makeRegion(ground, getNoise(noise, volcanoKey), terrain.volcano, scale));
        Collections.shuffle(regions, new Random(seed.next()));
		return new RegionSelector(regionCell, regions);
	}
	
	private static List<Region> blendRegions(List<Region> input, Noise base, Seed seed, int scale) {
        int length = input.size();
        for (int i = 1; i < input.size(); ++i) {
            length += input.size() - i;
        }
        List<Region> result = new ArrayList<>(length);
        for (int j = 0; j < length; ++j) {
            result.add(null);
        }
        int j = 0;
        int k = input.size();
        while (j < input.size()) {
        	Region t1 = input.get(j);
            result.set(j, t1);
            for (int l = j + 1; l < input.size(); ++l, ++k) {
            	Region t2 = input.get(l);
                Region t3 = blend(t1, t2, base, seed, scale);
                result.set(k, t3);
            }
            ++j;
        }
        return result;
	}
	
	private static Region blend(Region region1, Region region2, Noise base, Seed seed, int scale) {
		Noise combined = Source.perlin(seed.next(), scale, 1).warp(seed.next(), scale / 2, 2, scale / 2.0).blend(region1.variance(), region2.variance(), 0.5, 0.25);// removing the clamp here shouldn't do anything right?? .clamp(0.0, 1.0);
		float weight = (region1.weight() + region2.weight()) / 2.0f;
		return new Region(weight, base, combined, 1.0F, 1.0F);
	}
	
	private static Region makeRegion(Noise base, Noise variance, Terrain settings, boolean scale) {
		return new Region(settings.weight, base, variance, scale ? settings.baseScale : 1.0F, scale ? settings.verticalScale : 1.0F);
	}
	
	protected static ResourceKey<Noise> createKey(String string) {
		return RTFNoiseData.createKey("terrain/" + string);
	}
	
	private static Noise registerAndWrap(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise noise) {
		return new HolderNoise(ctx.register(key, noise));
	}
	
	private static Noise getNoise(HolderGetter<Noise> noise, ResourceKey<Noise> key) {
		return new HolderNoise(noise.getOrThrow(key));
	}
}
