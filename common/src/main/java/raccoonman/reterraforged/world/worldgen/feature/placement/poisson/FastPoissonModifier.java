package raccoonman.reterraforged.world.worldgen.feature.placement.poisson;

import java.util.Random;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile;
import raccoonman.reterraforged.world.worldgen.feature.placement.RTFPlacementModifiers;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class FastPoissonModifier extends PlacementModifier {
	public static final Codec<FastPoissonModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("radius").forGetter((p) -> p.radius),
		Codec.FLOAT.fieldOf("scale").forGetter((p) -> p.scale),
		Codec.FLOAT.fieldOf("jitter").forGetter((p) -> p.jitter),
		Codec.FLOAT.fieldOf("biome_fade").forGetter((p) -> p.biomeFade),
		Codec.INT.fieldOf("density_variance_scale").forGetter((p) -> p.densityVariationScale),
		Codec.FLOAT.fieldOf("density_variation").forGetter((p) -> p.densityVariation)
	).apply(instance, FastPoissonModifier::new));
	
	protected int radius;
	protected float scale;
	protected float jitter;
	protected float biomeFade;
	protected int densityVariationScale;
	protected float densityVariation;
	
	public FastPoissonModifier(
		int radius,
		float scale,
		float jitter,
		float biomeFade,
		int densityVariationScale,
		float densityVariation
	) {
		this.radius = radius;
		this.scale = scale;
		this.jitter = jitter;
		this.biomeFade = biomeFade;
		this.densityVariationScale = densityVariationScale;
		this.densityVariation = densityVariation;
	}
	
	@Override
	public Stream<BlockPos> getPositions(PlacementContext ctx, RandomSource random, BlockPos pos) {
		WorldGenLevel level = ctx.getLevel();
		ChunkAccess chunk = ctx.getLevel().getChunk(pos);
		long levelSeed = level.getSeed();
		int seed = (int) levelSeed + 234523;
		ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        FastPoisson poisson = FastPoisson.LOCAL_POISSON.get();
        DensityNoise density = this.getDensityNoise(seed, chunkPos, level.getLevel().getChunkSource().randomState());
        FastPoissonContext poissonConfig = new FastPoissonContext(this.radius, this.jitter, this.scale, density);
        Stream.Builder<BlockPos> builder = Stream.builder();
        poisson.visit(seed, chunkX, chunkZ, new Random(levelSeed), poissonConfig, builder, (x, z, b) -> {
        	b.accept(new BlockPos(x, 0, z));
        });
        return builder.build();
    }

	@Override
	public PlacementModifierType<FastPoissonModifier> type() {
		return RTFPlacementModifiers.FAST_POISSON;
	}

	private DensityNoise getDensityNoise(int seed, ChunkPos chunkPos, RandomState randomState) {
		BiomeVariance biomeVariance = BiomeVariance.NONE;
		
		if (this.biomeFade > BiomeVariance.MIN_FADE) {
			if((Object) randomState instanceof RTFRandomState rtfRandomState) {
				Tile.Chunk reader = rtfRandomState.generatorContext().cache.provideAtChunk(chunkPos.x, chunkPos.z).getChunkReader(chunkPos.x, chunkPos.z);
				if (reader != null) {
					biomeVariance = new BiomeVariance(reader, this.biomeFade);
				}
			}
		}

		Noise densityVariance = Noises.one();
		if (this.densityVariation > 0) {
			densityVariance = Noises.simplex(seed + 1, this.densityVariationScale, 1);
			densityVariance = Noises.mul(densityVariance, this.densityVariation);
			densityVariance = Noises.add(densityVariance, 1.0F - this.densityVariation);
		}

		return new DensityNoise(biomeVariance, densityVariance);
	}
}
