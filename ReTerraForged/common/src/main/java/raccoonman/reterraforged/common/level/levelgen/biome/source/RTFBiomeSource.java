package raccoonman.reterraforged.common.level.levelgen.biome.source;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SinglePointContext;
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateGroup;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateNoise;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePoint;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateSample;
import raccoonman.reterraforged.common.level.levelgen.densityfunctions.MutablePointContext;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseSample;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;
import raccoonman.reterraforged.common.util.CodecUtil;

public class RTFBiomeSource extends BiomeSource {
	private final Supplier<TerrainNoise> terrainNoise;
	private final Supplier<ClimateNoise> climateNoise;
	private final HolderSet<ClimateGroup> climateGroups;
    private final ThreadLocal<Point> localPoint = ThreadLocal.withInitial(Point::new);

    public RTFBiomeSource(Supplier<TerrainNoise> terrainSampler, Supplier<ClimateNoise> climateSampler, HolderSet<ClimateGroup> climateGroups) {
    	this.terrainNoise = terrainSampler;
		this.climateNoise = climateSampler; 
		this.climateGroups = climateGroups;
	}
	
	public HolderSet<ClimateGroup> getClimateGroups() {
		return this.climateGroups;
	}
	
	public int getClimateGroupIndex(ClimateSample sample) {
		int listCount = this.climateGroups.size();
		return Mth.clamp(Math.round((sample.climate * listCount) / listCount), 0, listCount - 1);
	}
	
	public Holder<ClimateGroup> getClimateGroup(ClimateSample sample) {
		return this.climateGroups.get(this.getClimateGroupIndex(sample));
	}
	
	public Holder<Climate> getClimate(NoiseSample sample, float depth) {
		return this.getClimateGroup(sample.climate).value().params().findValue(sample.climate.temperature, sample.climate.moisture, sample.terrain.continentNoise, sample.terrain.heightNoise, sample.terrain.riverNoise, depth);
	}

	@Override
	protected Codec<RTFBiomeSource> codec() {
		return CodecUtil.forError("CAN'T SERIALIZE");
	}

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		Stream<Holder<Biome>> stream = Stream.empty();
		
		for(Holder<ClimateGroup> holder : this.climateGroups) {
			stream = Stream.concat(stream, holder.value().params().values().stream().map(ClimatePoint::climate).flatMap((climate) -> {
				return climate.value().biomes().streamValues();
			}));
		}
		
		return stream;
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Sampler sampler) {
        int blockX = QuartPos.toBlock(x);
        int blockY = QuartPos.toBlock(y);
        int blockZ = QuartPos.toBlock(z);
        Point point = this.localPoint.get();
		this.climateNoise.get().sample(blockX, blockZ, point.sample.climate);
		this.terrainNoise.get().sample(blockX, blockZ, point.sample.terrain);
		return this.getClimate(point.sample, (float) sampler.depth().compute(new SinglePointContext(blockX, blockY, blockZ))).value().biomes().getValue(point.sample.climate.biomeNoise);
	}
	
	@Override
	public void addDebugInfo(List<String> lines, BlockPos pos, Sampler sampler) {
		NoiseSample sample = new NoiseSample();
		int x = pos.getX();
		int z = pos.getZ();
		this.climateNoise.get().sample(x, z, sample.climate);
		this.terrainNoise.get().sample(x, z, sample.terrain);

    	lines.add("");
		lines.add("[TFBiomeSource]");
    	lines.add("Climate Group Index: " + this.getClimateGroupIndex(sample.climate));
    	lines.add("Climate Group: " + name(this.getClimateGroup(sample.climate)));
    	lines.add("Climate: " + name(this.getClimate(sample, (float) sampler.depth().compute(new DensityFunction.SinglePointContext(x, pos.getY(), z)))));
    	lines.add("");
	}
	
	private static String name(Holder<?> holder) {
		return holder.unwrapKey().map((key) -> {
			return key.location().toString();
		}).orElse("(Inlined)");
	}
	
	private record Point(NoiseSample sample, MutablePointContext ctx) {

		public Point() {
			this(new NoiseSample(), new MutablePointContext());
		}
	}
}
