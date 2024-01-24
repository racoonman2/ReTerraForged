package raccoonman.reterraforged.world.worldgen.climate;

import raccoonman.reterraforged.data.preset.ClimateSettings;
import raccoonman.reterraforged.data.preset.WorldSettings;
import raccoonman.reterraforged.data.preset.WorldSettings.ControlPoints;
import raccoonman.reterraforged.world.worldgen.biome.type.BiomeType;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.continent.Continent;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.noise.function.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.module.LegacyMoisture;
import raccoonman.reterraforged.world.worldgen.noise.module.LegacyTemperature;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.util.Seed;

public class ClimateModule {
	private int seed;
	private float biomeFreq;
	private float warpStrength;
	private Noise warpX;
	private Noise warpZ;
	private Noise moisture;
	private Noise temperature;
	private Noise macroBiomeNoise;
	private Continent continent;
	private ControlPoints controlPoints;
	private Levels levels;
	
	public ClimateModule(Seed seed, Continent continent, WorldSettings.ControlPoints controlPoints, ClimateSettings climateSettings, Levels levels) {
		int biomeSize = climateSettings.biomeShape.biomeSize;
		
		float tempScaler = (float) climateSettings.temperature.scale;
		float moistScaler = climateSettings.moisture.scale * 2.5F;
		float biomeFreq = 1.0F / biomeSize;
		float moistureSize = moistScaler * biomeSize;
		float temperatureSize = tempScaler * biomeSize;
		
		int moistScale = NoiseUtil.round(moistureSize * biomeFreq);
		int tempScale = NoiseUtil.round(temperatureSize * biomeFreq);
		int warpScale = climateSettings.biomeShape.biomeWarpScale;
		
		this.continent = continent;
		this.seed = seed.next();
		this.biomeFreq = 1.0F / biomeSize;
		this.controlPoints = controlPoints;
		this.warpStrength = (float) climateSettings.biomeShape.biomeWarpStrength;
		this.levels = levels;
		
		Noise warpX = Noises.simplex(seed.next(), warpScale, 2);
		warpX = Noises.add(warpX, -0.5F);
		this.warpX = warpX;
		
		Noise warpZ = Noises.simplex(seed.next(), warpScale, 2);
		warpZ = Noises.add(warpZ, -0.5F);
		this.warpZ = warpZ;
		
		Seed moistureSeed = seed.offset(climateSettings.moisture.seedOffset);
		
		Noise moistureSource = Noises.simplex(moistureSeed.next(), moistScale, 1);
		moistureSource = Noises.clamp(moistureSource, 0.125F, 0.875F);
		moistureSource = Noises.map(moistureSource, 0.0F, 1.0F);
		moistureSource = Noises.frequency(moistureSource, 0.5F, 1.0F);
		
		Noise moisture = new LegacyMoisture(moistureSource, climateSettings.moisture.falloff);
		moisture = climateSettings.moisture.apply(moisture);
		moisture = Noises.warpPerlin(moisture, moistureSeed.next(), Math.max(1, moistScale / 2), 1, moistScale / 4.0F);
		moisture = Noises.warpPerlin(moisture, moistureSeed.next(), Math.max(1, moistScale / 6), 2, moistScale / 12.0F);
		this.moisture = moisture;
		
		Seed tempSeed = seed.offset(climateSettings.temperature.seedOffset);
		Noise temperature = new LegacyTemperature(1.0F / tempScale, climateSettings.temperature.falloff);
		temperature = climateSettings.temperature.apply(temperature);
		temperature = Noises.warpPerlin(temperature, tempSeed.next(), tempScale * 4, 2, tempScale * 4);
		temperature = Noises.warpPerlin(temperature, tempSeed.next(), tempScale, 1, tempScale);
		this.temperature = temperature;
		
		Noise macroBiomeNoise = Noises.worley(seed.next(), climateSettings.biomeShape.macroNoiseSize);
		this.macroBiomeNoise = macroBiomeNoise;
	}

	public void apply(Cell cell, float x, float z, float originalX, float originalZ) {
		this.apply(cell, x, z, originalX, originalZ, true);
	}

