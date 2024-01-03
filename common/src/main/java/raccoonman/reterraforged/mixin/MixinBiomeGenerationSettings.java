package raccoonman.reterraforged.mixin;

import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

//TODO do this with access wideners instead
@Deprecated
@Mixin(BiomeGenerationSettings.class)
public interface MixinBiomeGenerationSettings {
	@Accessor
	List<HolderSet<PlacedFeature>> getFeatures();
	
	@Accessor
	void setFlowerFeatures(Supplier<List<ConfiguredFeature<?, ?>>> flowerFeatures);
}
