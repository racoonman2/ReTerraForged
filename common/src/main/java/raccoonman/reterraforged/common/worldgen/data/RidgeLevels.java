package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

// note: you can get the weird variant of these by calling .negate()
final class RidgeLevels {
	public static final ResourceKey<Noise> VALLEYS = createKey("valleys");
	public static final ResourceKey<Noise> LOW_SLICE = createKey("low_slice");
	public static final ResourceKey<Noise> MID_SLICE = createKey("mid_slice");
	public static final ResourceKey<Noise> HIGH_SLICE = createKey("high_slice");
	public static final ResourceKey<Noise> PEAKS = createKey("peaks");
	
	public static void bootstrap(BootstapContext<Noise> ctx) {
		// these ranges are taken from OverworldBiomeBuilder.addInlandBiomes
		
		ctx.register(VALLEYS, RTFNoise.midpoint(-0.05F, 0.05F));
		ctx.register(LOW_SLICE, RTFNoise.midpoint(-0.26666668F, -0.05F));
		ctx.register(MID_SLICE, RTFNoise.midpoint(-0.4F, -0.26666668F));
		ctx.register(HIGH_SLICE, RTFNoise.midpoint(-0.56666666F, -0.4F));
		ctx.register(PEAKS, RTFNoise.midpoint(-0.7666667F, -0.56666666F));
	}

	private static ResourceKey<Noise> createKey(String name) {
		return RTFTerrainNoise.createKey("ridge_levels/" + name);
	}
}
