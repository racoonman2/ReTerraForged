package raccoonman.reterraforged.world.worldgen.surface.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;

record NoiseRule(Holder<Noise> noise, List<Pair<Float, SurfaceRules.RuleSource>> rules) implements SurfaceRules.RuleSource {
	public static final Codec<NoiseRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.CODEC.fieldOf("noise").forGetter(NoiseRule::noise),
		entryCodec().listOf().fieldOf("rules").forGetter(NoiseRule::rules)
	).apply(instance, NoiseRule::new));
	
	@Override
	public Rule apply(Context ctx) {
		return new Rule(this.noise.value(), this.rules.stream().map((pair) -> {
			return Pair.of(pair.getFirst(), pair.getSecond().apply(ctx));
		}).toList());
	}

	@Override
	public KeyDispatchDataCodec<NoiseRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	private static Codec<Pair<Float, SurfaceRules.RuleSource>> entryCodec() {
		return RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("threshold").forGetter(Pair::getFirst),
			SurfaceRules.RuleSource.CODEC.fieldOf("rule").forGetter(Pair::getSecond)
		).apply(instance, Pair::new));
	}
	
	private static class Rule implements SurfaceRules.SurfaceRule {
		private Noise noise;
		private List<Pair<Float, SurfaceRules.SurfaceRule>> rules;
		private long lastPos;
		private SurfaceRules.SurfaceRule rule;
		
		public Rule(Noise noise, List<Pair<Float, SurfaceRules.SurfaceRule>> rules) {
			this.noise = noise;
			this.rules = new ArrayList<>(rules);
			this.lastPos = Long.MIN_VALUE;
			
			Collections.sort(this.rules, (p1, p2) -> p2.getFirst().compareTo(p1.getFirst()));
		}

		@Override
		public BlockState tryApply(int x, int y, int z) {
			long pos = PosUtil.pack(x, z);
			if(this.lastPos != pos) {
				float noise = this.noise.compute(x, z, 0);
				SurfaceRules.SurfaceRule newRule = null;
				for(Pair<Float, SurfaceRules.SurfaceRule> entry : this.rules) {
					if(noise > entry.getFirst()) {
						newRule = entry.getSecond();
						break;
					}
				}
				this.lastPos = pos;
				this.rule = newRule;
			}
			return this.rule != null ? this.rule.tryApply(x, y, z) : null;
		}
	}
}
