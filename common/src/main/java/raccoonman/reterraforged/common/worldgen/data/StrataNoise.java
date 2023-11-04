package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;

public final class StrataNoise {
	public static final ResourceKey<Noise> STRATA_SELECTOR = createKey("selector");
	public static final ResourceKey<Noise> STRATA_DEPTH = createKey("depth");

	public static void bootstrap(BootstapContext<Noise> ctx, MiscellaneousSettings miscellaneousSettings, Seed seed) {
		Seed strataSeed = seed.offset(0);
		int strataScale = miscellaneousSettings.strataRegionSize;
		
		ctx.register(STRATA_SELECTOR, Source.cell(seed.next(), strataScale)
			.warp(seed.next(), strataScale / 4, 2, strataScale / 2D)
			.warp(seed.next(), 15, 2, 30));
		ctx.register(STRATA_DEPTH, Source.perlin(strataSeed.next(), strataScale, 3));
	}
	
	private static ResourceKey<Noise> createKey(String name) {
		return RTFNoiseData.createKey("strata/" + name);
	}
}
