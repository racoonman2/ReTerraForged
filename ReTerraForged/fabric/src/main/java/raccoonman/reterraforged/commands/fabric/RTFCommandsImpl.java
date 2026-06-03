package raccoonman.reterraforged.commands.fabric;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import raccoonman.reterraforged.commands.RTFCommands;

public class RTFCommandsImpl {

	public static void register(RTFCommands.Callback callback) {
	    CommandRegistrationCallback.EVENT.register(callback::register);
	}
}
