package raccoonman.reterraforged.common.registries.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePoint;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePreset;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseTree;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public final class RTFClimatePresets {
	public static final ResourceKey<ClimatePreset> DEFAULT = resolve("default");

	public static void register(BootstapContext<ClimatePreset> ctx) {
		HolderGetter<Climate> climates = ctx.lookup(RTFRegistries.CLIMATE);

		ctx.register(DEFAULT, createDefault(climates));
	}

	private static ClimatePreset createDefault(HolderGetter<Climate> climates) {
		List<ClimatePoint> points = new ArrayList<>();
		Collections.addAll(points,
			ClimatePoint.of(
				climates.getOrThrow(RTFClimates.TEMPERATE),
				NoiseTree.Parameter.ignore(),
				NoiseTree.Parameter.ignore(),
				NoiseTree.Parameter.ignore(),
				NoiseTree.Parameter.ignore(),
				NoiseTree.Parameter.span(0.0F, 0.825F)
			),
			ClimatePoint.of(
				climates.getOrThrow(RTFClimates.DEEP),
				NoiseTree.Parameter.ignore(),
				NoiseTree.Parameter.ignore(),
				NoiseTree.Parameter.ignore(), 
				NoiseTree.Parameter.ignore(),
				NoiseTree.Parameter.point(1.0F)
			)
		);
		return new ClimatePreset(new NoiseTree.PointList<>(5, ImmutableList.copyOf(points)));
	}

	private static ResourceKey<ClimatePreset> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.CLIMATE_PRESET, path);
	}
}
