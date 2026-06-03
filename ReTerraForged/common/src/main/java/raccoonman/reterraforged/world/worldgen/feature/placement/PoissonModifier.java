package raccoonman.reterraforged.world.worldgen.feature.placement;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFRandomState;

public class PoissonModifier extends PlacementModifier {
	public static final MapCodec<PoissonModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("radius").forGetter((p) -> p.radius),
		Codec.FLOAT.fieldOf("scale").forGetter((p) -> p.scale),
		Codec.FLOAT.fieldOf("jitter").forGetter((p) -> p.jitter),
		DensityFunction.CODEC.fieldOf("density").forGetter((p) -> p.density)
	).apply(instance, PoissonModifier::new));
	
    private static final ThreadLocal<PoissonSampler> LOCAL_SAMPLER = ThreadLocal.withInitial(PoissonSampler::new);
	
	private int radius;
	private float scale;
	private float jitter;
	private Holder<DensityFunction> density;
	
	public PoissonModifier(int radius, float scale, float jitter, Holder<DensityFunction> density) {
		this.radius = radius;
		this.scale = scale;
		this.jitter = jitter;
		this.density = density;
	}
	
	@Override
	public Stream<BlockPos> getPositions(PlacementContext ctx, RandomSource random, BlockPos pos) {
		WorldGenLevel level = ctx.getLevel();
		long seed = level.getSeed();

		RandomState randomState = level.getLevel().getChunkSource().randomState();
		RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);
		DensityFunction densityFunction = this.density.value().mapAll(rtfRandomState.globalFunctionVisitor());
		
		PoissonSampler poisson = LOCAL_SAMPLER.get();
        PoissonSampler.Context poissonCtx = new PoissonSampler.Context(this.radius, this.jitter, this.scale, densityFunction);
        Stream.Builder<BlockPos> builder = Stream.builder();

        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
		poisson.sample(seed, chunkX, chunkZ, poissonCtx, (x, z) -> {
        	builder.accept(new BlockPos(x, 0, z));
        });
        return builder.build();
    }

	@Override
	public PlacementModifierType<PoissonModifier> type() {
		return RTFPlacementModifiers.POISSON;
	}
}
