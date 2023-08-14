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

package raccoonman.reterraforged.common.level.levelgen.terrain;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class TerrainGenerator {
	// THIS IS HUGELY IMPORTANT!!!!
	// if number of currently generating chunks > CACHE_SIZE than performance goes to shit because terraforged's cache can't grow
	// so it just reallocates the terrain data every time it needs it (very bad)
	//	private static final int CACHE_SIZE = 1024;
	private final TerrainLevels levels;
    private final Supplier<TerrainNoise> terrainNoise;
    private final Queue<TerrainData> terrainDataPool;

    public TerrainGenerator(TerrainLevels levels, Supplier<TerrainNoise> terrainNoise) {
    	this.levels = levels;
    	this.terrainNoise = terrainNoise;
        this.terrainDataPool = new ConcurrentLinkedQueue<>();
//        for(int i = 0; i < CACHE_SIZE; i++) {
//        	this.terrainDataPool.add(new TerrainData(levels));
//        }
    }

    public void restore(TerrainData terrainData) {
//    	System.out.println("restored: " + this.terrainDataPool.size() + ":" + terrainData);
        this.terrainDataPool.add(terrainData);
    }

    public TerrainData generate(int chunkX, int chunkZ) {
//        var terrainData = this.terrainDataPool.poll();
//        if(terrainData == null) {
//        	terrainData = new TerrainData(this.levels);
//        }
    	TerrainData terrainData = new TerrainData(this.levels);
        this.terrainNoise.get().generate(chunkX, chunkZ, terrainData);
        return terrainData;
    }
}
