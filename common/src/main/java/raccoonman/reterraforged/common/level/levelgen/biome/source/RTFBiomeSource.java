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
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePoint;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePreset;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseSample;

public class RTFBiomeSource extends BiomeSource {
	public static final Codec<RTFBiomeSource> CODEC = ClimatePreset.CODEC.xmap(RTFBiomeSource::new, RTFBiomeSource::getPreset);
	
	private final Holder<ClimatePreset> climatePreset;

    public RTFBiomeSource(Holder<ClimatePreset> climatePreset) {
		this.climatePreset = climatePreset;
	}
    
    public Holder<ClimatePreset> getPreset() {
    	return this.climatePreset;
    }
	
//	public int getClimateGroupIndex(ClimateSample sample) {
//		int listCount = this.climatePreset.size();
//		return Mth.clamp(Math.round((sample.climate * listCount) / listCount), 0, listCount - 1);
//	}
	
//	public Holder<ClimatePreset> getClimateGroup(ClimateSample sample) {
//		return this.climatePreset.get(this.getClimateGroupIndex(sample));
//	}
	
	public Holder<Climate> getClimate(NoiseSample sample, float depth) {
		return this.climatePreset.value().params().findValue(sample.climate.temperature, sample.climate.moisture, sample.terrain.continentNoise, sample.terrain.heightNoise, sample.terrain.waterNoise, depth);
	}

	@Override
	protected Codec<RTFBiomeSource> codec() {
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
			1.0F - (float) sampler.depth().compute(ctx),
			(float) sampler.erosion().compute(ctx),
			(float) sampler.depth().compute(ctx)			
		).value().biomes().getValue(1);
	}
	
	@Override
	public void addDebugInfo(List<String> lines, BlockPos pos, Sampler sampler) {
//		NoiseSample sample = new NoiseSample();
//		int x = pos.getX();
//		int z = pos.getZ();
//		this.climateNoise.get().sample(x, z, sample.climate);
//		this.terrainNoise.get().sample(x, z, sample.terrain);
//
		DensityFunction.FunctionContext ctx = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
    	lines.add("");
		lines.add("[RTFBiomeSource]");
		lines.add("Temperature: " + sampler.temperature().compute(ctx));
		lines.add("Humidity: " + sampler.humidity().compute(ctx));
		lines.add("Continent: " + sampler.continentalness().compute(ctx));
		lines.add("Erosion: " + sampler.erosion().compute(ctx));
		lines.add("Depth: " + sampler.depth().compute(ctx));
		lines.add("Weirdness: " + sampler.weirdness().compute(ctx));
//    	lines.add("Climate Group Index: " + this.getClimateGroupIndex(sample.climate));
//    	lines.add("Climate Group: " + name(this.getClimateGroup(sample.climate)));
//    	lines.add("Climate: " + name(this.getClimate(sample, (float) sampler.depth().compute(new DensityFunction.SinglePointContext(x, pos.getY(), z)))));
    	lines.add("");
	}
//	
//	private static String name(Holder<?> holder) {
//		return holder.unwrapKey().map((key) -> {
//			return key.location().toString();
//		}).orElse("(Inlined)");
//	}
}
