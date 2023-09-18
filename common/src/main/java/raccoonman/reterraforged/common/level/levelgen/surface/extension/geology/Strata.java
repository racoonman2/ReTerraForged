package raccoonman.reterraforged.common.level.levelgen.surface.extension.geology;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.DensityFunction;

public record Strata(List<Stratum> stratum) {
	public static final Codec<Strata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Stratum.CODEC.listOf().fieldOf("stratum").forGetter(Strata::stratum)
	).apply(instance, Strata::new));
	
	public Strata mapAll(DensityFunction.Visitor visitor) {
		return new Strata(this.stratum.stream().map((stratum) -> {
			return stratum.mapAll(visitor);
		}).toList());
	}
}
