package raccoonman.reterraforged.common.registries.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePreset;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public final class RTFClimatePresets {
	public static final ResourceKey<ClimatePreset> DEFAULT = resolve("default");

	public static void register(BootstapContext<ClimatePreset> ctx) {
		HolderGetter<Climate> climates = ctx.lookup(RTFRegistries.CLIMATE);

		ctx.register(DEFAULT, createDefault(climates));
	}

	private static ClimatePreset createDefault(HolderGetter<Climate> climates) {
		List<ClimatePreset.Entry> points = new ArrayList<>();
		Collections.addAll(points,
			new ClimatePreset.Entry(
				new ParameterPoint(
					Parameter.point(0.0F), 
					Parameter.point(0.5F),
					Parameter.span(0.55F, 1.0F),
					Parameter.point(1.0F),
					Parameter.point(0.0F),
					Parameter.point(0.0F), 
					0L
				),
				HolderSet.direct(climates.getOrThrow(RTFClimates.TEMPERATE))
			),
			new ClimatePreset.Entry(
				new ParameterPoint(
					Parameter.point(0.0F), 
					Parameter.point(0.5F),
					Parameter.span(0.0F, 0.55F),
					Parameter.point(1.0F),
					Parameter.point(0.0F),
					Parameter.point(0.0F), 
					0L
				),
				HolderSet.direct(climates.getOrThrow(RTFClimates.OCEAN))
			)
		);
		return new ClimatePreset(points);
	}

	private static ResourceKey<ClimatePreset> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.CLIMATE_PRESET, path);
	}
}
