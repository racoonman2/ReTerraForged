package raccoonman.reterraforged.commands.neoforge;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import raccoonman.reterraforged.commands.RTFCommands;

@EventBusSubscriber
public class RTFCommandsImpl {
	private static final List<RTFCommands.Callback> CALLBACKS = new ArrayList<>();
	
	public static void register(RTFCommands.Callback callback) {
		CALLBACKS.add(callback);
	}
	
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		CALLBACKS.forEach((callback) -> callback.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
	}
}