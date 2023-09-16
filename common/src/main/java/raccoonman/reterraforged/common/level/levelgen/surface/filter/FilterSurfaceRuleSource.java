package raccoonman.reterraforged.common.level.levelgen.surface.filter;

import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.registries.RTFBuiltInRegistries;

public record FilterSurfaceRuleSource(List<FilterSource> filters) implements SurfaceRules.RuleSource {
	public static final Codec<FilterSurfaceRuleSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		FilterSource.CODEC.listOf().fieldOf("filters").forGetter(FilterSurfaceRuleSource::filters)
	).apply(instance, FilterSurfaceRuleSource::new));
	
	private static final SurfaceRule RULE = (x, y, z) -> null;
	
	@Override
	public SurfaceRule apply(Context ctx) {
		if((Object) ctx instanceof ContextExtension extension) {
			for(FilterSource source : this.filters) {
				Filter filter = source.apply(ctx);
				extension.filters().add(filter);
			}
			return RULE;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<FilterSurfaceRuleSource> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	// vanilla basically does this with erodedBadlandsExtension & frozenOceanExtension so there's precedent for this
	public interface FilterSource {
		public static final Codec<FilterSource> CODEC = RTFBuiltInRegistries.SURFACE_FILTER_TYPE.byNameCodec().dispatch(FilterSource::codec, Function.identity());
		
		Filter apply(Context ctx);
		
		Codec<? extends FilterSource> codec();
	}
	
	public interface Filter {
		void apply(int worldX, int worldZ, int chunkLocalX, int chunkLocalZ, BlockColumn column);
	}
}
