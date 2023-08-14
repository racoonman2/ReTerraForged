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

public class NoiseTileSize {
    public static final int CHUNK_SIZE = 16;
    public static final NoiseTileSize DEFAULT = new NoiseTileSize(1);

    public final int chunkLength;
    public final int chunkSize;
    public final int chunkMin;
    public final int chunkMax;

    public final int regionLength;
    public final int regionSize;
    public final int min;
    public final int max;

    public NoiseTileSize(int chunkRadius) {
        chunkLength = chunkRadius * 2 + 1;
        chunkSize = chunkLength * chunkLength;
        chunkMin = -chunkRadius;
        chunkMax = chunkMin + chunkLength;

        regionLength = chunkLength * CHUNK_SIZE;
        regionSize = regionLength * regionLength;
        min = -CHUNK_SIZE * chunkRadius;
        max = min + regionLength;
    }

    public int chunkIndex(int x, int z) {
        return z * chunkLength + x;
    }

    public int relativeChunkIndex(int x, int z) {
        x -= chunkMin;
        z -= chunkMin;
        return z * chunkLength + x;
    }

    public int index(int x, int z) {
        return z * regionLength + x;
    }

    public int relativeIndex(int x, int z) {
        x -= min;
        z -= min;
        return index(x, z);
    }

    @Override
    public String toString() {
        return "NoiseTileSize{" +
                "chunkDiameter=" + chunkLength +
                ", chunkSize=" + chunkSize +
                ", chunkMin=" + chunkMin +
                ", chunkMax=" + chunkMax +
                ", regionDiameter=" + regionLength +
                ", regionSize=" + regionSize +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
