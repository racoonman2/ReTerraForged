package raccoonman.reterraforged.common.level.levelgen.test.climate;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.BiomeType;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.ContinentLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.MoistureLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.TemperatureLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.Continent;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Levels;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;

public class Climate {
    private Noise rand;
	private float lowerHeight;
	private Noise offsetX;
	private Noise offsetY;
	private int offsetDistance;
	private Levels levels;
	private ClimateModule biomeNoise;
    private Noise beachNoise;

	public Climate(Continent continent, GeneratorContext context) {
		this.biomeNoise = new ClimateModule(continent, context);
		this.levels = context.levels;
		this.offsetDistance = context.settings.climate().biomeEdgeShape.strength;
		this.rand = Source.builder().shift(context.seed.next()).rand();
		this.offsetX = context.settings.climate().biomeEdgeShape.build(context.seed.next());
		this.offsetY = context.settings.climate().biomeEdgeShape.build(context.seed.next());
		this.lowerHeight = context.levels.ground;
		this.beachNoise = Source.build(context.seed.next(), 20, 1).perlin2().scale(context.levels.scale(5));
	}

	public Noise getRand() {
		return this.rand;
	}

	public float getOffsetX(float x, float z, int seed, float distance) {
		return this.offsetX.compute(x, z, seed) * distance;
	}

	public float getOffsetZ(float x, float z, int seed, float distance) {
		return this.offsetY.compute(x, z, seed) * distance;
	}

	public void apply(Cell cell, float x, float z, int seed) {
		this.biomeNoise.apply(cell, x, z, seed, true);
		float edgeBlend = 0.4F;
		if (cell.value <= this.levels.water) {
			if (cell.terrain == TerrainType.COAST) {
				cell.terrain = TerrainType.SHALLOW_OCEAN;
			}
		} else if (cell.biomeRegionEdge < edgeBlend || cell.terrain == TerrainType.MOUNTAIN_CHAIN) {
			float modifier = 1.0F - NoiseUtil.map(cell.biomeRegionEdge, 0.0F, edgeBlend, edgeBlend);
			float distance = this.offsetDistance * modifier;
			float dx = this.getOffsetX(x, z, seed, distance);
			float dz = this.getOffsetZ(x, z, seed, distance);
			x += dx;
			z += dz;
			this.biomeNoise.apply(cell, x, z, seed, false);
		}
		this.modifyTemp(cell, x, z);

        cell.continentLevel = this.getContinentLevel(cell, x, z).mid();
        cell.temperatureLevel = cell.biome.getTemperatureLevel(cell.biomeRegionId);
        cell.moistureLevel = cell.biome.getMoistureLevel(cell.biomeRegionId);
	}

	private void modifyTemp(Cell cell, float x, float z) {
		float height = cell.value;
		if (height > 0.75F) {
			cell.temperature = Math.max(0.0F, cell.temperature - 0.05F);
			return;
		}
		if (height > 0.45F) {
			float delta = (height - 0.45F) / 0.3F;
			cell.temperature = Math.max(0.0F, cell.temperature - delta * 0.05F);
			return;
		}
		height = Math.max(this.lowerHeight, height);
		if (height >= this.lowerHeight) {
			float delta = 1.0F - (height - this.lowerHeight) / (0.45F - this.lowerHeight);
			cell.temperature = Math.min(1.0F, cell.temperature + delta * 0.05F);
		}
	}
	
	private ContinentLevel getContinentLevel(Cell cell, float x, float z) {
        float waterLevel = this.levels.water;
        float beachLevel = this.levels.water(5);
       
        if(cell.terrain.isDeepOcean()) {
        	return ContinentLevel.DEEP_OCEAN;
        }
        
        if(cell.terrain.isShallowOcean()) {
        	return ContinentLevel.OCEAN;
        }
        
        if(cell.terrain == TerrainType.COAST && cell.value > waterLevel && cell.value <= beachLevel) {
        	if(cell.biome != BiomeType.DESERT) {
	        	if (cell.value + this.beachNoise.compute(x, z, 0) < this.levels.water(5)) {
		        	return ContinentLevel.COAST;
	            }
        	}
        }
        
        if(cell.continentEdge > 0.825F) {
        	return ContinentLevel.FAR_INLAND;
        }
        
        if(cell.continentEdge > 0.65F) {
        	return ContinentLevel.MID_INLAND;
        }
        
        return ContinentLevel.NEAR_INLAND;
	}
}
