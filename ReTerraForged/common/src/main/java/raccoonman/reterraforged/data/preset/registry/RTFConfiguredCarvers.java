package raccoonman.reterraforged.data.preset.registry;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
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
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.pages.CaveOptions;
import raccoonman.reterraforged.preset.pages.WorldOptions;
import raccoonman.reterraforged.world.worldgen.carvers.RTFCarvers;
import raccoonman.reterraforged.world.worldgen.carvers.ScalableCaveCarver;

public class RTFConfiguredCarvers {

	//TODO make lava level configurable
	public static void bootstrap(Preset preset, BootstrapContext<ConfiguredWorldCarver<?>> ctx) {
        HolderGetter<Block> blocks = ctx.lookup(Registries.BLOCK);
    
        float caveRarity = preset.getOption(CaveOptions.CAVE_CARVER_RARITY);
        float deepCaveRarity = preset.getOption(CaveOptions.DEEP_CAVE_CARVER_RARITY);
        float canyonRarity = preset.getOption(CaveOptions.CANYON_CARVER_RARITY);
        
        float horizontalCaveScale = preset.getOption(CaveOptions.CAVE_CARVER_HORIZONTAL_SCALE);
        float verticalCaveScale = preset.getOption(CaveOptions.CAVE_CARVER_HORIZONTAL_SCALE);
        float horizontalDeepCaveScale = preset.getOption(CaveOptions.CAVE_CARVER_VERTICAL_SCALE);
        float verticalDeepCaveScale = preset.getOption(CaveOptions.CAVE_CARVER_HORIZONTAL_SCALE);
        float horizontalCanyonScale = preset.getOption(CaveOptions.CANYON_CARVER_HORIZONTAL_SCALE);
        float verticalCanyonScale = preset.getOption(CaveOptions.CANYON_CARVER_VERTICAL_SCALE);

        int minCaveY = preset.getOption(CaveOptions.CAVE_CARVER_MIN_Y);
        int maxCaveY = preset.getOption(CaveOptions.CAVE_CARVER_MAX_Y);
        int minDeepCaveY = preset.getOption(CaveOptions.DEEP_CAVE_CARVER_MIN_Y);
        int maxDeepCaveY = preset.getOption(CaveOptions.DEEP_CAVE_CARVER_MAX_Y);
        int minCanyonY = preset.getOption(CaveOptions.CANYON_CARVER_MIN_Y);
        int maxCanyonY = preset.getOption(CaveOptions.CANYON_CARVER_MAX_Y);
        
        VerticalAnchor lavaLevel = VerticalAnchor.absolute(preset.getOption(WorldOptions.MIN_Y) + 8);
        
        ctx.register(Carvers.CAVE, RTFCarvers.SCALABLE_CAVE_CARVER.configured(new ScalableCaveCarver.Config(horizontalCaveScale, verticalCaveScale, caveRarity, UniformHeight.of(VerticalAnchor.absolute(minCaveY), VerticalAnchor.absolute(maxCaveY)), UniformFloat.of(0.1F, 0.9F), lavaLevel, CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), UniformFloat.of(0.7F, 1.4F), UniformFloat.of(0.8F, 1.3F), UniformFloat.of(-1.0F, -0.4F))));
        ctx.register(Carvers.CAVE_EXTRA_UNDERGROUND, RTFCarvers.SCALABLE_CAVE_CARVER.configured(new ScalableCaveCarver.Config(horizontalDeepCaveScale, verticalDeepCaveScale, deepCaveRarity, UniformHeight.of(VerticalAnchor.absolute(minDeepCaveY), VerticalAnchor.absolute(maxDeepCaveY)), UniformFloat.of(0.1F, 0.9F), lavaLevel, CarverDebugSettings.of(false, Blocks.OAK_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), UniformFloat.of(0.7F, 1.4F), UniformFloat.of(0.8F, 1.3F), UniformFloat.of(-1.0F, -0.4F))));
        ctx.register(Carvers.CANYON, WorldCarver.CANYON.configured(new CanyonCarverConfiguration(canyonRarity, UniformHeight.of(VerticalAnchor.absolute(minCanyonY), VerticalAnchor.absolute(maxCanyonY)), ConstantFloat.of(3.0F), lavaLevel, CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()), blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), UniformFloat.of(-0.125F, 0.125F), new CanyonCarverConfiguration.CanyonShapeConfiguration(UniformFloat.of(0.75F, 1.0F), TrapezoidFloat.of(0.0F, 6.0F, 2.0F), 3, UniformFloat.of(0.75F * horizontalCanyonScale, 1.0F * horizontalCanyonScale), 1.0F * verticalCanyonScale, 0.0F))));
    }
}
