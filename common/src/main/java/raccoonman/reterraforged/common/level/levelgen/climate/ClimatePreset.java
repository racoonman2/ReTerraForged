package raccoonman.reterraforged.common.level.levelgen.climate;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public record ClimatePreset(List<ClimatePreset.Entry> entries) {
	public static final Codec<ClimatePreset> DIRECT_CODEC = ClimatePreset.Entry.CODEC.listOf().xmap(ClimatePreset::new, ClimatePreset::entries);
	public static final Codec<Holder<ClimatePreset>> CODEC = RegistryFileCodec.create(RTFRegistries.CLIMATE_PRESET, DIRECT_CODEC);
	public static final Codec<HolderSet<ClimatePreset>> LIST_CODEC = RegistryCodecs.homogeneousList(RTFRegistries.CLIMATE_PRESET);
	
	public ParameterList<WeightMap<Holder<Biome>>> createParameterList() {
		List<Pair<ParameterPoint, WeightMap<Holder<Biome>>>> points = new ArrayList<>();
		for(ClimatePreset.Entry entry : this.entries) {
			WeightMap.Builder<Holder<Biome>> builder = new WeightMap.Builder<>();
			for(Holder<Climate> climate : entry.climates()) {
				builder.addAll(climate.value().biomes());
			}
			points.add(Pair.of(entry.point(), builder.build()));
		}
		return new ParameterList<>(points);
	}
	
	public record Entry(ParameterPoint point, HolderSet<Climate> climates) {
		public static final Codec<ClimatePreset.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ParameterPoint.CODEC.fieldOf("point").forGetter(ClimatePreset.Entry::point),
			Climate.LIST_CODEC.fieldOf("climates").forGetter(ClimatePreset.Entry::climates)
		).apply(instance, ClimatePreset.Entry::new));
	}
}
