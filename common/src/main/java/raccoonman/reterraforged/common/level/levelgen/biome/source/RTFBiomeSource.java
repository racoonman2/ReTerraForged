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
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePoint;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePreset;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseSample;
import raccoonman.reterraforged.common.level.levelgen.noise.density.MutablePointContext;

public class RTFBiomeSource extends BiomeSource {
	public static final Codec<RTFBiomeSource> CODEC = ClimatePreset.CODEC.xmap(RTFBiomeSource::new, RTFBiomeSource::getPreset);
	
	private final Holder<ClimatePreset> climatePreset;
    private final ThreadLocal<Point> localPoint = ThreadLocal.withInitial(Point::new);

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
        int blockX = QuartPos.toBlock(x);
        int blockY = QuartPos.toBlock(y);
        int blockZ = QuartPos.toBlock(z);
        Point point = this.localPoint.get();
        point.sample.terrain.continentNoise = 1;
        point.sample.terrain.baseNoise = 0;
        point.sample.terrain.heightNoise = 0;
        point.sample.terrain.waterNoise = 1;
        point.sample.climate.biomeNoise = 0.5F;
        point.sample.climate.biomeEdgeNoise = 0.5F;
        point.sample.climate.moisture = 0.5F;
        point.sample.climate.temperature = 0.5F;
        point.sample.climate.climate = 0.5F;
//		this.climateNoise.get().sample(blockX, blockZ, point.sample.climate);
//		this.terrainNoise.get().sample(blockX, blockZ, point.sample.terrain);
		point.ctx.x = blockX;
		point.ctx.y = blockY;
		point.ctx.z = blockZ;
		return this.getClimate(point.sample, (float) sampler.depth().compute(point.ctx)).value().biomes().getValue(point.sample.climate.biomeNoise);
	}
	
	@Override
	public void addDebugInfo(List<String> lines, BlockPos pos, Sampler sampler) {
//		NoiseSample sample = new NoiseSample();
//		int x = pos.getX();
//		int z = pos.getZ();
//		this.climateNoise.get().sample(x, z, sample.climate);
//		this.terrainNoise.get().sample(x, z, sample.terrain);
//
//    	lines.add("");
//		lines.add("[TFBiomeSource]");
//    	lines.add("Climate Group Index: " + this.getClimateGroupIndex(sample.climate));
//    	lines.add("Climate Group: " + name(this.getClimateGroup(sample.climate)));
//    	lines.add("Climate: " + name(this.getClimate(sample, (float) sampler.depth().compute(new DensityFunction.SinglePointContext(x, pos.getY(), z)))));
//    	lines.add("");
	}
//	
//	private static String name(Holder<?> holder) {
//		return holder.unwrapKey().map((key) -> {
//			return key.location().toString();
//		}).orElse("(Inlined)");
//	}
	
	private record Point(NoiseSample sample, MutablePointContext ctx) {

		public Point() {
			this(new NoiseSample(), new MutablePointContext());
		}
	}
}
