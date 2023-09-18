package raccoonman.reterraforged.common.level.levelgen.surface.extension;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.registries.RTFBuiltInRegistries;

public record ExtensionRuleSource(List<ExtensionSource> extensions) implements SurfaceRules.RuleSource {
	public static final Codec<ExtensionRuleSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtensionSource.CODEC.listOf().fieldOf("extensions").forGetter(ExtensionRuleSource::extensions)
	).apply(instance, ExtensionRuleSource::new));
	
	public ExtensionRuleSource {
		extensions = ImmutableList.copyOf(extensions);
	}
	
	@Override
	public SurfaceRule apply(Context context) {
		if((Object) context instanceof ContextExtension ctx) {
			for(ExtensionSource source : this.extensions) {
				ctx.extensions().add(source.apply(context));
			}
			return (x, y, z) -> null;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<ExtensionRuleSource> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	// vanilla does this with erodedBadlandsExtension & frozenOceanExtension so there's precedent for this
	public interface ExtensionSource {
		public static final Codec<ExtensionSource> CODEC = RTFBuiltInRegistries.SURFACE_EXTENSION_TYPE.byNameCodec().dispatch(ExtensionSource::codec, Function.identity());
		
		Extension apply(Context ctx);
		
		Codec<? extends ExtensionSource> codec();
	}
	
	public interface Extension {
		void apply(int worldX, int worldZ, int chunkLocalX, int chunkLocalZ, BlockColumn column);
	}
}
