package raccoonman.reterraforged.common.level.levelgen.surface.rule;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;

public record ErosionRule(TagKey<Block> erodible, TagKey<Block> solid, BlockState material, Map<Block, BlockState> overrides) implements SurfaceRules.RuleSource {
	public static final Codec<ErosionRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		TagKey.hashedCodec(Registries.BLOCK).fieldOf("erodible").forGetter(ErosionRule::erodible),
		TagKey.hashedCodec(Registries.BLOCK).fieldOf("solid").forGetter(ErosionRule::solid),
		BlockState.CODEC.fieldOf("material").forGetter(ErosionRule::material),
		Codec.simpleMap(BuiltInRegistries.BLOCK.byNameCodec(), BlockState.CODEC, BuiltInRegistries.BLOCK).fieldOf("overrides").forGetter(ErosionRule::overrides)
	).apply(instance, ErosionRule::new));
	
	private static final SurfaceRules.SurfaceRule RULE = (x, y, z) -> {
		return null;
	};
	
	@Override
	public SurfaceRules.SurfaceRule apply(Context ctx) {
		if((Object) ctx instanceof ContextExtension extension) {
			extension.setErosionRule(ErosionRule.this);
		} else {
			throw new IllegalStateException();
		}
		return RULE;
	}

	@Override
	public KeyDispatchDataCodec<ErosionRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	public class Applied {
		
	}
}
