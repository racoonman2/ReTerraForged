package raccoonman.reterraforged.world.worldgen.surface.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;

public record LayeredSurfaceRule(TagKey<Layer> layers) implements SurfaceRules.RuleSource {
	public static final Codec<LayeredSurfaceRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		TagKey.hashedCodec(RTFRegistries.SURFACE_LAYERS).fieldOf("layers").forGetter(LayeredSurfaceRule::layers)
	).apply(instance, LayeredSurfaceRule::new));
		
	@Override
	public SurfaceRules.SurfaceRule apply(Context ctx) {
		if((Object) ctx.randomState instanceof RTFRandomState rtfRandomState) {
			RegistryLookup<Layer> layerLookup = rtfRandomState.registryAccess().lookupOrThrow(RTFRegistries.SURFACE_LAYERS);
			return SurfaceRules.sequence(layerLookup.getOrThrow(this.layers).stream().map(Layer::unwrapRule).toArray(SurfaceRules.RuleSource[]::new)).apply(ctx);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<LayeredSurfaceRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	public static Layer layer(SurfaceRules.RuleSource rule) {
		return new Layer(rule);
	}
	
	public record Layer(SurfaceRules.RuleSource rule) {
		public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SurfaceRules.RuleSource.CODEC.fieldOf("rule").forGetter(Layer::rule)
		).apply(instance, Layer::new));
		
		protected static SurfaceRules.RuleSource unwrapRule(Holder<Layer> layer) {
			return layer.value().rule();
		}
	}
}
