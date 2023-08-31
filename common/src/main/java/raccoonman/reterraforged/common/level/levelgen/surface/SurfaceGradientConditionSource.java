package raccoonman.reterraforged.common.level.levelgen.surface;

import com.mojang.serialization.Codec;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules.Condition;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.LazyXZCondition;

public class SurfaceGradientConditionSource implements ConditionSource {
	public static final Codec<SurfaceGradientConditionSource> CODEC = Codec.unit(new SurfaceGradientConditionSource());
	
	@Override
	public Condition apply(Context ctx) {
		return new ConditionImpl(ctx);
	}

	@Override
	public KeyDispatchDataCodec<SurfaceGradientConditionSource> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}

	private static class ConditionImpl extends LazyXZCondition {
		
		protected ConditionImpl(Context ctx) {
			super(ctx);
			
		}

		@Override
		protected boolean compute() {
            int i = this.context.blockX & 0xF;
            int j = this.context.blockZ & 0xF;
            int k = Math.max(j - 1, 0);
            int l = Math.min(j + 1, 15);
            ChunkAccess chunkAccess = this.context.chunk;
            int m = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, k);
            int n = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, l);
            if (n >= m + 2) {
                return true;
            }
            int o = Math.max(i - 1, 0);
            int p = Math.min(i + 1, 15);
            int q = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, o, j);
            return q >= (chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, p, j)) + 2;
		}
	}
	
	private static class MutablePointContext implements DensityFunction.FunctionContext {
		public int x, y, z;

		public DensityFunction.FunctionContext forPoint(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}
		
		@Override
		public int blockX() {
			return this.x;
		}

		@Override
		public int blockY() {
			return this.y;
		}

		@Override
		public int blockZ() {
			return this.z;
		}
	}
}
