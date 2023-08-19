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

package raccoonman.reterraforged.common.level.levelgen.biome.surface;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.TerrainCache;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.registries.data.tags.RTFBlockTags;

public final class Surface {

	// TODO don't require TerrainCache here
	public static void apply(ChunkAccess chunk, ChunkGenerator generator, TerrainCache terrain) {
        var norm = 55 * (generator.getGenDepth() / 255F);
        var pos = new BlockPos.MutableBlockPos();
        for (int dz = 0; dz < 16; dz++) {
            for (int dx = 0; dx < 16; dx++) {
                int y = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, dx, dz);

                float gradient = terrain.getNormalGradient(dx, dz, norm);
                if (y < generator.getSeaLevel() || gradient < 0.6F) continue;

                var solid = findSolid(pos.set(dx, y, dz), chunk);
                if (solid == null) continue;

                int bottom = pos.getY();
                while (y > bottom) {
                    chunk.setBlockState(pos.setY(y), solid, false);
                    y--;
                }
            }
        }
    }

    public static void applyPost(ChunkAccess chunk, TerrainCache terrain, ChunkGenerator generator) {
        float norm = 70 * (generator.getGenDepth() / 255F);
        var pos = new BlockPos.MutableBlockPos();

        for (int dz = 0; dz < 16; dz++) {
            for (int dx = 0; dx < 16; dx++) {
                int y = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, dx, dz) + 1;
                pos.set(dx, y, dz);

                var state = chunk.getBlockState(pos);
                float gradient = terrain.getNormalGradient(dx, dz, norm);

                if (gradient < 0.625F) {
                    if (state.getBlock() instanceof SnowLayerBlock) {
                        smoothSnow(pos, state, chunk, terrain);
                    }
                } else {
                    if (state.isAir()) {
                        state = chunk.getBlockState(pos.setY(y - 1));
                    }

                    if (state.is(BlockTags.SNOW)) {
                        erodeSnow(pos, chunk);
                    }
                }
            }
        }
    }

    public static void smoothWater(ChunkAccess chunk, WorldGenLevel region, TerrainCache terrain) {
        var pos = new BlockPos.MutableBlockPos();

        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();

        var waterState = Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 2);

        for (int dz = 0; dz < 16; dz++) {
            for (int dx = 0; dx < 16; dx++) {
                if (!isSmoothable(dx, dz, terrain)) continue;

                int x = minX + dx;
                int z = minZ + dz;
                int y = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, dx, dz);

                var state = chunk.getBlockState(pos.set(x, y, z));
                if (state.is(Blocks.WATER) && state.getValue(LiquidBlock.LEVEL) == 0) {
                    if (shouldSmooth(x, y, z, chunk, region, pos)) {
                        chunk.setBlockState(pos.set(x, y, z), waterState, false);
                    }
                }
            }
        }
    }

    protected static boolean shouldSmooth(int x, int y, int z, ChunkAccess chunk, WorldGenLevel region, BlockPos.MutableBlockPos pos) {
        int radius = 6;
        int radius2 = radius * radius;

        for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int d2 = dx * dx + dz * dz;
                if (d2 == 0 || d2 > radius2) continue;

                pos.set(x + dx, y, z + dz);

                var world = sameChunk(pos, chunk.getPos()) ? chunk : region;
                var state = world.getBlockState(pos);

                if (state.isAir()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static boolean isSmoothable(int x, int z, TerrainCache terrainData) {
        return terrainData.getWater(x, z) == 0.0F;
    }

    protected static void smoothSnow(BlockPos.MutableBlockPos pos, BlockState state, ChunkAccess chunk, TerrainCache terrain) {
        float height = terrain.height().get(pos.getX(), pos.getZ());
        float delta = height - NoiseUtil.floor(height);

        int layers = 1 + NoiseUtil.floor(delta * 7.9999F);
        state = state.setValue(SnowLayerBlock.LAYERS, layers);
        chunk.setBlockState(pos, state, false);
    }

    protected static void erodeSnow(BlockPos.MutableBlockPos pos, ChunkAccess chunk) {
        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);

        int y0 = pos.getY() - 1;
        int y1 = Math.max(pos.getY() - 15, 0);

        for (int y = y0; y > y1; y--) {
            pos.setY(y);

            var state = chunk.getBlockState(pos);
            if (isErodible(state)) {
                chunk.setBlockState(pos, Blocks.STONE.defaultBlockState(), false);
            } else {
                return;
            }
        }
    }

    public static boolean isErodible(BlockState state) {
        return state.is(RTFBlockTags.ERODIBLE) ||state.is(BlockTags.SNOW);
    }

    protected static boolean sameChunk(BlockPos pos, ChunkPos chunk) {
        return pos.getX() >> 4 == chunk.x && pos.getZ() >> 4 == chunk.z;
    }

    protected static BlockState findSolid(BlockPos.MutableBlockPos pos, ChunkAccess chunk) {
        var state = chunk.getBlockState(pos);

        if (!isErodible(state)) return null;

        for (int y = pos.getY() - 1, bottom = Math.max(0, pos.getY() - 20); y > bottom; y--) {
            state = chunk.getBlockState(pos.setY(y));

            // Stop when we hit non-erodable material
            if (!isErodible(state)) {
                return state;
            }
        }

        return null;
    }
}
