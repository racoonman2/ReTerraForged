package raccoonman.reterraforged.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.data.RTFTranslationKeys;

public class Toast {

	public static void addToast(String message, Component description, SystemToastId id) {
		Minecraft mc = Minecraft.getInstance();
		SystemToast.add(mc.getToastManager(), id, Component.translatable(message), description);
	}
	
	public static void tryOrToast(String errorMessage, Toast.Task task) {
		try {
			task.run();
		} catch(Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			Component messageComponent;
			if(message != null) {
				messageComponent = Component.literal(message);
			} else {
				messageComponent = Component.translatable(RTFTranslationKeys.MISSING_ERROR_MESSAGE);
			}
			
			addToast(errorMessage, messageComponent, SystemToastId.PACK_LOAD_FAILURE);
		}
	}
	
	public interface Task {
		void run() throws Exception;
	}
}
