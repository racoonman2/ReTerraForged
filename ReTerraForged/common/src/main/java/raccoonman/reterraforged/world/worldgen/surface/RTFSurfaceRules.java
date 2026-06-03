package raccoonman.reterraforged.world.worldgen.surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.platform.RegistryUtil;

public class RTFSurfaceRules {

	public static void bootstrap() {
		register("density", DensityRule.CODEC);
		register("layer", LayerRule.CODEC);
	}
	
	public static DensityRule density(Holder<DensityFunction> density, double threshold, SurfaceRules.RuleSource rule) {
		return density(density, ImmutableMap.of(threshold, rule));
	}

	public static DensityRule density(Holder<DensityFunction> density, Map<Double, SurfaceRules.RuleSource> rules) {
		return new DensityRule(density, inverseSort(rules));
	}
	
	public static LayerRule layer(Block full, Block layer, IntegerProperty layerProperty) {
		List<BlockState> layers = new ArrayList<>();
		for(int value : layerProperty.getPossibleValues()) {
			layers.add(layer.defaultBlockState().setValue(layerProperty, value));
		}
		layers.add(full.defaultBlockState());
		return new LayerRule(layers);
	}

	public static LayerRule layer(List<BlockState> layers) {
		return new LayerRule(layers);
	}
	
	private static <K extends Comparable<K>, V> List<Pair<K, V>> inverseSort(Map<K, V> map) {
		List<Pair<K, V>> list = map.entrySet().stream().map((entry) -> {
			return Pair.of(entry.getKey(), entry.getValue());
		}).collect(Collectors.toList());
		Collections.sort(list, (pair1, pair2) -> pair2.getFirst().compareTo(pair1.getFirst()));
		return list;
	}
	
	private static void register(String name, MapCodec<? extends SurfaceRules.RuleSource> value) {
		RegistryUtil.register(BuiltInRegistries.MATERIAL_RULE, name, value);
	}
}
