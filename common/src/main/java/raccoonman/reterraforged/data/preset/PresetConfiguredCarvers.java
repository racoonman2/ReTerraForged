package raccoonman.reterraforged.data.preset;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantFloat;
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
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import raccoonman.reterraforged.data.preset.settings.CaveSettings;
import raccoonman.reterraforged.data.preset.settings.Preset;

public class PresetConfiguredCarvers {

	//TODO make lava level configurable
	public static void bootstrap(Preset preset, BootstapContext<ConfiguredWorldCarver<?>> ctx) {
		CaveSettings caveSettings = preset.caves();
        HolderGetter<Block> blocks = ctx.lookup(Registries.BLOCK);
        
//        ctx.register(Carvers.CAVE, WorldCarver.CAVE.configured(new CaveCarverConfiguration(caveSettings.caveCarverChance, modifiedCaveY(caveSettings), modifiedCaveYScale(caveSettings), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), modifiedCaveHorizontalRadiusMultiplier(caveSettings), modifiedCaveVerticalRadiusMultiplier(caveSettings), modifiedCaveFloorLevel(caveSettings))));
//        ctx.register(Carvers.CAVE_EXTRA_UNDERGROUND, WorldCarver.CAVE.configured(new CaveCarverConfiguration(modifiedDeepCaveChance(caveSettings), UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(47)), UniformFloat.of(0.1F, 0.9F), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.OAK_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), UniformFloat.of(0.7F, 1.4F), UniformFloat.of(0.8F, 1.3F), UniformFloat.of(-1.0F, -0.4F))));
//        ctx.register(Carvers.CANYON, WorldCarver.CANYON.configured(new CanyonCarverConfiguration(caveSettings.ravineCarverChance, modifiedRavineY(caveSettings), ConstantFloat.of(3.0F), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), modifiedRavineYScale(caveSettings), new CanyonCarverConfiguration.CanyonShapeConfiguration(UniformFloat.of(0.75F, 1.0F), TrapezoidFloat.of(0.0F, 6.0F, 2.0F), 3, UniformFloat.of(0.75F, 1.0F), 1.0F, 0.0F))));
	}
}
