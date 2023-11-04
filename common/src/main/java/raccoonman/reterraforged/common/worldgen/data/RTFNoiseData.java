package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public final class RTFNoiseData {
	
	public static void bootstrap(BootstapContext<Noise> ctx, Preset preset) {
		Seed seed = new Seed(0);
		
        StrataNoise.bootstrap(ctx, preset.miscellaneous(), seed);
	}

	protected static ResourceKey<Noise> createKey(String name) {
        return ResourceKey.create(RTFRegistries.NOISE, RTFRegistries.resolve(name));
	}
}
