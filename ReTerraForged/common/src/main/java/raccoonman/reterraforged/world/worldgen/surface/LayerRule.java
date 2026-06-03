package raccoonman.reterraforged.world.worldgen.surface;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFSurfaceContext;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public record LayerRule(List<BlockState> layers) implements SurfaceRules.RuleSource {
	public static final MapCodec<LayerRule> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockState.CODEC.listOf().fieldOf("layers").forGetter(LayerRule::layers)
	).apply(instance, LayerRule::new));
	
	@Override
	public LayerRule.Rule apply(SurfaceRules.Context surfaceContext) {
		RTFSurfaceContext rtfSurfaceContext = ExtensionUtil.cast(surfaceContext);
		return new LayerRule.Rule(rtfSurfaceContext);
	}

	@Override
	public KeyDispatchDataCodec<LayerRule> codec() {
		return KeyDispatchDataCodec.of(CODEC);
	}
	
	private class Rule implements SurfaceRule {
		private RTFSurfaceContext rtfSurfaceContext;
		
		public Rule(RTFSurfaceContext rtfSurfaceContext) {
			this.rtfSurfaceContext = rtfSurfaceContext;
		}
		
		@Override
		public BlockState tryApply(int x, int y, int z) {
			double terrainHeight = this.rtfSurfaceContext.getTerrainHeight();
			if(terrainHeight == Double.MIN_VALUE) {
				throw new IllegalStateException("Terrain height not set");
			}
			
			int layerCount = LayerRule.this.layers.size();
			if(layerCount == 1) {
				return LayerRule.this.layers.get(0);
			}

			double depth = terrainHeight - y;
			if(depth > 0.0D && depth < 1.0D) {
				int index = NoiseUtil.round(depth * (layerCount - 2));
				return LayerRule.this.layers.get(index);
			} else {
				return LayerRule.this.layers.get(layerCount - 1);
			}
		}
	}
}
