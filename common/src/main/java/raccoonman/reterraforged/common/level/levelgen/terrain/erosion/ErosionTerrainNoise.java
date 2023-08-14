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
import java.util.function.Consumer;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseData;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainBlender;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainSample;
import raccoonman.reterraforged.common.level.levelgen.util.Seed;
import raccoonman.reterraforged.common.util.pos.PosUtil;
import raccoonman.reterraforged.common.util.storage.LongCache;
import raccoonman.reterraforged.common.util.storage.LossyCache;
import raccoonman.reterraforged.common.util.storage.ObjectPool;

// i don't like this being a seperate class
// this should be made into a filter or something
// same with snow and water smoothing
@Deprecated(forRemoval = true)
public class ErosionTerrainNoise extends TerrainNoise {
    private static final int CACHE_SIZE = 256;

    protected final NoiseTileSize tileSize;
    protected final ErosionFilter erosion;
    protected final ThreadLocal<NoiseResource> localResource;

    protected final ObjectPool<float[]> pool;
    protected final LongCache<CompletableFuture<float[]>> cache;

    public ErosionTerrainNoise(Seed seed, WorldSettings worldSettings, NoiseLevels levels, TerrainBlender terrain, NoiseTileSize tileSize) {
    	super(seed, worldSettings, levels, terrain);
        this.tileSize = tileSize;
        this.erosion = new ErosionFilter(tileSize.regionLength, worldSettings.erosion());
        this.localResource = ThreadLocal.withInitial(() -> new NoiseResource(tileSize));
        this.pool = ObjectPool.forCacheSize(CACHE_SIZE, () -> new float[16 * 16]);
        this.cache = LossyCache.concurrent(CACHE_SIZE, CompletableFuture[]::new, this::restore);
    }

    @Override
    public void generate(int chunkX, int chunkZ, Consumer<NoiseData> consumer) {
        try {
            var resource = localResource.get();

            collectNeighbours(chunkX, chunkZ, resource);
            generateCenterChunk(chunkX, chunkZ, resource);
            awaitNeighbours(resource);

            generateErosion(chunkX, chunkZ, resource);
            generateRivers(chunkX, chunkZ, resource);
            consumer.accept(resource.chunk);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void collectNeighbours(int chunkX, int chunkZ, NoiseResource resource) {
        for (int dz = tileSize.chunkMin; dz < tileSize.chunkMax; dz++) {
            for (int dx = tileSize.chunkMin; dx < tileSize.chunkMax; dx++) {
                if (dx == 0 && dz == 0) continue;

                int tileIndex = tileSize.relativeChunkIndex(dx, dz);

                int cx = chunkX + dx;
                int cz = chunkZ + dz;
                resource.chunkCache[tileIndex] = getChunk(cx, cz);
            }
        }
    }

    protected void generateCenterChunk(int chunkX, int chunkZ, NoiseResource resource) {
        var blender = super.getBlenderResource();

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        int min = resource.chunk.min();
        int max = resource.chunk.max();

        for (int dz = min; dz < max; dz++) {
            float nz = getNoiseCoord(startZ + dz);

            for (int dx = min; dx < max; dx++) {
                float nx = getNoiseCoord(startX + dx);

                var sample = resource.chunkSample.get(dx, dz);
                super.sampleTerrain(nx, nz, sample, blender);

                int tileIndex = tileSize.relativeIndex(dx, dz);
                resource.heightmap[tileIndex] = sample.heightNoise;
            }
        }
    }

    protected void awaitNeighbours(NoiseResource resource) {
        for (int cz = tileSize.chunkMin; cz < tileSize.chunkMax; cz++) {
            for (int cx = tileSize.chunkMin; cx < tileSize.chunkMax; cx++) {
                if (cx == 0 && cz == 0) continue;

                int chunkIndex = tileSize.relativeChunkIndex(cx, cz);
                float[] chunk = resource.chunkCache[chunkIndex].join();

                int relStartX = cx << 4;
                int relStartZ = cz << 4;
                for (int i = 0; i < chunk.length; i++) {
                    int dx = i & 15;
                    int dz = i >> 4;
                    int index = tileSize.relativeIndex(relStartX + dx, relStartZ + dz);
                    resource.heightmap[index] = chunk[i];
                }
            }
        }
    }

    protected void generateErosion(int chunkX, int chunkZ, NoiseResource resource) {
        erosion.apply(this.seed, chunkX, chunkZ, tileSize, resource.erosionResource, resource.random, resource.heightmap);
    }

    protected void generateRivers(int chunkX, int chunkZ, NoiseResource resource) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        int min = resource.chunk.min();
        int max = resource.chunk.max();
        for (int dz = min; dz < max; dz++) {
            float nz = getNoiseCoord(startZ + dz);

            for (int dx = min; dx < max; dx++) {
                float nx = getNoiseCoord(startX + dx);

                int tileIndex = tileSize.relativeIndex(dx, dz);
                float height = resource.heightmap[tileIndex];

                int chunkIndex = resource.chunk.index().of(dx, dz);
                var sample = resource.chunkSample.get(chunkIndex);
                sample.heightNoise = height;

                this.continent.sampleRiver(nx, nz, sample, this.seed);

                resource.chunk.setNoise(chunkIndex, sample);
            }
        }
    }

    protected void restore(CompletableFuture<float[]> task) {
        task.thenAccept(pool::restore);
    }

    protected CompletableFuture<float[]> getChunk(int x, int z) {
        return cache.computeIfAbsent(PosUtil.pack(x, z), this::generateChunk);
    }

    protected CompletableFuture<float[]> generateChunk(final long key) {
        return CompletableFuture.supplyAsync(() -> {
            int chunkX = PosUtil.unpackLeft(key);
            int chunkZ = PosUtil.unpackRight(key);

            int startX = chunkX << 4;
            int startZ = chunkZ << 4;

            float[] height = pool.take();
            var sample = new TerrainSample();
            var blender = super.getBlenderResource();

            for (int i = 0; i < height.length; i++) {
                int dx = i & 15;
                int dz = i >> 4;

                float nx = getNoiseCoord(startX + dx);
                float nz = getNoiseCoord(startZ + dz);
                height[i] = super.sampleTerrain(nx, nz, sample, blender).heightNoise;
            }

            return height;
        });
    }
}
