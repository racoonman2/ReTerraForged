package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public final class RTFTerrainNoise {
	public static final ResourceKey<Noise> UNIT = createKey("unit");

	public static final ResourceKey<Noise> CONTINENT = createKey("continent");
	public static final ResourceKey<Noise> HEIGHT = createKey("height");
	public static final ResourceKey<Noise> EROSION = createKey("erosion");
	public static final ResourceKey<Noise> RIDGE = createKey("ridge");
	
	public static void bootstrap(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, WorldSettings worldSettings, TerrainSettings terrain, Seed seed) {
		ErosionLevels.bootstrap(ctx);
		RidgeLevels.bootstrap(ctx);
		TerrainTypes.bootstrap(ctx, noise, worldSettings, terrain, seed);
		
		WorldSettings.Continent continentSettings = worldSettings.continent;
		WorldSettings.ControlPoints controlPoints = worldSettings.controlPoints;

		ctx.register(UNIT, Source.constant(1.0F / 256.0F));
		
		Noise continent = RTFNoise.registerAndWrap(ctx, CONTINENT, continentSettings.continentType.create(worldSettings, seed.offset(21345)));
		Noise land = RTFNoise.lookup(noise, TerrainTypes.DALES_HEIGHT);
		
		ctx.register(HEIGHT, new ContinentLerper(continent, RTFNoise.lookup(noise, TerrainTypes.DEEP_OCEAN_HEIGHT), RTFNoise.lookup(noise, TerrainTypes.SHALLOW_OCEAN_HEIGHT), RTFNoise.lookup(noise, TerrainTypes.COAST_HEIGHT), Interpolation.CURVE3, land, Interpolation.LINEAR, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast, controlPoints.inland));
		ctx.register(EROSION, RTFNoise.lookup(noise, TerrainTypes.DALES_EROSION));
		ctx.register(RIDGE, RTFNoise.lookup(noise, TerrainTypes.DALES_RIDGE));
	}

	protected static ResourceKey<Noise> createKey(String name) {
		return RTFNoise.createKey("terrain/" + name);
	}
}
