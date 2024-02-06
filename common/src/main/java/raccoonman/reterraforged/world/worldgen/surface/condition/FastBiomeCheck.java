package raccoonman.reterraforged.world.worldgen.surface.condition;

import com.mojang.serialization.Codec;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

class FastBiomeCheck implements SurfaceRules.Condition {
	private SurfaceRules.Context ctx;

	public FastBiomeCheck(SurfaceRules.Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public boolean test() {
		return false;
	}
	
	public record Source() implements SurfaceRules.ConditionSource {
		public static final Codec<Source> CODEC = Codec.unit(Source::new);
		
		@Override
		public FastBiomeCheck apply(SurfaceRules.Context ctx) {
			return new FastBiomeCheck(ctx);
		}

		@Override
		public KeyDispatchDataCodec<Source> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
