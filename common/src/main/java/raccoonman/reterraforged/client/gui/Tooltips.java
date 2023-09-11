package raccoonman.reterraforged.client.gui;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public final class Tooltips {
	
	public static String translationKey(String key) {
		return key + ".tooltip";
	}
	
	public static String failTranslationKey(String key) {
		return translationKey(key) + ".fail";
	}
	
	public static Tooltip create(String key) {
		return Tooltip.create(Component.translatable(key));
	}
}
