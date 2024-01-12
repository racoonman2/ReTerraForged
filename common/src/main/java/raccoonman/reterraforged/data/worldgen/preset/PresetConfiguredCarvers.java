package raccoonman.reterraforged.data.worldgen.preset;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import raccoonman.reterraforged.data.worldgen.preset.settings.CaveSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldPreset;
import raccoonman.reterraforged.world.worldgen.floatproviders.LegacyCanyonYScale;
import raccoonman.reterraforged.world.worldgen.heightproviders.LegacyCarverHeight;

public class PresetConfiguredCarvers {

	//TODO make lava level configurable
	public static void bootstrap(WorldPreset preset, BootstapContext<ConfiguredWorldCarver<?>> ctx) {
		CaveSettings caveSettings = preset.caves();
        HolderGetter<Block> blocks = ctx.lookup(Registries.BLOCK);
        
        ctx.register(Carvers.CAVE, WorldCarver.CAVE.configured(new CaveCarverConfiguration(caveSettings.caveCarverProbability, modifiedCaveY(caveSettings), modifiedCaveYScale(caveSettings), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), modifiedCaveHorizontalRadiusMultiplier(caveSettings), modifiedCaveVerticalRadiusMultiplier(caveSettings), modifiedCaveFloorLevel(caveSettings))));
        ctx.register(Carvers.CAVE_EXTRA_UNDERGROUND, WorldCarver.CAVE.configured(new CaveCarverConfiguration(modifiedDeepCaveProbability(caveSettings), UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(47)), UniformFloat.of(0.1F, 0.9F), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.OAK_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), UniformFloat.of(0.7F, 1.4F), UniformFloat.of(0.8F, 1.3F), UniformFloat.of(-1.0F, -0.4F))));
        ctx.register(Carvers.CANYON, WorldCarver.CANYON.configured(new CanyonCarverConfiguration(caveSettings.ravineCarverProbability, modifiedRavineY(caveSettings), ConstantFloat.of(3.0F), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), modifiedRavineYScale(caveSettings), new CanyonCarverConfiguration.CanyonShapeConfiguration(UniformFloat.of(0.75F, 1.0F), TrapezoidFloat.of(0.0F, 6.0F, 2.0F), 3, UniformFloat.of(0.75F, 1.0F), 1.0F, 0.0F))));
	}

	private static HeightProvider modifiedCaveY(CaveSettings caveSettings) {
        return caveSettings.legacyCarverDistribution ? LegacyCarverHeight.of(0, 8, 120) : UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180));
	}
	
	private static FloatProvider modifiedCaveYScale(CaveSettings caveSettings) {
		return caveSettings.legacyCarverDistribution ? ConstantFloat.of(0.5F) : UniformFloat.of(0.1F, 0.9F);
	}
	
	private static FloatProvider modifiedCaveFloorLevel(CaveSettings caveSettings) {
		return caveSettings.legacyCarverDistribution ? ConstantFloat.of(-0.7F) : UniformFloat.of(-1.0F, -0.4F);
	}
	
	private static FloatProvider modifiedCaveHorizontalRadiusMultiplier(CaveSettings caveSettings) {
		return caveSettings.legacyCarverDistribution ? ConstantFloat.of(1.0F) : UniformFloat.of(0.7F, 1.4F);
	}
	
	private static FloatProvider modifiedCaveVerticalRadiusMultiplier(CaveSettings caveSettings) {
		return caveSettings.legacyCarverDistribution ? ConstantFloat.of(1.0F) : UniformFloat.of(0.8F, 1.3F);
	}
	
	private static float modifiedDeepCaveProbability(CaveSettings caveSettings) {
		return caveSettings.legacyCarverDistribution ? 0.0F : caveSettings.deepCaveCarverProbability;
	}
	
	private static HeightProvider modifiedRavineY(CaveSettings caveSettings) {
        return caveSettings.legacyCarverDistribution ? LegacyCarverHeight.of(20, 8, 40) : UniformHeight.of(VerticalAnchor.absolute(10), VerticalAnchor.absolute(67));
	}
	
	private static FloatProvider modifiedRavineYScale(CaveSettings caveSettings) {
		return caveSettings.legacyCarverDistribution ? new LegacyCanyonYScale() : UniformFloat.of(-0.125F, 0.125F);
	}
}
