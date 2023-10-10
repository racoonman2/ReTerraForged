package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.StructureSettings;

public final class MCStructureSets {

	public static void bootstrap(BootstapContext<StructureSet> ctx, Preset preset) {
		StructureSettings structures = preset.structures();
		
		//TODO do structure separation stuff here
	}
}
