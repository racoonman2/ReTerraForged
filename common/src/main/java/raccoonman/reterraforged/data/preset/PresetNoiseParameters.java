package raccoonman.reterraforged.data.preset;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.data.preset.settings.Preset;

public class PresetNoiseParameters {

	public static void bootstrap(Preset preset, BootstapContext<NormalNoise.NoiseParameters> ctx) {
//		TODO
//		CaveSettings caveSettings = preset.caves();
//		CaveSettings.Pillar pillars = caveSettings.pillars;
//		
//		double pillarRarenessModifier = 1.0D;
//		double pillarThicknessModifier = 1.0D;
//		double caveLayerModifier = 1.0D;
//		double caveCheeseModifier = 1.0D;
//		
//        register(ctx, Noises.PILLAR, -7, 1.0 * pillars.multiplier, 1.0 * pillars.multiplier);
//        register(ctx, Noises.PILLAR_RARENESS, -8, 1.0 * pillarRarenessModifier);
//        register(ctx, Noises.PILLAR_THICKNESS, -8, 1.0 * pillarThicknessModifier);
//        register(ctx, Noises.CAVE_LAYER, -8, 1.0 * caveLayerModifier);
//        register(ctx, Noises.CAVE_CHEESE, -8, 
//        	0.0 * caveCheeseModifier, 
//        	0.0 * caveCheeseModifier, 
//        	2.0 * caveCheeseModifier, 
//        	1.0 * caveCheeseModifier, 
//        	2.0 * caveCheeseModifier, 
//        	1.0 * caveCheeseModifier, 
//        	0.0 * caveCheeseModifier, 
//        	2.0 * caveCheeseModifier, 
//        	0.0 * caveCheeseModifier
//        );
	}

    private static void register(BootstapContext<NormalNoise.NoiseParameters> bootstapContext, ResourceKey<NormalNoise.NoiseParameters> resourceKey, int firstOctave, double initialAmplitude, double ... amplitudes) {
        bootstapContext.register(resourceKey, new NormalNoise.NoiseParameters(firstOctave, initialAmplitude, amplitudes));
    }
}
