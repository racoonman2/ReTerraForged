package raccoonman.reterraforged.mixin.accessors;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@Mixin(BiomeGenerationSettings.class)
public interface BiomeGenerationSettingsAccessor {
	@Accessor("features")
	List<HolderSet<PlacedFeature>> getFeatures();

	@Mutable
	@Accessor("features")
	void setFeatures(List<HolderSet<PlacedFeature>> features);
	
	@Mutable
	@Accessor("featureSet")
	void setFeatureSet(Supplier<Set<PlacedFeature>> featureSet);
	
	@Mutable
	@Accessor("flowerFeatures")
	void setFlowerFeatures(Supplier<List<ConfiguredFeature<?, ?>>> flowerFeatures);
}
