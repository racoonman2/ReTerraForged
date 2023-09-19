package raccoonman.reterraforged.common.level.levelgen.geology;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public record Strata(ResourceLocation name, List<Stratum> strata) {
	public static final Codec<Strata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("name").forGetter(Strata::name),
		Stratum.CODEC.listOf().fieldOf("strata").forGetter(Strata::strata)
	).apply(instance, Strata::new));
	
	public Strata {
		strata = ImmutableList.copyOf(strata);
	}
	
	public Geology generateGeology(PositionalRandomFactory randomFactory) {
		RandomSource randomSource = randomFactory.fromHashOf(this.name);
		List<Geology.Layer> layers = new LinkedList<>();
		for(Stratum stratum : this.strata) {
			layers.addAll(stratum.generateLayers(randomSource.fork()));
		}
		return new Geology(layers);
	}
}
