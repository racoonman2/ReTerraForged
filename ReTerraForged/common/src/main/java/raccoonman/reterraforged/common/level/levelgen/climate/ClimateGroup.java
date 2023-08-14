package raccoonman.reterraforged.common.level.levelgen.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseTree;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public record ClimateGroup(NoiseTree.PointList<Holder<Climate>, ClimatePoint> params) {
	public static final Codec<ClimateGroup> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		NoiseTree.PointList.codec(ClimatePoint.CODEC, 6).fieldOf("params").forGetter(ClimateGroup::params)
	).apply(instance, ClimateGroup::new));
	public static final Codec<Holder<ClimateGroup>> CODEC = RegistryFileCodec.create(RTFRegistries.CLIMATE_GROUP, DIRECT_CODEC);
    public static final Codec<HolderSet<ClimateGroup>> LIST_CODEC = RegistryCodecs.homogeneousList(RTFRegistries.CLIMATE_GROUP, DIRECT_CODEC);
}
