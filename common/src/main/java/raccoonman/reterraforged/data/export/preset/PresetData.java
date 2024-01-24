package raccoonman.reterraforged.data.export.preset;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.data.preset.Preset;
import raccoonman.reterraforged.registries.RTFRegistries;

//TODO support different presets per dimension
public class PresetData {
	public static final ResourceKey<Preset> PRESET = RTFRegistries.createKey(RTFRegistries.PRESET, "preset");
	
	public static void bootstrap(Preset preset, BootstapContext<Preset> ctx) {
		ctx.register(PRESET, preset);
	}
}
