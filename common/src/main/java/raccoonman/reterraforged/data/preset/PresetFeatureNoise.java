package raccoonman.reterraforged.data.preset;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class PresetFeatureNoise {
	public static final ResourceKey<Noise> MEADOW_TREES = createKey("meadow_trees");
	
	public static void bootstrap(Preset preset, BootstapContext<Noise> ctx) {
		ctx.register(MEADOW_TREES, createMeadowTrees());
	}
	
	private static Noise createMeadowTrees() {
		return Noises.simplex(0, 75, 2);
	}
	
	public static ResourceKey<Noise> createKey(String name) {
        return PresetNoiseData.createKey("features/" + name);
	}
}
