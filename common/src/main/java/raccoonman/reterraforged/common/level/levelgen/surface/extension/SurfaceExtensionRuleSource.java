package raccoonman.reterraforged.common.level.levelgen.surface.extension;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;

public record SurfaceExtensionRuleSource(List<SurfaceExtensionSource> surfaceExtensions) implements RuleSource {
	public static final Codec<SurfaceExtensionRuleSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		SurfaceExtensionSource.CODEC.listOf().fieldOf("extensions").forGetter(SurfaceExtensionRuleSource::surfaceExtensions)
	).apply(instance, SurfaceExtensionRuleSource::new));
	
	public SurfaceExtensionRuleSource {
		surfaceExtensions = ImmutableList.copyOf(surfaceExtensions);
	}
	
	@Override
	public SurfaceRule apply(Context ctx) {
		if((Object) ctx instanceof ContextExtension ext) {
			for(SurfaceExtensionSource source : this.surfaceExtensions) {
				ext.addSurfaceExtension(source.apply(ctx));
			}
			return (x, y, z) -> null;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<SurfaceExtensionRuleSource> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}
