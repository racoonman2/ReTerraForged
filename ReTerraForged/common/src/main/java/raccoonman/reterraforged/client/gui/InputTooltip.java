package raccoonman.reterraforged.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class InputTooltip {
	
	public static Component make(String actionKey, String... inputKeys) {
		Component actionComponent = makeActionComponent(actionKey);
		Component inputComponent = makeInputComponent(inputKeys);
		return inputComponent.copy().append(" ").append(actionComponent);
	}
	
	private static Component makeActionComponent(String actionKey) {
		return Component.translatable(actionKey).withStyle(ChatFormatting.GREEN);
	}

	private static Component makeInputComponent(String... inputKeys) {
		MutableComponent component = Component.literal("[");
		boolean prependPlus = false;
		for(String inputKey : inputKeys) {
			if(prependPlus) {
				component = component.append(" + ");
			}
			component = component.append(Component.translatable(inputKey));
			prependPlus |= true;
		}
		component = component.append("]");
		component = component.withStyle(ChatFormatting.GOLD);
		return component;
	}
}
