/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.level.levelgen.climate;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.common.level.levelgen.cell.CellSampler;
import raccoonman.reterraforged.common.level.levelgen.cell.CellShape;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.settings.ClimateSettings;
import raccoonman.reterraforged.common.level.levelgen.settings.ClimateSettings.BiomeShape;
import raccoonman.reterraforged.common.level.levelgen.settings.ClimateSettings.RangeValue;
import raccoonman.reterraforged.common.level.levelgen.util.Seed;
import raccoonman.reterraforged.common.noise.Module;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.domain.Domain;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.MathUtil;

public class ClimateNoise {
    private static final float MOISTURE_SIZE = 2.5F;

    private final int seed;
    private final float frequency;
    
    private final Domain warp;
    private final Module moisture;
    private final Module temperature;
    private final NoiseLevels levels;
    private final CellSampler base;
    private final CellSampler climate;
    
    public ClimateNoise(Seed seed, ClimateSettings settings, NoiseLevels levels) {
    	this.seed = seed.get();
    	
    	BiomeShape biomeShape = settings.biomeShape();

    	this.levels = levels;
    	int biomeSize = biomeShape.biomeSize();
        this.frequency = 1.0F / biomeSize;
        float biomeFreq = 1.0F / biomeSize;

        RangeValue moistureSettings = settings.moisture();
        float moistScaler = moistureSettings.scale() * MOISTURE_SIZE;
        float moistureSize = moistScaler * biomeSize;
        int moistScale = NoiseUtil.round(moistureSize * biomeFreq);

        Seed moistureSeed = seed.offset(moistureSettings.seedOffset());
        Noise moisture = new Moisture(moistureSeed.next(), moistScale, moistureSettings.falloff());
        this.moisture = new Module(
        	Holder.direct(
        		moistureSettings.apply(moisture)
		        	.warp(Math.max(1, moistScale / 2), 1, moistScale / 4.0D).shift(moistureSeed.next())
		        	.warp(Math.max(1, moistScale / 6), 2, moistScale / 12.0D).shift(moistureSeed.next())
        	),
        	moistureSeed.next()
        );

        RangeValue tempSettings = settings.temperature();
        float tempScaler = tempSettings.scale();
        float temperatureSize = tempScaler * biomeSize;
        int tempScale = NoiseUtil.round(temperatureSize * biomeFreq);
        
        Seed tempSeed = seed.offset(tempSettings.seedOffset());
        Noise temperature = new Temperature(1F / tempScale, tempSettings.falloff());
        this.temperature = new Module(
        	Holder.direct(
	        	tempSettings.apply(temperature)
	        	.warp(tempScale * 4, 2, tempScale * 4).shift(tempSeed.next())
	        	.warp(tempScale, 1, tempScale).shift(tempSeed.next())
	        ),
        	tempSeed.next()
        );

        int warpScale = biomeShape.biomeWarpScale();
        this.warp = Domain.warp(
        	Source.build(warpScale, 3).lacunarity(2.4).gain(0.3).simplex().shift(tempSeed.next()),
        	Source.build(warpScale, 3).lacunarity(2.4).gain(0.3).simplex().shift(tempSeed.next()),
        	Source.constant(biomeShape.biomeWarpStrength() * 0.75)
        );
        
        this.base = new CellSampler(CellShape.SQUARE, 1.0F, 0.8F);
        this.climate = new CellSampler(CellShape.HEXAGON, 1.0F / settings.climateListScale(), 1.0F);
    }

    public void sample(float x, float y, ClimateSample sample) {
    	x = this.levels.toCoord(x);
    	y = this.levels.toCoord(y);
    	
        float px = warp.getX(x, y, this.seed);
        float py = warp.getY(x, y, this.seed);

        px *= frequency;
        py *= frequency;

        sampleClimate(px, py, sample);
    }

    // FIXME this doesn't seem to respect the level seed
    private void sampleClimate(float x, float y, ClimateSample sample) {
    	this.base.sample(x, y, this.seed, sample.cell);
    	sample.biomeNoise = MathUtil.rand(sample.cell.nearestHash, 1236785);
        sample.biomeEdgeNoise = 1f - NoiseUtil.sqrt(sample.cell.distance / sample.cell.distance2);
        sample.moisture = this.moisture.getValue(sample.cell.nearestX, sample.cell.nearestY);
        sample.temperature = this.temperature.getValue(sample.cell.nearestX, sample.cell.nearestY);
        this.climate.sample(x, y, this.seed, sample.cell);
        sample.climate = MathUtil.rand(sample.cell.nearestHash, 4389734);
    }
}
