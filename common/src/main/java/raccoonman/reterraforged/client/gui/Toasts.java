package raccoonman.reterraforged.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;

public final class Toasts {

	public static void notify(String message, Component description, SystemToastIds id) {
		Minecraft mc = Minecraft.getInstance();
		SystemToast.add(mc.getToasts(), id, Component.translatable(message), description);
	}
	
	public static void tryOrToast(String errorMessage, ThrowingRunnable r) {
		try {
			r.run();
		} catch(Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			Component messageComponent;
			if(message != null) {
				messageComponent = Component.literal(message);
			} else {
				messageComponent = Component.translatable(RTFTranslationKeys.TOASTS_NO_ERROR_MESSAGE);
			}
			
			notify(errorMessage, messageComponent, SystemToastIds.PACK_LOAD_FAILURE);
		}
	}
	
	public interface ThrowingRunnable {
		void run() throws Exception;
	}
}
