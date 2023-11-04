package raccoonman.reterraforged.common.level.levelgen.test.world.continent.advanced;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Line;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.SimpleContinent;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public class AdvancedContinentGenerator extends AbstractContinent implements SimpleContinent {
	protected static float CENTER_CORRECTION = 0.35f;
	protected float frequency;
	protected float variance;
	protected int varianceSeed;
	protected Domain warp;
	protected Noise cliffNoise;
	protected Noise bayNoise;

	public AdvancedContinentGenerator(Seed seed, GeneratorContext context) {
		super(seed, context);
		WorldSettings settings = context.settings.world();
		int tectonicScale = settings.continent.continentScale * 4;
		this.frequency = 1.0F / tectonicScale;
		this.varianceSeed = seed.next();
		this.variance = settings.continent.continentSizeVariance;
		this.warp = this.createWarp(seed, tectonicScale, settings.continent);
		this.cliffNoise = Source.build(seed.next(), this.continentScale / 2, 2).build(Source.SIMPLEX2).clamp(0.1, 0.25).map(0.0, 1.0).freq(1.0F / this.frequency, 1.0F / this.frequency);
		this.bayNoise = Source.simplex(seed.next(), 100, 1).scale(0.1).bias(0.9).freq(1.0F / this.frequency, 1.0F / this.frequency);
	}

	@Override
	public void apply(Cell cell, float x, float y, int seed) {
		float wx = this.warp.getX(x, y, seed);
		float wy = this.warp.getY(x, y, seed);
		x = wx * this.frequency;
		y = wy * this.frequency;
		int xi = NoiseUtil.floor(x);
		int yi = NoiseUtil.floor(y);
		int cellX = xi;
		int cellY = yi;
		float cellPointX = x;
		float cellPointY = y;
		float nearest = Float.MAX_VALUE;
		for (int cy = yi - 1; cy <= yi + 1; ++cy) {
			for (int cx = xi - 1; cx <= xi + 1; ++cx) {
				Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
				float px = cx + vec.x() * this.jitter;
				float py = cy + vec.y() * this.jitter;
				float dist2 = Line.dist2(x, y, px, py);
				if (dist2 < nearest) {
					cellPointX = px;
					cellPointY = py;
					cellX = cx;
					cellY = cy;
					nearest = dist2;
				}
			}
		}
		nearest = Float.MAX_VALUE;
		float sumX = 0.0F;
		float sumY = 0.0F;
		for (int cy2 = cellY - 1; cy2 <= cellY + 1; ++cy2) {
			for (int cx2 = cellX - 1; cx2 <= cellX + 1; ++cx2) {
				if (cx2 != cellX || cy2 != cellY) {
					Vec2f vec2 = NoiseUtil.cell(this.seed, cx2, cy2);
					float px2 = cx2 + vec2.x() * this.jitter;
					float py2 = cy2 + vec2.y() * this.jitter;
					float dist3 = getDistance(x, y, cellPointX, cellPointY, px2, py2);
					sumX += px2;
					sumY += py2;
					if (dist3 < nearest) {
						nearest = dist3;
					}
				}
			}
		}
		if (this.shouldSkip(cellX, cellY)) {
			return;
		}
		cell.continentId = AbstractContinent.getCellValue(this.seed, cellX, cellY);
		cell.continentEdge = this.getDistanceValue(x, y, cellX, cellY, nearest, seed);
		cell.continentX = this.getCorrectedContinentCentre(cellPointX, sumX / 8.0F);
		cell.continentZ = this.getCorrectedContinentCentre(cellPointY, sumY / 8.0F);
	}

	@Override
	public float getEdgeValue(float x, float z, int seed) {
		try (Resource<Cell> resource = Cell.getResource()) {
			Cell cell = resource.get();
			this.apply(cell, x, z, seed);
			return cell.continentEdge;
		}
	}

	@Override
	public long getNearestCenter(float x, float z, int seed) {
		try (Resource<Cell> resource = Cell.getResource()) {
			Cell cell = resource.get();
			this.apply(cell, x, z, seed);
			return NoiseUtil.pack(cell.continentX, cell.continentZ);
		}
	}

	@Override
	public Rivermap getRivermap(int x, int z, int seed) {
		return this.riverCache.getRivers(x, z, seed);
	}

	protected Domain createWarp(Seed seed, int tectonicScale, WorldSettings.Continent continent) {
		int warpScale = NoiseUtil.round(tectonicScale * 0.225F);
		double strength = NoiseUtil.round(tectonicScale * 0.33F);
		return Domain.warp(
				Source.build(seed.next(), warpScale, continent.continentNoiseOctaves).gain(continent.continentNoiseGain)
						.lacunarity(continent.continentNoiseLacunarity).build(Source.PERLIN2),
				Source.build(seed.next(), warpScale, continent.continentNoiseOctaves).gain(continent.continentNoiseGain)
						.lacunarity(continent.continentNoiseLacunarity).build(Source.PERLIN2),
				Source.constant(strength));
	}

	protected float getDistanceValue(float x, float y, int cellX, int cellY, float distance, int seed) {
		distance = this.getVariedDistanceValue(cellX, cellY, distance);
		distance = NoiseUtil.sqrt(distance);
		distance = NoiseUtil.map(distance, 0.05F, 0.25F, 0.2F);
		distance = this.getCoastalDistanceValue(x, y, distance, seed);
		if (distance < this.controlPoints.inland() && distance >= this.controlPoints.shallowOcean()) {
			distance = this.getCoastalDistanceValue(x, y, distance, seed);
		}
		return distance;
	}

	protected float getVariedDistanceValue(int cellX, int cellY, float distance) {
		if (this.variance > 0.0F && !this.isDefaultContinent(cellX, cellY)) {
			float sizeValue = AbstractContinent.getCellValue(this.varianceSeed, cellX, cellY);
			float sizeModifier = NoiseUtil.map(sizeValue, 0.0F, this.variance, this.variance);
			distance *= sizeModifier;
		}
		return distance;
	}

	protected float getCoastalDistanceValue(float x, float y, float distance, int seed) {
		if (distance > this.controlPoints.shallowOcean() && distance < this.controlPoints.inland()) {
			float alpha = distance / this.controlPoints.inland();
			float cliff = this.cliffNoise.compute(x, y, seed);
			distance = NoiseUtil.lerp(distance * cliff, distance, alpha);
			if (distance < this.controlPoints.shallowOcean()) {
				distance = this.controlPoints.shallowOcean() * this.bayNoise.compute(x, y, seed);
			}
		}
		return distance;
	}

	protected int getCorrectedContinentCentre(float point, float average) {
		point = NoiseUtil.lerp(point, average, 0.35F) / this.frequency;
		return (int) point;
	}

	protected static float midPoint(float a, float b) {
		return (a + b) * 0.5F;
	}

	protected static float getDistance(float x, float y, float ax, float ay, float bx, float by) {
		float mx = midPoint(ax, bx);
		float my = midPoint(ay, by);
		float dx = bx - ax;
		float dy = by - ay;
		float nx = -dy;
		float ny = dx;
		return getDistance2Line(x, y, mx, my, mx + nx, my + ny);
	}

	protected static float getDistance2Line(float x, float y, float ax, float ay, float bx, float by) {
		float dx = bx - ax;
		float dy = by - ay;
		float v = (x - ax) * dx + (y - ay) * dy;
		v /= dx * dx + dy * dy;
		float ox = ax + dx * v;
		float oy = ay + dy * v;
		return Line.dist2(x, y, ox, oy);
	}
}
