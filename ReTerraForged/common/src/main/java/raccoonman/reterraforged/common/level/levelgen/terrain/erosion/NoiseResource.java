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

package raccoonman.reterraforged.common.level.levelgen.terrain.erosion;

import java.util.concurrent.CompletableFuture;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseData;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainSample;
import raccoonman.reterraforged.common.util.FastRandom;
import raccoonman.reterraforged.common.util.storage.ObjectMap;

public class NoiseResource {
    public final FastRandom random = new FastRandom();

    public final NoiseData chunk = new NoiseData();
    public final ErosionFilter.Resource erosionResource = new ErosionFilter.Resource();

    public final float[] heightmap;
    public final TerrainSample sharedSample;
    public final ObjectMap<TerrainSample> chunkSample;

    public final CompletableFuture<float[]>[] chunkCache;

    public NoiseResource() {
        this(NoiseTileSize.DEFAULT);
    }

    @SuppressWarnings("unchecked")
	public NoiseResource(NoiseTileSize tileSize) {
        this.heightmap = new float[tileSize.regionSize];
        this.sharedSample = new TerrainSample();
        this.chunkSample = new ObjectMap<>(1, TerrainSample[]::new);
        this.chunkSample.fill(TerrainSample::new);
        this.chunkCache = new CompletableFuture[tileSize.chunkSize];
    }

    public TerrainSample getSample(int dx, int dz) {
        return NoiseData.isInsideChunk(dx, dz) ? chunkSample.get(dx, dz) : sharedSample;
    }
}
