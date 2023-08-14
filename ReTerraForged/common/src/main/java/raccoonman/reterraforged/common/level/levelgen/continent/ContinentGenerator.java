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
import raccoonman.reterraforged.common.level.levelgen.settings.ControlPoints;
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainSample;
import raccoonman.reterraforged.common.level.levelgen.util.Seed;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.domain.Domain;

// what a mess
public class ContinentGenerator {
    protected final ContinentNoise noise;
    
    protected final Domain warp;
    protected final float frequency;

    public ContinentGenerator(Seed seed, NoiseLevels levels, WorldSettings settings) {
        this.noise = createContinent(seed, settings, levels);

        this.frequency = 1F / settings.continentScale();

        double strength = 0.2;

        var builder = Source.builder()
        	.octaves(3)
            .lacunarity(2.2)
            .frequency(3)
            .gain(0.3);

        this.warp = Domain.warp(
        	builder.perlin().shift(seed.next()),
        	builder.perlin().shift(seed.next()),
        	Source.constant(strength)
        );
    }

    public void sampleContinent(float x, float y, TerrainSample sample, int seed) {
        x *= frequency;
        y *= frequency;

        float px = warp.getX(x, y, seed);
        float py = warp.getY(x, y, seed);

        var offset = noise.getWorldOffset();
        px += offset.x;
        py += offset.y;

        noise.shapeNoise.sample(px, py, sample);
    }

    public void sampleRiver(float x, float y, TerrainSample sample, int seed) {
        x *= frequency;
        y *= frequency;

        float px = warp.getX(x, y, seed);
        float py = warp.getY(x, y, seed);

        var offset = noise.getWorldOffset();
        px += offset.x;
        py += offset.y;

        noise.riverNoise.sample(px, py, sample, seed);
    }
 
    protected static ContinentNoise createContinent(Seed seed, WorldSettings settings, NoiseLevels levels) {
        var config = new ContinentConfig();
        config.shape.scale = settings.continentScale();
        config.shape.seed0 = seed.next();
        config.shape.seed1 = seed.next();
        return new ContinentNoise(seed.get(), config, levels, new ControlPoints(settings.controlPoints()));
    }
}
