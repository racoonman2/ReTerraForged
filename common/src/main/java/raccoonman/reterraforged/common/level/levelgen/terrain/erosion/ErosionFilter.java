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

import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.FastRandom;
import raccoonman.reterraforged.common.util.MathUtil;

public class ErosionFilter {
    private static final float HEIGHT_FALL_OFF = 0.4F;
    private static final int HEIGHT = 0;
    private static final int GRAD_X = 1;
    private static final int GRAD_Y = 2;

    private static final int erosionRadius = 7;
    private static final float inertia = 0.005f; // At zero, water will instantly change direction to flow downhill. At 1, water will never change direction.
    private static final float sedimentCapacityFactor = 7F; // Multiplier for how much sediment a droplet can carry
    private static final float minSedimentCapacity = 0.008f; // Used to prevent carry capacity getting too close to zero on flatter terrain
    private static final float evaporateSpeed = 0.35f;
    private static final float gravity = 2.5F;

    private final float erodeSpeed;
    private final float depositSpeed;
    private final float initialSpeed;
    private final float initialWaterVolume;
    private final int maxDropletLifetime;
    private final int[][] erosionBrushIndices;
    private final float[][] erosionBrushWeights;

    private final int iterations;

    public ErosionFilter(int mapSize, WorldSettings.Erosion settings) {
        this.iterations = settings.dropletsPerChunk();
        this.erodeSpeed = settings.erosionRate();
        this.depositSpeed = settings.depositeRate();
        this.initialSpeed = settings.dropletVelocity();
        this.initialWaterVolume = settings.dropletVolume();
        this.maxDropletLifetime = settings.dropletLifetime();
        this.erosionBrushIndices = new int[mapSize * mapSize][];
        this.erosionBrushWeights = new float[mapSize * mapSize][];
        this.initBrushes(mapSize, erosionRadius);
    }

    public void apply(int seed, int chunkX, int chunkZ, NoiseTileSize size, Resource resource, FastRandom random, float[] map) {
        int maxIndex = size.regionLength - 2;
        for (int i = 0; i < iterations; i++) {
            long iterationSeed = NoiseUtil.seed(seed, i);

            for (int dz = size.chunkMin; dz < size.chunkMax; dz++) {
                int startZ = (dz - size.chunkMin) << 4;

                for (int dx = size.chunkMin; dx < size.chunkMax; dx++) {
                    int startX = (dx - size.chunkMin) << 4;

                    long chunkSeed = NoiseUtil.seed(chunkX + dx, chunkZ + dz);
                    random.seed(chunkSeed, iterationSeed);

                    int x = startX + random.nextInt(NoiseTileSize.CHUNK_SIZE);
                    int z = startZ + random.nextInt(NoiseTileSize.CHUNK_SIZE);

                    x = MathUtil.clamp(x, 1, maxIndex);
                    z = MathUtil.clamp(z, 1, maxIndex);

                    applyDrop(x, z, map, size.regionLength, resource);
                }
            }
        }
    }

    private void applyDrop(float posX, float posY, float[] map, int mapSize, Resource resource) {
        float dirX = 0;
        float dirY = 0;
        float sediment = 0;
        float speed = initialSpeed;
        float water = initialWaterVolume;

        for (int lifetime = 0; lifetime < maxDropletLifetime; lifetime++) {
            int nodeX = (int) posX;
            int nodeY = (int) posY;
            int dropletIndex = nodeY * mapSize + nodeX;
            // Calculate droplet's offset inside the cell (0,0) = at NW node, (1,1) = at SE node
            float cellOffsetX = posX - nodeX;
            float cellOffsetY = posY - nodeY;

            // Calculate droplet's height and direction of flow with bilinear interpolation of surrounding heights
            var gradient = grad(map, mapSize, posX, posY, resource.grad1);

            // Update the droplet's direction and position (move position 1 unit regardless of speed)
            dirX = (dirX * inertia - gradient[GRAD_X] * (1 - inertia));
            dirY = (dirY * inertia - gradient[GRAD_Y] * (1 - inertia));

            // Normalize direction
            float len2 = dirX * dirX + dirY * dirY;
            if (len2 == 0) {
                return;
            }

            float len = NoiseUtil.sqrt(len2);
            dirX /= len;
            dirY /= len;

            posX += dirX;
            posY += dirY;

            // Stop simulating droplet if it's not moving or has flowed over edge of map
            if ((dirX == 0 && dirY == 0) || posX < 0 || posX >= mapSize - 1 || posY < 0 || posY >= mapSize - 1) {
                return;
            }

            // Find the droplet's new height and calculate the deltaHeight
            float falloff = getFalloff(map[dropletIndex]);
            float newHeight = grad(map, mapSize, posX, posY, resource.grad2)[HEIGHT];
            float deltaHeight = (newHeight - gradient[HEIGHT]) * falloff;

            // Calculate the droplet's sediment capacity (higher when moving fast down a slope and contains lots of water)
            float sedimentCapacity = Math.max(-deltaHeight * speed * water * sedimentCapacityFactor, minSedimentCapacity);

            // If carrying more sediment than capacity, or if flowing uphill:
            if (sediment > sedimentCapacity || deltaHeight > 0) {
                // If moving uphill (deltaHeight > 0) try fill up to the current height, otherwise deposit a fraction of the excess sediment
                float amountToDeposit = (deltaHeight > 0) ? Math.min(deltaHeight, sediment) : (sediment - sedimentCapacity) * depositSpeed;
                sediment -= amountToDeposit;

                // Add the sediment to the four nodes of the current cell using bilinear interpolation
                // Deposition is not distributed over a radius (like erosion) so that it can fill small pits
                map[dropletIndex] += amountToDeposit * (1 - cellOffsetX) * (1 - cellOffsetY);
                map[dropletIndex + 1] += amountToDeposit * cellOffsetX * (1 - cellOffsetY);
                map[dropletIndex + mapSize] += amountToDeposit * (1 - cellOffsetX) * cellOffsetY;
                map[dropletIndex + mapSize + 1] += amountToDeposit * cellOffsetX * cellOffsetY;
            } else {
                // Erode a fraction of the droplet's current carry capacity.
                // Clamp the erosion to the change in height so that it doesn't dig a hole in the terrain behind the droplet
                float amountToErode = Math.min((sedimentCapacity - sediment) * erodeSpeed, -deltaHeight);

                // Use erosion brush to erode from all nodes inside the droplet's erosion radius
                for (int brushPointIndex = 0; brushPointIndex < erosionBrushIndices[dropletIndex].length; brushPointIndex++) {
                    int nodeIndex = erosionBrushIndices[dropletIndex][brushPointIndex];
                    float weighedErodeAmount = amountToErode * erosionBrushWeights[dropletIndex][brushPointIndex];
                    float deltaSediment = Math.min(map[nodeIndex], weighedErodeAmount);
                    map[nodeIndex] -= deltaSediment;
                    sediment += deltaSediment;
                }
            }

            float speed2 = speed * speed + deltaHeight * gravity;
            if (speed2 <= 0) return;

            // Update droplet's speed and water content
            speed = NoiseUtil.sqrt(speed2);
            water *= (1 - evaporateSpeed);
        }
    }

