package raccoonman.reterraforged.common.level.levelgen.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseTree;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public record ClimatePreset(NoiseTree.PointList<Holder<Climate>, ClimatePoint> params) {
	public static final Codec<ClimatePreset> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		NoiseTree.PointList.codec(ClimatePoint.CODEC.listOf(), 6).fieldOf("params").forGetter(ClimatePreset::params)
	).apply(instance, ClimatePreset::new));
	public static final Codec<Holder<ClimatePreset>> CODEC = RegistryFileCodec.create(RTFRegistries.CLIMATE_PRESET, DIRECT_CODEC);
}
