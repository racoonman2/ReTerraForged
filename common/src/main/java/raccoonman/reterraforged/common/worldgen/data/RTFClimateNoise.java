package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.worldgen.data.preset.ClimateSettings;

public final class RTFClimateNoise {
	public static final ResourceKey<Noise> TEMPERATURE = createKey("temperature");
	public static final ResourceKey<Noise> MOISTURE = createKey("moisture");
	
	public static void bootstrap(BootstapContext<Noise> ctx, ClimateSettings climateSettings) {
		ctx.register(TEMPERATURE, Source.constant(0.5D));
		ctx.register(MOISTURE, Source.constant(-1.0D));
	}
	
	private static ResourceKey<Noise> createKey(String path) {
		return RTFNoise.createKey("climate/" + path);
	}
}
