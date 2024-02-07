package raccoonman.reterraforged.data.preset;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.data.preset.settings.SurfaceSettings;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.tags.RTFSurfaceLayerTags;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.surface.condition.RTFSurfaceConditions;
import raccoonman.reterraforged.world.worldgen.surface.rule.LayeredSurfaceRule;
import raccoonman.reterraforged.world.worldgen.surface.rule.RTFSurfaceRules;

public class PresetSurfaceLayerData {
	public static final ResourceKey<LayeredSurfaceRule.Layer> EROSION_MATERIAL = createKey("erosion_material");
	public static final ResourceKey<LayeredSurfaceRule.Layer> EROSION = createKey("erosion");
	public static final ResourceKey<LayeredSurfaceRule.Layer> DEFAULT = createKey("default");
	public static final ResourceKey<LayeredSurfaceRule.Layer> FOREST = createKey("forest");
	
	private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
	private static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
	private static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
	
	public static void bootstrap(Preset preset, BootstapContext<LayeredSurfaceRule.Layer> ctx) {
		SurfaceSettings surfaceSettings = preset.surface();
		SurfaceSettings.Erosion erosion = surfaceSettings.erosion();
		
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);

		ctx.register(EROSION_MATERIAL, makeErosionMaterial());
		ctx.register(EROSION, makeErosion(erosion, noise));
		ctx.register(DEFAULT, makeDefault());
		ctx.register(FOREST, makeForest(noise));
	}

	private static LayeredSurfaceRule.Layer makeErosionMaterial() {
		return LayeredSurfaceRule.layer(
			SurfaceRules.sequence(
				SurfaceRules.ifTrue(
					SurfaceRules.isBiome(Biomes.WOODED_BADLANDS, Biomes.ERODED_BADLANDS, Biomes.BADLANDS),
					SurfaceRules.bandlands()
				),
				STONE
			)
		);
	}
	
	private static LayeredSurfaceRule.Layer makeErosion(SurfaceSettings.Erosion erosion, HolderGetter<Noise> noise) {
		SurfaceRules.ConditionSource erodedRock = RTFSurfaceConditions.steepness(erosion.rockSteepness, noise.getOrThrow(PresetSurfaceNoise.STEEPNESS_VARIANCE));
		SurfaceRules.ConditionSource erodedRockVariance = RTFSurfaceConditions.height(noise.getOrThrow(PresetSurfaceNoise.ERODED_ROCK), noise.getOrThrow(PresetSurfaceNoise.HEIGHT_VARIANCE));
		SurfaceRules.RuleSource erodedMaterial = RTFSurfaceRules.layered(RTFSurfaceLayerTags.EROSION_MATERIAL);
		SurfaceRules.RuleSource erode = SurfaceRules.sequence(
			SurfaceRules.ifTrue(
				erodedRock, 
				erodedMaterial
			),	
			SurfaceRules.ifTrue(
				erodedRockVariance,
				erodedMaterial
			)
		);
		return LayeredSurfaceRule.layer(
//			SurfaceRuleData.overworld(),
			SurfaceRules.ifTrue(
				SurfaceRules.abovePreliminarySurface(),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(
						SurfaceRules.ON_FLOOR, 
						erode
					),
					SurfaceRules.ifTrue(
						SurfaceRules.UNDER_FLOOR,
						erode
					)
				)
			)
		);
	}

	private static LayeredSurfaceRule.Layer makeDefault() {
		return LayeredSurfaceRule.layer(
			SurfaceRuleData.overworld()
		);
	}
	
	private static LayeredSurfaceRule.Layer makeForest(HolderGetter<Noise> noise) {
		return LayeredSurfaceRule.layer(
			SurfaceRules.ifTrue(
				SurfaceRules.abovePreliminarySurface(),
				SurfaceRules.ifTrue(
					SurfaceRules.isBiome(Biomes.FOREST),
					SurfaceRules.ifTrue(
						SurfaceRules.ON_FLOOR, 
						RTFSurfaceRules.noise(
							noise.getOrThrow(PresetSurfaceNoise.FOREST), 
							List.of(
								Pair.of(0.65F, PODZOL),
								Pair.of(0.725F, DIRT)
							)
						)	
					)
				)
			)
		);
	}
	
    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static ResourceKey<LayeredSurfaceRule.Layer> createKey(String name) {
        return ResourceKey.create(RTFRegistries.SURFACE_LAYERS, RTFCommon.location(name));
	}
}