    private void initBrushes(int size, int radius) {
        int[] xOffsets = new int[radius * radius * 4];
        int[] yOffsets = new int[radius * radius * 4];
        float[] weights = new float[radius * radius * 4];
        float weightSum = 0;
        int addIndex = 0;

        for (int i = 0; i < erosionBrushIndices.length; i++) {
            int centreX = i % size;
            int centreY = i / size;

            if (centreY <= radius || centreY >= size - radius || centreX <= radius + 1 || centreX >= size - radius) {
                weightSum = 0;
                addIndex = 0;
                for (int y = -radius; y <= radius; y++) {
                    for (int x = -radius; x <= radius; x++) {
                        float sqrDst = x * x + y * y;
                        if (sqrDst < radius * radius) {
                            int coordX = centreX + x;
                            int coordY = centreY + y;

                            if (coordX >= 0 && coordX < size && coordY >= 0 && coordY < size) {
                                float weight = 1 - (float) Math.sqrt(sqrDst) / radius;
                                weightSum += weight;
                                weights[addIndex] = weight;
                                xOffsets[addIndex] = x;
                                yOffsets[addIndex] = y;
                                addIndex++;
                            }
                        }
                    }
                }
            }

            int numEntries = addIndex;
            erosionBrushIndices[i] = new int[numEntries];
            erosionBrushWeights[i] = new float[numEntries];

            for (int j = 0; j < numEntries; j++) {
                erosionBrushIndices[i][j] = (yOffsets[j] + centreY) * size + xOffsets[j] + centreX;
                erosionBrushWeights[i][j] = weights[j] / weightSum;
            }
        }
    }

    private float[] grad(float[] nodes, int mapSize, float posX, float posY, float[] resource) {
        int coordX = (int) posX;
        int coordY = (int) posY;

        // Calculate droplet's offset inside the cell (0,0) = at NW node, (1,1) = at SE node
        float x = posX - coordX;
        float y = posY - coordY;

        // Calculate heights of the four nodes of the droplet's cell
        int nodeIndexNW = coordY * mapSize + coordX;
        float heightNW = nodes[nodeIndexNW];
        float heightNE = nodes[nodeIndexNW + 1];
        float heightSW = nodes[nodeIndexNW + mapSize];
        float heightSE = nodes[nodeIndexNW + mapSize + 1];

        resource[HEIGHT] = heightNW * (1 - x) * (1 - y) + heightNE * x * (1 - y) + heightSW * (1 - x) * y + heightSE * x * y;
        resource[GRAD_X] = (heightNE - heightNW) * (1 - y) + (heightSE - heightSW) * y;
        resource[GRAD_Y] = (heightSW - heightNW) * (1 - x) + (heightSE - heightNE) * x;

        return resource;
    }

    private static float getFalloff(float height) {
        if (height >= HEIGHT_FALL_OFF) return 1F;

        return height / HEIGHT_FALL_OFF;
    }

    public record Resource(float[] grad1, float[] grad2) {
    	
    	public Resource() {
    		this(new float[3], new float[3]);
    	}
    }
}
