package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.KeyDispatchDataCodec;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellField;
import raccoonman.reterraforged.world.worldgen.heightmap.WorldLookup;
import raccoonman.reterraforged.world.worldgen.tile.Tile;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

public record CellSampler(Supplier<GeneratorContext> generatorContext, CellField field) implements MappedFunction {
	private static final ThreadLocal<Cache2d> LOCAL_CELL = ThreadLocal.withInitial(Cache2d::new);
	
	@Override
	public double compute(FunctionContext ctx) {
		WorldLookup worldLookup = this.generatorContext.get().lookup;
		Cell cell = LOCAL_CELL.get().getAndUpdate(worldLookup, ctx.blockX(), ctx.blockZ());
		return this.field.read(cell);
	}

	@Override
	public double minValue() {
		return 0.0F;
	}

	@Override
	public double maxValue() {
		return 1.0F;
	}

	public static boolean isCachedNoiseChunk(int cellCountXZ) {
		return cellCountXZ > 1;
	}
	
	public static class Cache2d {
		private long lastPos = Long.MAX_VALUE;
		private Cell cell = new Cell();
		
		public Cell getAndUpdate(WorldLookup lookup, int blockX, int blockZ) {
			blockX = QuartPos.toBlock(QuartPos.fromBlock(blockX));
			blockZ = QuartPos.toBlock(QuartPos.fromBlock(blockZ));
			
			long packedPos = PosUtil.pack(blockX, blockZ);
			if(this.lastPos != packedPos) {
				lookup.apply(this.cell.reset(), blockX, blockZ);
				this.lastPos = packedPos;
			}
			return this.cell;
		}
	}
	
	public class CacheChunk implements MappedFunction {
		@Nullable
		private Tile.Chunk chunk;
		private Cache2d cache2d;
		private int chunkX, chunkZ;

		public CacheChunk(@Nullable Tile.Chunk chunk, Cache2d cache2d, int chunkX, int chunkZ) {
			this.chunk = chunk;
			this.cache2d = cache2d;
			this.chunkX = chunkX;
			this.chunkZ = chunkZ;
		}

		@Override
		public double compute(FunctionContext ctx) {
			int blockX = ctx.blockX();
			int blockZ = ctx.blockZ();
			int chunkX = SectionPos.blockToSectionCoord(blockX);
			int chunkZ = SectionPos.blockToSectionCoord(blockZ);
			int quartBlockX = QuartPos.toBlock(QuartPos.fromBlock(blockX));
			int quartBlockZ = QuartPos.toBlock(QuartPos.fromBlock(blockZ));
			
			WorldLookup worldLookup = CellSampler.this.generatorContext.get().lookup;
			Cell cell = (this.chunk != null && this.chunkX == chunkX && this.chunkZ == chunkZ) ? 
				this.chunk.getCell(blockX, blockZ) :
				this.cache2d.getAndUpdate(worldLookup, quartBlockX, quartBlockZ);
			return CellSampler.this.field.read(cell);
		}

		@Override
		public double minValue() {
			return CellSampler.this.minValue();
		}

		@Override
		public double maxValue() {
			return CellSampler.this.maxValue();
		}
	}
	
	public record Marker(CellField field) implements MappedFunction.Marker {
		public static final Codec<Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				CellField.CODEC.fieldOf("field").forGetter(Marker::field)
		).apply(instance, Marker::new));
		
		@Override
		public KeyDispatchDataCodec<Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}