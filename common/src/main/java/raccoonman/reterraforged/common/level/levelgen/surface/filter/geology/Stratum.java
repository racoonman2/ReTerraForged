package raccoonman.reterraforged.common.level.levelgen.surface.filter.geology;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;

public record Stratum(BlockState state, DensityFunction depth) {
	public static final Codec<Stratum> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BlockState.CODEC.fieldOf("state").forGetter(Stratum::state),
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("depth").forGetter(Stratum::depth)
	).apply(instance, Stratum::new));
	
	public Stratum mapAll(DensityFunction.Visitor visitor) {
		return new Stratum(this.state, this.depth.mapAll(visitor));
	}
}