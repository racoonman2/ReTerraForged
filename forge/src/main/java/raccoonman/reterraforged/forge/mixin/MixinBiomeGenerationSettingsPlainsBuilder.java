package raccoonman.reterraforged.forge.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@Mixin(BiomeGenerationSettings.PlainBuilder.class)
public interface MixinBiomeGenerationSettingsPlainsBuilder {
	@Accessor
	List<List<Holder<PlacedFeature>>> getFeatures();
}
