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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

import java.util.Comparator;
import java.util.function.Predicate;

public class StructureTerrain {
    private static final int RADIUS = 20;
    private static final Comparator<StructurePiece> PIECE_SORTER = Comparator.comparing(o -> o.getBoundingBox().minY());

    private static final Predicate<Structure> TERRAIN_MODIFIED = f -> f.terrainAdaptation() == TerrainAdjustment.BEARD_BOX || f.terrainAdaptation() == TerrainAdjustment.BEARD_THIN;

    protected final ObjectList<StructurePiece> rigids = new ObjectArrayList<>(10);
    protected final ObjectListIterator<StructurePiece> pieceIterator;

    protected final BlockState air = Blocks.AIR.defaultBlockState();
    protected final BlockState solid = Blocks.STONE.defaultBlockState();
    protected final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public StructureTerrain(ChunkAccess chunk, StructureManager manager) {
        var chunkPos = chunk.getPos();

        manager.startsForStructure(chunkPos, TERRAIN_MODIFIED).forEach(start -> {
            for (var piece : start.getPieces()) {
                if (!piece.isCloseToChunk(chunkPos, RADIUS)) continue;

                if (piece instanceof PoolElementStructurePiece element) {
                    var projection = element.getElement().getProjection();
                    if (projection == StructureTemplatePool.Projection.RIGID) {
                        this.rigids.add(element);
                    }
                } else {
                    this.rigids.add(piece);
                }
            }
        });

        this.rigids.sort(PIECE_SORTER);
        this.pieceIterator = rigids.iterator();
    }

    public void modify(int x, int z, ChunkAccess chunk, TerrainData terrainData) {
        int y = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);

        float maxY = y;
        int maxPosY = y;
        StructurePiece highest = null;

        while (pieceIterator.hasNext()) {
            var piece = pieceIterator.next();
            var bounds = piece.getBoundingBox();
            int length = Math.max(bounds.getXSpan(), bounds.getZSpan());
            float radius = Math.max(4, RADIUS - length);

            int posY = getPieceY(piece);
            maxPosY = Math.max(maxPosY, posY);

            if (highest == null && posY > y) {
                maxY = raise(x, z, bounds, posY, maxY, radius);
            }

            if (x >= bounds.minX() && x <= bounds.maxX() && z >= bounds.minZ() && z <= bounds.maxZ()) {
                if (highest == null || bounds.minY() > highest.getBoundingBox().minY()) {
                    highest = piece;
                }
            }
        }

        boolean raised = raiseTerrain(x, y, z, maxY, chunk);
        boolean carved = carveTerrain(x, maxPosY, z, chunk, highest);

        if (raised || carved) {
            terrainData.getHeight().set(x, z, chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z));
        }

        reset();
    }

    protected boolean raiseTerrain(int x, int y, int z, float maxY, ChunkAccess chunk) {
        int max = (int) maxY;
        if (y + 1 >= max) return false;

        for (int py = y; py < max; py++) {
            chunk.setBlockState(pos.set(x, py, z), solid, false);
        }

        return true;
    }

    protected boolean carveTerrain(int x, int y, int z, ChunkAccess chunk, StructurePiece piece) {
        if (piece == null) return false;

        int surface = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        int ceiling = Math.max(y, surface);

        var bounds = piece.getBoundingBox();
        int minY = getPieceY(piece);
        int maxY = bounds.maxY();

        if (ceiling > maxY + 5) {
            float alpha = getEllipseDistAlpha(x, z, bounds);
            float depth = (ceiling - maxY) * 0.5F;
            maxY += NoiseUtil.round(depth * alpha);
        }

        for (int py = minY; py <= maxY; py++) {
            chunk.setBlockState(pos.set(x, py, z), air, false);
        }

        return true;
    }

    protected void reset() {
        pieceIterator.back(rigids.size());
    }

    private static float raise(int x, int z, BoundingBox bounds, float level, float surface, float borderRadius) {
        float radius2 = Math.max(1, borderRadius * borderRadius);
        float distAlpha = 1 - getDistAlpha(x, z, bounds, radius2);
        float alpha = NoiseUtil.pow(distAlpha, 2F - distAlpha);
        return NoiseUtil.lerp(surface, level, alpha);
    }

    protected static float getEllipseDistAlpha(int x, int z, BoundingBox bounds) {
        float radiusX = bounds.getXSpan() * 0.5F;
        float radiusZ = bounds.getZSpan() * 0.5F;
        float centerX = (bounds.minX() + bounds.maxX()) * 0.5F;
        float centerZ = (bounds.minZ() + bounds.maxZ()) * 0.5F;
        float dx = x - centerX;
        float dz = z - centerZ;
        float qx = (dx * dx) / (radiusX * radiusX);
        float qz = (dz * dz) / (radiusZ * radiusZ);
        return NoiseUtil.clamp(1 - qx - qz, 0, 1);
    }

    protected static float getDistAlpha(int x, int z, BoundingBox box, float radius2) {
        int dx = getDist(x, box.minX(), box.maxX());
        int dz = getDist(z, box.minZ(), box.maxZ());
        return getDistAlpha(dx, dz, radius2);
    }

    protected static float getDistAlpha(int dx, int dz, float radius2) {
        int d2 = dx * dx + dz * dz;
        if (d2 == 0)  return 0F;
        if (d2 >= radius2) return 1F;
        return NoiseUtil.sqrt(d2 / radius2);
    }

    protected static int getPieceY(StructurePiece piece) {
        int y = piece.getBoundingBox().minY();
        if (piece instanceof PoolElementStructurePiece element) {
            y += element.getGroundLevelDelta();
        }
        return y;
    }

    protected static int getDist(int pos, int min, int max) {
        return Math.max(0, Math.max(min - pos, pos - max));
    }
}
