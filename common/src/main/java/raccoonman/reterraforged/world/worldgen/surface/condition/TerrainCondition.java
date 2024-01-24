package raccoonman.reterraforged.world.worldgen.surface.condition;

import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;

class TerrainCondition extends CellCondition {
	private Set<Terrain> terrain;
	
	public TerrainCondition(Context context, Set<Terrain> terrain) {
		super(context);
		
		this.terrain = terrain;
	}

	@Override
	public boolean test(Cell cell, int x, int z) {
		return this.terrain.contains(cell.terrain);
	}
	
	public record Source(Set<Terrain> terrain) implements SurfaceRules.ConditionSource {
		public static final Codec<Source> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Terrain.CODEC.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("terrain").forGetter(Source::terrain)
		).apply(instance, Source::new));

		@Override
		public TerrainCondition apply(Context ctx) {
			return new TerrainCondition(ctx, this.terrain);
		}

		@Override
		public KeyDispatchDataCodec<Source> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
