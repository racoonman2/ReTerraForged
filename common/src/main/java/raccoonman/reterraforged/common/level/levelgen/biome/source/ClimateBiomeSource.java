package raccoonman.reterraforged.common.level.levelgen.biome.source;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePreset;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public class ClimateBiomeSource extends BiomeSource {
	public static final Codec<ClimateBiomeSource> CODEC = ClimatePreset.CODEC.xmap(ClimateBiomeSource::new, ClimateBiomeSource::getPreset);

	private final Holder<ClimatePreset> preset;
	//TODO no suppliers!
	private final Supplier<ParameterList<WeightMap<Holder<Biome>>>> parameterList;

	public ClimateBiomeSource(Holder<ClimatePreset> preset) {
		this.preset = preset;
		this.parameterList = Suppliers.memoize(() -> preset.value().createParameterList());
	}

	public Holder<ClimatePreset> getPreset() {
		return this.preset;
	}

	@Override
	protected Codec<ClimateBiomeSource> codec() {
		return CODEC;
	}

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return this.preset.value().entries().stream().map(ClimatePreset.Entry::climates).flatMap((climates) -> {
			return climates.stream().flatMap((climate) -> climate.value().biomes().streamValues());
		});
	};

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Sampler sampler) {
		int blockX = QuartPos.toBlock(x);
		int blockY = QuartPos.toBlock(y);
		int blockZ = QuartPos.toBlock(z);
		DensityFunction.SinglePointContext point = new DensityFunction.SinglePointContext(blockX, blockY, blockZ);
		float weirdness = (float) sampler.weirdness().compute(point);
		return this.parameterList.get().findValue(
			Climate.target((float) sampler.temperature().compute(point),
				(float) sampler.humidity().compute(point),
				(float) sampler.continentalness().compute(point),
				(float) sampler.erosion().compute(point),
				(float) sampler.depth().compute(point),
				weirdness
			)
		).getValue(weirdness);
	}

	@Override
	public void addDebugInfo(List<String> lines, BlockPos pos, Sampler sampler) {
		DensityFunction.FunctionContext ctx = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
		lines.add("");
		lines.add("[ClimateBiomeSource]");
		lines.add("Temperature: " + sampler.temperature().compute(ctx));
		lines.add("Humidity: " + sampler.humidity().compute(ctx));
		lines.add("Continent: " + sampler.continentalness().compute(ctx));
		lines.add("Erosion: " + sampler.erosion().compute(ctx));
		lines.add("Depth: " + sampler.depth().compute(ctx));
		lines.add("Weirdness: " + sampler.weirdness().compute(ctx));
	}
}