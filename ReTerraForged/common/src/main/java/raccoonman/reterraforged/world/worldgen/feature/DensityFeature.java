package raccoonman.reterraforged.world.worldgen.feature;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.densityfunction.ElevationFunction;
import raccoonman.reterraforged.world.worldgen.feature.DensityFeature.Config;

public class DensityFeature extends Feature<Config> {

	public DensityFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> featureCtx) {
		WorldGenLevel level = featureCtx.level();
		RandomSource random = featureCtx.random();
		Config config = featureCtx.config();
		BlockPos origin = featureCtx.origin();
		RandomState randomState = level.getLevel().getChunkSource().randomState();
		FunctionContext functionCtx = new DensityFunction.SinglePointContext(origin.getX(), origin.getY(), origin.getZ());
		RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);

		int seaLevel = level.getSeaLevel();
		DensityFunction.Visitor functionVisitor = (function) -> {
			if(function instanceof ElevationFunction.Marker marker) {
				return new ElevationFunction(seaLevel, marker.scaler());
			}
			return rtfRandomState.globalFunctionVisitor().apply(function);
		};
		
		int entryCount = config.entries.size();
		Context densityCtx = new Context(entryCount);
        for (int i = 0; i < entryCount; i++) {
            Entry entry = config.entries.get(i);
            double chance = entry.computeDensity(functionCtx, functionVisitor);
            densityCtx.record(i, chance);
        }

        int index = densityCtx.nextIndex(random);
        if (index != -1) {
            return config.entries.get(index).feature.value().place(level, featureCtx.chunkGenerator(), random, origin);
        }
		return false;
	}
	
	public record Entry(Holder<PlacedFeature> feature, Holder<DensityFunction> function) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlacedFeature.CODEC.fieldOf("feature").forGetter(Entry::feature),
			DensityFunction.CODEC.fieldOf("function").forGetter(Entry::function)
		).apply(instance, Entry::new));
		
		public double computeDensity(FunctionContext context, DensityFunction.Visitor functionVisitor) {
			return this.function.value().mapAll(functionVisitor).compute(context);
		}
	}
	
	public record Config(List<Entry> entries) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Entry.CODEC.listOf().fieldOf("entries").forGetter(Config::entries)
		).apply(instance, Config::new));
	}
	
	private static class Context {
	    private int length;
	    private double total;
	    private double[] buffer;

	    public Context(int size) {
	    	this.buffer = new double[size];
	    }
	    
	    void record(int index, double chance) {
	    	this.buffer[index] = chance;
	    	this.total += chance;
	    	this.length++;
	    }

	    int nextIndex(RandomSource random) {
	        if (this.total == 0) {
	            return -1;
	        }
	        double value = 0.0F;
	        double chance = this.total * random.nextFloat();
	        for (int i = 0; i < this.length; i++) {
	            value += this.buffer[i];
	            if (value >= chance) {
	                return i;
	            }
	        }
	        return -1;
	    }
	}
}
