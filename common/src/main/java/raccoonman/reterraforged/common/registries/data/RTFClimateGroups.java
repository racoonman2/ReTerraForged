package raccoonman.reterraforged.common.registries.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateGroup;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimatePoint;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseTree;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.registries.data.tags.RTFClimateTags;

public final class RTFClimateGroups {
	public static final ResourceKey<ClimateGroup> DEFAULT = resolve("default");

	public static void register(BootstapContext<ClimateGroup> ctx) {
		HolderGetter<Climate> climates = ctx.lookup(RTFRegistries.CLIMATE);

		ctx.register(DEFAULT, new ClimateGroup(
			new NoiseTree.PointList<>(6, ImmutableList.of(
				ClimatePoint.of(
					climates,
					Either.left(RTFClimateTags.TEMPERATE),
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.max(0.95F)
				),
				ClimatePoint.of(
					climates,
					Either.left(RTFClimateTags.DEEP),
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.ignore(), 
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.ignore(),
					NoiseTree.Parameter.min(0.95F)
				)
			)
		)));
	}
	
	private static ResourceKey<ClimateGroup> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.CLIMATE_GROUP, path);
	}
}
