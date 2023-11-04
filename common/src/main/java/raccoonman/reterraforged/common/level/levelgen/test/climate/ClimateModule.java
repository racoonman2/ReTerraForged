package raccoonman.reterraforged.common.level.levelgen.test.climate;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Moisture;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Temperature;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.BiomeType;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.Continent;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.ControlPoints;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

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

	public ClimateModule(Continent continent, GeneratorContext context) {
		Seed seed = context.seed;
		Preset settings = context.settings;
		int biomeSize = settings.climate().biomeShape.biomeSize;
		float tempScaler = (float) settings.climate().temperature.scale;
		float moistScaler = settings.climate().moisture.scale * 2.5F;
		float biomeFreq = 1.0F / biomeSize;
		float moistureSize = moistScaler * biomeSize;
		float temperatureSize = tempScaler * biomeSize;
		int moistScale = NoiseUtil.round(moistureSize * biomeFreq);
		int tempScale = NoiseUtil.round(temperatureSize * biomeFreq);
		int warpScale = settings.climate().biomeShape.biomeWarpScale;
		this.continent = continent;
		this.seed = seed.next();
		this.biomeFreq = 1.0F / biomeSize;
		this.controlPoints = ControlPoints.make(context.settings.world().controlPoints);
		this.warpStrength = (float) settings.climate().biomeShape.biomeWarpStrength;
		this.warpX = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
		this.warpZ = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
		Seed moistureSeed = seed.offset(settings.climate().moisture.seedOffset);
		Noise moisture = new Moisture(moistureSeed.next(), moistScale, settings.climate().moisture.falloff);
		this.moisture = settings.climate().moisture.apply(moisture)
				.warp(moistureSeed.next(), Math.max(1, moistScale / 2), 1, moistScale / 4.0)
				.warp(moistureSeed.next(), Math.max(1, moistScale / 6), 2, moistScale / 12.0);
		Seed tempSeed = seed.offset(settings.climate().temperature.seedOffset);
		Noise temperature = new Temperature(settings.climate().temperature.falloff, 1.0F / tempScale);
		this.temperature = settings.climate().temperature.apply(temperature)
				.warp(tempSeed.next(), tempScale * 4, 2, tempScale * 4).warp(tempSeed.next(), tempScale, 1, tempScale);
		this.macroBiomeNoise = Source.cell(seed.next(), context.settings.climate().biomeShape.macroNoiseSize);
	}

	public void apply(Cell cell, float x, float y, int seed) {
		this.apply(cell, x, y, seed, true);
	}

	public void apply(Cell cell, float x, float y, int seed, boolean mask) {
		float ox = this.warpX.compute(x, y, seed) * this.warpStrength;
		float oz = this.warpZ.compute(x, y, seed) * this.warpStrength;
		x += ox;
		y += oz;
		x *= this.biomeFreq;
		y *= this.biomeFreq;
		int xr = NoiseUtil.floor(x);
		int yr = NoiseUtil.floor(y);
		int cellX = xr;
		int cellY = yr;
		float centerX = x;
		float centerY = y;
		float edgeDistance = 999999.0F;
		float edgeDistance2 = 999999.0F;
		DistanceFunction dist = DistanceFunction.EUCLIDEAN;
		for (int dy = -1; dy <= 1; ++dy) {
			for (int dx = -1; dx <= 1; ++dx) {
				int cx = xr + dx;
				int cy = yr + dy;
				Vec2f vec = NoiseUtil.cell(this.seed + seed, cx, cy);
				float cxf = cx + vec.x();
				float cyf = cy + vec.y();
				float distance = dist.apply(cxf - x, cyf - y);
				if (distance < edgeDistance) {
					edgeDistance2 = edgeDistance;
					edgeDistance = distance;
					centerX = cxf;
					centerY = cyf;
					cellX = cx;
					cellY = cy;
				} else if (distance < edgeDistance2) {
					edgeDistance2 = distance;
				}
			}
		}
		cell.biomeRegionId = this.cellValue(this.seed + seed, cellX, cellY);
		cell.moisture = this.moisture.compute(centerX, centerY, seed);
		cell.temperature = this.temperature.compute(centerX, centerY, seed);
		cell.macroBiomeId = this.macroBiomeNoise.compute(centerX, centerY, seed);
		int posX = NoiseUtil.floor(centerX / this.biomeFreq);
		int posZ = NoiseUtil.floor(centerY / this.biomeFreq);
		float continentEdge = this.continent.getLandValue(posX, posZ, seed);
		if (mask) {
			cell.biomeRegionEdge = this.edgeValue(edgeDistance, edgeDistance2);
			this.modifyTerrain(cell, continentEdge);
		}
		this.modifyMoisture(cell, continentEdge);
		cell.biome = BiomeType.get(cell.temperature, cell.moisture);
	}

	private void modifyMoisture(Cell cell, float continentEdge) {
		float limit = 0.75F;
		float range = 1.0F - limit;
		if (continentEdge < limit) {
			float alpha = (limit - continentEdge) / range;
			float multiplier = 1.0F + alpha * range;
			cell.moisture = NoiseUtil.clamp(cell.moisture * multiplier, 0.0F, 1.0F);
		} else {
			float alpha = (continentEdge - limit) / range;
			float multiplier = 1.0F - alpha * range;
			cell.moisture *= multiplier;
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
