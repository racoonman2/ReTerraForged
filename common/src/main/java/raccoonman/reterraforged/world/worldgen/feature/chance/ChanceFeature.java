package raccoonman.reterraforged.world.worldgen.feature.chance;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.world.worldgen.feature.chance.ChanceFeature.Config;

public class ChanceFeature extends Feature<Config> {

	public ChanceFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> ctx) {
		RandomSource random = ctx.random();
		Config config = ctx.config();
		
		int entryCount = config.entries.size();
		ChanceContext chanceCtx = ChanceContext.make(entryCount);
        for (int i = 0; i < entryCount; i++) {
            Entry entry = config.entries.get(i);
            float chance = entry.getChance(chanceCtx, ctx);
            chanceCtx.record(i, chance);
        }

        int index = chanceCtx.nextIndex(random);
        if (index > -1) {
            return config.entries.get(index).feature.value().place(ctx.level(), ctx.chunkGenerator(), random, ctx.origin());
        }
		return false;
	}
	
	public record Entry(Holder<PlacedFeature> feature, float chance, List<ChanceModifier> modifiers) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlacedFeature.CODEC.fieldOf("feature").forGetter(Entry::feature),
			Codec.FLOAT.fieldOf("chance").forGetter(Entry::chance),
			ChanceModifier.CODEC.listOf().fieldOf("modifiers").forGetter(Entry::modifiers)
		).apply(instance, Entry::new));
		
		public float getChance(ChanceContext chanceCtx, FeaturePlaceContext<?> placeCtx) {
			float chance = this.chance;
			for (ChanceModifier modifier : this.modifiers) {
				chance *= modifier.getChance(chanceCtx, placeCtx);
			}
			return chance;
		}
	}
	
	public record Config(List<Entry> entries) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Entry.CODEC.listOf().fieldOf("entries").forGetter(Config::entries)
		).apply(instance, Config::new));
	}
}
