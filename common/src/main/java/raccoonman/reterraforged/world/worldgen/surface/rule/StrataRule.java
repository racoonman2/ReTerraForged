package raccoonman.reterraforged.world.worldgen.surface.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;

public record StrataRule(Layer layer) implements SurfaceRules.RuleSource {
	public static final Codec<StrataRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Layer.CODEC.fieldOf("layer").forGetter(StrataRule::layer)
	).apply(instance, StrataRule::new));

	@Override
	public Rule apply(Context ctx) {
		return new Rule(ctx);
	}

	@Override
	public KeyDispatchDataCodec<StrataRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	public enum Layer implements StringRepresentable {
		SURFACE("surface"),
		UNDERGROUND("underground");

		public static final Codec<Layer> CODEC = StringRepresentable.fromEnum(Layer::values);
		
		private String name;
		
		private Layer(String name) {
			this.name = name;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
	
	public class Rule implements SurfaceRules.SurfaceRule {
		private Context context;
		
		public Rule(Context context) {
			this.context = context;
		}

		@Override
		public BlockState tryApply(int x, int y, int z) {
			return null;
		}
	}
}