	public void apply(Cell cell, float x, float z, float originalX, float originalZ, boolean mask) {
		float ox = this.warpX.compute(x, z, 0) * this.warpStrength;
		float oz = this.warpZ.compute(x, z, 0) * this.warpStrength;
		x += ox;
		z += oz;
		x *= this.biomeFreq;
		z *= this.biomeFreq;
		int xr = NoiseUtil.floor(x);
		int zr = NoiseUtil.floor(z);
		int cellX = xr;
		int cellZ = zr;
		float centerX = x;
		float centerZ = z;
		float edgeDistance = 999999.0F;
		float edgeDistance2 = 999999.0F;
		DistanceFunction dist = DistanceFunction.EUCLIDEAN;
		for (int dz = -1; dz <= 1; ++dz) {
			for (int dx = -1; dx <= 1; ++dx) {
				int cx = xr + dx;
				int cz = zr + dz;
				Vec2f vec = NoiseUtil.cell(this.seed, cx, cz);
				float cxf = cx + vec.x();
				float czf = cz + vec.y();
				float distance = dist.apply(cxf - x, czf - z);
				if (distance < edgeDistance) {
					edgeDistance2 = edgeDistance;
					edgeDistance = distance;
					centerX = cxf;
					centerZ = czf;
					cellX = cx;
					cellZ = cz;
				} else if (distance < edgeDistance2) {
					edgeDistance2 = distance;
				}
			}
		}
		cell.biomeRegionId = this.cellValue(this.seed , cellX, cellZ);
		cell.regionMoisture = this.moisture.compute(centerX, centerZ, 0);
		cell.regionTemperature = this.temperature.compute(centerX, centerZ, 0);
		cell.macroBiomeId = this.macroBiomeNoise.compute(centerX, centerZ, 0);
		int posX = NoiseUtil.floor(centerX / this.biomeFreq);
		int posZ = NoiseUtil.floor(centerZ / this.biomeFreq);
		float continentEdge = this.continent.getLandValue(posX, posZ);
		if (mask) {
			cell.biomeRegionEdge = this.edgeValue(edgeDistance, edgeDistance2);
			this.modifyTerrain(cell, continentEdge);
		}
		cell.regionMoisture = this.modifyMoisture(cell.regionMoisture, continentEdge);

		cell.biome = BiomeType.get(cell.regionTemperature, cell.regionMoisture);
		cell.regionTemperature = this.modifyTemp(cell.height, cell.regionTemperature, originalX, originalZ);

        cell.temperature = cell.biome.getTemperature(cell.biomeRegionId);
        cell.moisture = cell.biome.getMoisture(cell.biomeRegionId);
	}

	private float modifyTemp(float height, float temp, float x, float z) {
		if (height > 0.75F) {
			return Math.max(0.0F, temp - 0.05F);
		}
		if (height > 0.45F) {
			float delta = (height - 0.45F) / 0.3F;
			return Math.max(0.0F, temp - delta * 0.05F);
		}
		height = Math.max(this.levels.ground, height);
		if (height >= this.levels.ground) {
			float delta = 1.0F - (height - this.levels.ground) / (0.45F - this.levels.ground);
			return Math.min(1.0F, temp + delta * 0.05F);
		}
		return temp;
	}

	private float modifyMoisture(float moisture, float continentEdge) {
		float limit = 0.75F;
		float range = 1.0F - limit;
		if (continentEdge < limit) {
			float alpha = (limit - continentEdge) / range;
			float multiplier = 1.0F + alpha * range;
			return NoiseUtil.clamp(moisture * multiplier, 0.0F, 1.0F);
		} else {
			float alpha = (continentEdge - limit) / range;
			float multiplier = 1.0F - alpha * range;
			return moisture *= multiplier;
		}
	}

	private void modifyTerrain(Cell cell, float continentEdge) {
		if (cell.terrain.isOverground() && !cell.terrain.overridesCoast() && continentEdge <= this.controlPoints.coastMarker()) {
			cell.terrain = TerrainType.COAST;
		}
	}

	private float cellValue(int seed, int cellX, int cellY) {
		float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
		return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
	}

	private float edgeValue(float distance, float distance2) {
		EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
		float value = edge.apply(distance, distance2);
		value = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
		return value;
	}
}
