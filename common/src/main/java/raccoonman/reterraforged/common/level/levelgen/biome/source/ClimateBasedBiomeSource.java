package raccoonman.reterraforged.common.level.levelgen.biome.source;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePoint;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePreset;

// TODO add support for composing climate presets in some way
public class ClimateBasedBiomeSource extends BiomeSource {
	public static final Codec<ClimateBasedBiomeSource> CODEC = ClimatePreset.CODEC.xmap(ClimateBasedBiomeSource::new, ClimateBasedBiomeSource::getPreset);
	
	private final Holder<ClimatePreset> climatePreset;

    public ClimateBasedBiomeSource(Holder<ClimatePreset> climatePreset) {
		this.climatePreset = climatePreset;
	}
    
    public Holder<ClimatePreset> getPreset() {
    	return this.climatePreset;
    }
	
	@Override
	protected Codec<ClimateBasedBiomeSource> codec() {
		return CODEC;
	}

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return this.climatePreset.value().params().values().stream().map(ClimatePoint::climate).flatMap((climate) -> {
			return climate.value().biomes().streamValues();
		});
	};
	
	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Sampler sampler) {
        DensityFunction.FunctionContext ctx = new DensityFunction.SinglePointContext(QuartPos.toBlock(x), QuartPos.toBlock(y), QuartPos.toBlock(z));
		return this.climatePreset.value().params().findValue(
			(float) sampler.temperature().compute(ctx),
			(float) sampler.humidity().compute(ctx),
			(float) sampler.continentalness().compute(ctx),
			(float) sampler.erosion().compute(ctx),
			(float) sampler.depth().compute(ctx)
		).value().biomes().getValue((float) sampler.weirdness().compute(ctx));
	}
	
	@Override
	public void addDebugInfo(List<String> lines, BlockPos pos, Sampler sampler) {
		DensityFunction.FunctionContext ctx = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
    	lines.add("");
		lines.add("[RTFBiomeSource]");
		lines.add("Temperature: " + sampler.temperature().compute(ctx));
		lines.add("Humidity: " + sampler.humidity().compute(ctx));
		lines.add("Continent: " + sampler.continentalness().compute(ctx));
		lines.add("Erosion: " + sampler.erosion().compute(ctx));
		lines.add("Depth: " + sampler.depth().compute(ctx));
		lines.add("Weirdness: " + sampler.weirdness().compute(ctx));
	}
}