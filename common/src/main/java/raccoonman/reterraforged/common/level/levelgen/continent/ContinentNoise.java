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

package raccoonman.reterraforged.common.level.levelgen.continent;

import raccoonman.reterraforged.common.level.levelgen.continent.config.ContinentConfig;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.TerrainSample;
import raccoonman.reterraforged.common.level.levelgen.settings.ControlPoints;
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.domain.Domain;

public class ContinentNoise {
    private final ContinentCellNoise noise;
    
    private final Domain warp;
    private final float frequency;

    public ContinentNoise(NoiseLevels levels, WorldSettings settings) {
        this.noise = createCellNoise(settings, levels);

        this.frequency = 1F / settings.continentScale();

        double strength = 0.2;
        var builder = Source.builder()
        	.octaves(3)
            .lacunarity(2.2)
            .frequency(3)
            .gain(0.3);

        //TODO don't hardcode this
        this.warp = Domain.warp(
        	builder.perlin().unique(),
        	builder.perlin().unique(),
        	Source.constant(strength)
        );
    }

    public void sampleContinent(float x, float y, TerrainSample sample, int seed) {
        x *= frequency;
        y *= frequency;

        float px = warp.getX(x, y, seed);
        float py = warp.getY(x, y, seed);

        var offset = noise.getWorldOffset(seed);
        px += offset.x;
        py += offset.y;

        noise.shapeNoise.sample(px, py, sample, seed);
    }

    public void sampleRiver(float x, float y, TerrainSample sample, int seed) {
        x *= frequency;
        y *= frequency;

        float px = warp.getX(x, y, seed);
        float py = warp.getY(x, y, seed);

        var offset = noise.getWorldOffset(seed);
        px += offset.x;
        py += offset.y;

        noise.riverNoise.sample(px, py, sample, seed);
    }
    
    protected static ContinentCellNoise createCellNoise(WorldSettings settings, NoiseLevels levels) {
        var config = new ContinentConfig();
        config.shape.scale = settings.continentScale();
        return new ContinentCellNoise(config, levels, new ControlPoints(settings.controlPoints()));
    }
}
