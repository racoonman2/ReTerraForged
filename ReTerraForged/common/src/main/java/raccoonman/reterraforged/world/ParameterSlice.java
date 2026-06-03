package raccoonman.reterraforged.world;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Climate;
import raccoonman.reterraforged.registry.RTFRegistries;

public record ParameterSlice(ParameterSlice.Color color, List<Climate.ParameterPoint> points) {
	public static final Codec<ParameterSlice> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Color.CODEC.fieldOf("color").forGetter(ParameterSlice::color),
		Climate.ParameterPoint.CODEC.listOf().fieldOf("points").forGetter(ParameterSlice::points)
	).apply(instance, ParameterSlice::new));
	public static final Codec<Holder<ParameterSlice>> CODEC = RegistryFileCodec.create(RTFRegistries.PARAMETER_SLICE, DIRECT_CODEC);
    public static final Codec<HolderSet<ParameterSlice>> LIST_CODEC = RegistryCodecs.homogeneousList(RTFRegistries.PARAMETER_SLICE, DIRECT_CODEC);
    
    public static Optional<Holder<ParameterSlice>> findSliceAtPoint(HolderSet<ParameterSlice> slices, Climate.Sampler sampler, BlockPos pos) {
    	Holder<ParameterSlice> terrain = null;
		for(Holder<ParameterSlice> holder : slices) {
			if(PointFinder.fitness(sampler, holder.value().points(), pos) == 0L) {
				terrain = holder;
				break;
			}
		}
		return Optional.ofNullable(terrain);
    }
    
	@SafeVarargs
	public static ParameterSlice of(int red, int green, int blue, List<Climate.ParameterPoint>... lists) {
		ImmutableList.Builder<Climate.ParameterPoint> points = ImmutableList.builder();
		for(List<Climate.ParameterPoint> list : lists) {
			points.addAll(list);
		}
		return new ParameterSlice(new Color(red, green, blue), points.build());
	}
    
    public record Color(int red, int green, int blue) {
    	public static final Codec<Color> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.INT.fieldOf("red").forGetter(Color::red),
    		Codec.INT.fieldOf("green").forGetter(Color::green),
    		Codec.INT.fieldOf("blue").forGetter(Color::blue)
    	).apply(instance, Color::new));
    }
}
