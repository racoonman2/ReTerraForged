package raccoonman.reterraforged.registries;

import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.BuiltInRegistries;
import raccoonman.reterraforged.server.commands.TerrainArgument;

public class RTFArgumentTypeInfos {

	public static void bootstrap() {
        ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, "reterraforged:terrain", TerrainArgument.class, new TerrainArgument.Info());
	}
}
