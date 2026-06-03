package raccoonman.reterraforged.world.worldgen.surface;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.densityfunction.MutableFunctionContext;

public record DensityRule(Holder<DensityFunction> function, List<Pair<Double, SurfaceRules.RuleSource>> rules) implements SurfaceRules.RuleSource {
	public static final MapCodec<DensityRule> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("function").forGetter(DensityRule::function),
		pairCodec("threshold", Codec.DOUBLE, "rule", SurfaceRules.RuleSource.CODEC).listOf().fieldOf("rules").forGetter(DensityRule::rules)
	).apply(instance, DensityRule::new));
			
	@Override
	public SurfaceRules.SurfaceRule apply(SurfaceRules.Context ctx) {
		DensityFunction function = this.mapFunction(ctx);
		return new Rule(ctx, function, this.rules.stream().map((pair) -> {
			return pair.mapSecond((ruleSource) -> ruleSource.apply(ctx));
		}).toList());
	}

	@Override
	public KeyDispatchDataCodec<DensityRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	private DensityFunction mapFunction(SurfaceRules.Context ctx) {
		RTFRandomState rtfRandomState = ExtensionUtil.cast(ctx.randomState);
		return rtfRandomState.globalFunctionVisitor().mapForChunk(this.function.value(), ctx.noiseChunk);
	}

	// TODO move this somewhere else
	// Codec.pair breaks when using primitives as the first value
	private static <F, S> Codec<Pair<F, S>> pairCodec(String firstKey, Codec<F> first, String secondKey, Codec<S> second) {
		return RecordCodecBuilder.create(instance -> instance.group(
			first.fieldOf(firstKey).forGetter(Pair::getFirst),
			second.fieldOf(secondKey).forGetter(Pair::getSecond)
		).apply(instance, Pair::of));
	}
	
	private class Rule implements SurfaceRules.SurfaceRule {
		private DensityFunction function;
		private List<Pair<Double, SurfaceRules.SurfaceRule>> rules;
		private MutableFunctionContext functionCtx;
		
		public Rule(SurfaceRules.Context ctx, DensityFunction function, List<Pair<Double, SurfaceRules.SurfaceRule>> rules) {
			this.function = function;
			this.rules = rules;
			this.functionCtx = new MutableFunctionContext();
		}

		@Override
		public BlockState tryApply(int x, int y, int z) {
			double density = this.function.compute(this.functionCtx.at(x, y, z));
			BlockState state = null;
			for(Pair<Double, SurfaceRules.SurfaceRule> entry : this.rules) {
				if(density > entry.getFirst() && (state = entry.getSecond().tryApply(x, y, z)) != null) {
					break;
				}
			}
			return state;
		}
	}
}
