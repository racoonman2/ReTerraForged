package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

final class ErosionLevels {
	public static final ResourceKey<Noise> LEVEL_0 = createKey("level_0");
	public static final ResourceKey<Noise> LEVEL_1 = createKey("level_1");
	public static final ResourceKey<Noise> LEVEL_2 = createKey("level_2");
	public static final ResourceKey<Noise> LEVEL_3 = createKey("level_3");
	public static final ResourceKey<Noise> LEVEL_4 = createKey("level_4");
	public static final ResourceKey<Noise> LEVEL_5 = createKey("level_5");
	public static final ResourceKey<Noise> LEVEL_6 = createKey("level_6");
	
	public static void bootstrap(BootstapContext<Noise> ctx) {
		// these ranges are taken from OverworldBiomeBuilder.erosions
		
		ctx.register(LEVEL_0, RTFNoise.midpoint(-1.0F, -0.78F));
		ctx.register(LEVEL_1, RTFNoise.midpoint(-0.78F, -0.375F));
		ctx.register(LEVEL_2, RTFNoise.midpoint(-0.375F, -0.2225F));
		ctx.register(LEVEL_3, RTFNoise.midpoint(-0.2225F, 0.05F));
		ctx.register(LEVEL_4, RTFNoise.midpoint(0.05F, 0.45F));
		ctx.register(LEVEL_5, RTFNoise.midpoint(0.45F, 0.55F));
		ctx.register(LEVEL_6, RTFNoise.midpoint(0.55F, 1.0F));
	}
	
	private static ResourceKey<Noise> createKey(String name) {
		return RTFTerrainNoise.createKey("erosion_levels/" + name);
	}
}
