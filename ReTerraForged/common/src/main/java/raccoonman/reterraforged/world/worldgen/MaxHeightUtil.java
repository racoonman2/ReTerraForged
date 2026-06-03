package raccoonman.reterraforged.world.worldgen;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.Beardifier.Rigid;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class MaxHeightUtil {

	public static int getMaxHeight(int chunkX, int chunkZ, int maxHeight, NoiseGeneratorSettings generatorSettings, NoiseSettings noiseSettings) {
		int minY = noiseSettings.minY();
		int height = noiseSettings.height();
		int cellHeight = noiseSettings.getCellHeight();
		int seaLevel = generatorSettings.seaLevel();
		int scaledDynamicHeight = -minY + Math.max(seaLevel, maxHeight + 1);
		return Math.min(height, ((scaledDynamicHeight / cellHeight) + 1) * cellHeight);
	}
	
	public static int getMaxHeight(ChunkPos chunkPos, int maxHeight, NoiseGeneratorSettings generatorSettings, NoiseSettings noiseSettings, StructureManager structureManager) {
		int dynamicHeight = getMaxHeight(chunkPos.x, chunkPos.z, maxHeight, generatorSettings, noiseSettings);
		dynamicHeight = Math.clamp(getHighestStructureY(chunkPos, noiseSettings, structureManager), dynamicHeight, noiseSettings.height());
		return dynamicHeight;
	}
	
	public static int getMaxHeight(int chunkX, int chunkZ, int maxHeight, NoiseGeneratorSettings generatorSettings, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifierOrMarker) {
		int dynamicHeight = getMaxHeight(chunkX, chunkZ, maxHeight, generatorSettings, noiseSettings);
		if(beardifierOrMarker instanceof Beardifier beardifier) {
			dynamicHeight = Math.clamp(getHighestStructureY(noiseSettings, beardifier.pieces, beardifier.junctions), dynamicHeight, noiseSettings.height());
		}
		return dynamicHeight;
	}
	
	private static int getHighestStructureY(NoiseSettings noiseSettings, List<Rigid> pieces, List<JigsawJunction> junctions) {
        int highestY = Integer.MIN_VALUE;
        for(Rigid rigid : pieces) {
        	BoundingBox boundingBox = rigid.box();
        	highestY = Math.max(highestY, boundingBox.maxY());
        }
        // TODO calculate junction height
        return -noiseSettings.minY() + highestY;
	}
	
	private static int getHighestStructureY(ChunkPos chunkPos, NoiseSettings noiseSettings, StructureManager structureManager) {
		int minBlockX = chunkPos.getMinBlockX();
        int minBlockZ = chunkPos.getMinBlockZ();
        List<Rigid> pieces = new ObjectArrayList<>(10);
        List<JigsawJunction> junctions = new ObjectArrayList<>(32);
        structureManager.startsForStructure(chunkPos, structure -> structure.terrainAdaptation() != TerrainAdjustment.NONE).forEach(structureStart -> {
            TerrainAdjustment terrainAdjustment = structureStart.getStructure().terrainAdaptation();
            for (StructurePiece structurePiece : structureStart.getPieces()) {
                if (!structurePiece.isCloseToChunk(chunkPos, 12)) continue;
                if (structurePiece instanceof PoolElementStructurePiece poolElementStructurePiece) {
                    StructureTemplatePool.Projection projection = poolElementStructurePiece.getElement().getProjection();
                    if (projection == StructureTemplatePool.Projection.RIGID) {
                        pieces.add(new Rigid(poolElementStructurePiece.getBoundingBox(), terrainAdjustment, poolElementStructurePiece.getGroundLevelDelta()));
                    }
                    for (JigsawJunction jigsawJunction : poolElementStructurePiece.getJunctions()) {
                        int sourceX = jigsawJunction.getSourceX();
                        int sourceZ = jigsawJunction.getSourceZ();
                        if (sourceX <= minBlockX - 12 || sourceZ <= minBlockZ - 12 || sourceX >= minBlockX + 15 + 12 || sourceZ >= minBlockZ + 15 + 12) continue;
                        junctions.add(jigsawJunction);
                    }
                    continue;
                }
                pieces.add(new Rigid(structurePiece.getBoundingBox(), terrainAdjustment, 0));
            }
        });
        return getHighestStructureY(noiseSettings, pieces, junctions);
    }
}
