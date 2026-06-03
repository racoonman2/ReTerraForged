package raccoonman.reterraforged.world.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.densityfunction.MutableFunctionContext;

public class DiskFeature extends Feature<DiskFeature.Config> {

    public DiskFeature(Codec<DiskFeature.Config> codec) {
		super(codec);
	}
    
	@Override
	public boolean place(FeaturePlaceContext<DiskFeature.Config> featureCtx) {
		WorldGenLevel level = featureCtx.level();
		RandomSource random = featureCtx.random();
		BlockPos pos = featureCtx.origin();
		Config config = featureCtx.config();
		ChunkGenerator generator = featureCtx.chunkGenerator();
		
		if(!level.getFluidState(pos).is(FluidTags.WATER) || !(level.getChunkSource() instanceof ServerChunkCache serverChunkSource)) {
			return false;
		}
		
		RandomState randomState = serverChunkSource.randomState();
		RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);
        	
		DensityFunction domain = config.domain.value().mapAll(rtfRandomState.globalFunctionVisitor());
		BlockPredicate target = config.target();
		int ySize = config.ySize().sample(random);
		int radius = config.radius().sample(random);
		double radiusSq = (radius * radius) * config.radiusMultiplier();
		
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		int placed = 0;
        	
		MutableFunctionContext functionCtx = new MutableFunctionContext();
		for(int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
			for(int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
				int dx = x - pos.getX();
				int dz = z - pos.getZ();
				
				functionCtx.at(x, z);
				double rad2 = domain.compute(functionCtx) * radiusSq;
				if (dx * dx + dz * dz <= rad2) {
					for(int y = pos.getY() - ySize; y <= pos.getY() + ySize && y + 1 < generator.getGenDepth(); ++y) {
						blockPos.set(x, y, z);
						
						if(target.test(level, blockPos)) {
							level.setBlock(blockPos, config.stateProvider().getState(level, random, blockPos), Block.UPDATE_CLIENTS);
							placed++;
						}
					}
				}
			}
		}
		return placed > 0;
	}
	
	public record Config(Holder<DensityFunction> domain, RuleBasedBlockStateProvider stateProvider, BlockPredicate target, IntProvider radius, IntProvider ySize, double radiusMultiplier) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DensityFunction.CODEC.fieldOf("domain").forGetter(Config::domain),
			RuleBasedBlockStateProvider.CODEC.fieldOf("strata_provider").forGetter(Config::stateProvider),
			BlockPredicate.CODEC.fieldOf("target").forGetter(Config::target),
			IntProvider.CODEC.fieldOf("radius").forGetter(Config::radius),
			IntProvider.CODEC.fieldOf("y_size").forGetter(Config::ySize),
			Codec.DOUBLE.fieldOf("radius_multiplier").forGetter(Config::radiusMultiplier)
		).apply(instance, Config::new));
	}
}