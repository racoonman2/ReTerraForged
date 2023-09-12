package raccoonman.reterraforged.common.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.data.preset.Preset;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public final class RTFNoiseData {
	
	public static void bootstrap(BootstapContext<Noise> ctx, Preset preset) {
		ctx.register(createKey("test"), Source.constant(1));
	}
	
	private static Noise getNoise(HolderGetter<Noise> getter, ResourceKey<Noise> key) {
		return new HolderNoise(getter.getOrThrow(key));
	}
	
	private static Noise register(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise value) {
		return new HolderNoise(ctx.register(key, value));
	}
	
	private static ResourceKey<Noise> createKey(String string) {
        return ResourceKey.create(RTFRegistries.NOISE, ReTerraForged.resolve(string));
    }
}
