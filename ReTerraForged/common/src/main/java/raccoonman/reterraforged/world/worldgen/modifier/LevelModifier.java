package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.List;
import java.util.Set;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

interface LevelModifier extends Modifier {
	Set<ResourceKey<LevelStem>> levels();
	
	void applyModifier(LevelStem level, RegistryAccess registryAccess);
	
	@Override
	default void applyModifier(RegistryAccess registryAccess) {
		RegistryLookup<LevelStem> levels = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
		for(ResourceKey<LevelStem> level : this.levels()) {
			levels.get(level).ifPresent((holder) -> {
				this.applyModifier(holder.value(), registryAccess);
			});
		}
	}

	@Override
	default void applyModifier() {
		// NOOP
	}

    public static <P extends LevelModifier> Products.P1<RecordCodecBuilder.Mu<P>, Set<ResourceKey<LevelStem>>> addFields(RecordCodecBuilder.Instance<P> codec) {
        return codec.group(ResourceKey.codec(Registries.LEVEL_STEM).listOf().xmap(Set::copyOf, List::copyOf).fieldOf("levels").forGetter(LevelModifier::levels));
    }
}
