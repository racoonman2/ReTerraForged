package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.List;
import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public record AppendSurfaceRuleModifier(Set<ResourceKey<LevelStem>> levels, RuleSource surfaceRule) implements Modifier {
	public static final MapCodec<AppendSurfaceRuleModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceKey.codec(Registries.LEVEL_STEM).listOf().xmap(Set::copyOf, List::copyOf).fieldOf("levels").forGetter(AppendSurfaceRuleModifier::levels),
		SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(AppendSurfaceRuleModifier::surfaceRule)
	).apply(instance, AppendSurfaceRuleModifier::new));

	@Override
	public void applyModifier(RegistryAccess registryAccess) {
		//TODO
	}

	@Override
	public void applyModifier() {
		//NOOP
	}
	
	@Override
	public MapCodec<AppendSurfaceRuleModifier> codec() {
		return CODEC;
	}

	@Override
	public int priority() {
		return 0;
	}
}
