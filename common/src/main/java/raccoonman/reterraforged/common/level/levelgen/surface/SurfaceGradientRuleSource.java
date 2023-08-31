package raccoonman.reterraforged.common.level.levelgen.surface;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import raccoonman.reterraforged.common.registries.data.tags.RTFBlockTags;

public class SurfaceGradientRuleSource implements RuleSource {
	public static final Codec<SurfaceGradientRuleSource> CODEC = Codec.unit(new SurfaceGradientRuleSource());
	
	@Override
	public SurfaceRule apply(Context ctx) {
        BlockPos.MutableBlockPos pos = new MutableBlockPos();
        BlockState solid = findSolid(pos.set(ctx.pos), ctx.chunk);
        return (x, y, z) -> solid;
	}

	@Override
	public KeyDispatchDataCodec<SurfaceGradientRuleSource> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}

	protected static BlockState findSolid(BlockPos.MutableBlockPos pos, ChunkAccess chunk) {
		var state = chunk.getBlockState(pos);

		if (!isErodible(state))
			return null;

		for (int y = pos.getY() - 1, bottom = Math.max(0, pos.getY() - 20); y > bottom; y--) {
			state = chunk.getBlockState(pos.setY(y));

			// Stop when we hit non-erodable material
			if (!isErodible(state)) {
				return state;
			}
		}

		return null;
	}

	private static boolean isErodible(BlockState state) {
		return state.is(RTFBlockTags.ERODIBLE);
	}
}
