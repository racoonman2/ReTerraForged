package raccoonman.reterraforged.world.worldgen.structure.rule;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.heightmap.WorldLookup;
import raccoonman.reterraforged.world.worldgen.cell.terrain.Terrain;
import raccoonman.reterraforged.world.worldgen.cell.terrain.TerrainType;

record CellTest(float cutoff, Set<Terrain> terrainTypeBlacklist) implements StructureRule {
	public static final Codec<CellTest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("cutoff").forGetter(CellTest::cutoff),
		Codec.STRING.xmap(TerrainType::get, Terrain::getName).listOf().fieldOf("terrain_type_blacklist").forGetter((set) -> set.terrainTypeBlacklist().stream().toList())
	).apply(instance, CellTest::new));

	public CellTest(float cutoff, List<Terrain> terrainTypeBlacklist) {
		this(cutoff, ImmutableSet.copyOf(terrainTypeBlacklist));
	}
	
	@Override
	public boolean test(WorldGenLevel level, BlockPos pos, BoundingBox boundingBox) {
		int startX = pos.getX();
		int startZ = pos.getZ();
		RandomState randomState = level.getLevel().getChunkSource().randomState();
		if((Object) randomState instanceof RTFRandomState rtfRandomState) {
			@Nullable
			GeneratorContext generatorContext = rtfRandomState.generatorContext();
			if(generatorContext != null) {
				WorldLookup worldLookup = generatorContext.lookup;
				Cell cell = new Cell();
				for(int x = startX; x < startX + boundingBox.getXSpan(); x++) {
					for(int z = startZ; z < startZ + boundingBox.getZSpan(); z++) {
						worldLookup.applyCell(cell.reset(), x, z);
						
						if(cell.riverMask < this.cutoff || this.terrainTypeBlacklist.contains(cell.terrain)) {
							return false;
						}
					}	
				}
			}
			return true;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public Codec<CellTest> codec() {
		return CODEC;
	}
}
