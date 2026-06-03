package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import raccoonman.reterraforged.mixin.accessors.PlacedFeatureAccessor;

public record AddFeaturePlacementModifier(Holder<PlacedFeature> target, List<PlacementModifier> additions, InsertionOrder insertionOrder) implements Modifier {
	public static final MapCodec<AddFeaturePlacementModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PlacedFeature.CODEC.fieldOf("feature").forGetter(AddFeaturePlacementModifier::target),
		PlacementModifier.CODEC.listOf().fieldOf("additions").forGetter(AddFeaturePlacementModifier::additions),
		InsertionOrder.CODEC.fieldOf("insertion_order").forGetter(AddFeaturePlacementModifier::insertionOrder)
	).apply(instance, AddFeaturePlacementModifier::new));

	@Override
	public void applyModifier() {
	}

	@Override
	public void applyModifier(RegistryAccess registryAccess) {
		PlacedFeature feature = this.target.value();
		List<PlacementModifier> modifiers = this.insertionOrder.addAll(feature.placement(), this.additions);
		((PlacedFeatureAccessor) (Object) feature).setPlacement(modifiers);
	}
	
	@Override
	public MapCodec<AddFeaturePlacementModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
