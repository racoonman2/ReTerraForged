package raccoonman.reterraforged.common.client.gui.screen.element;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public final class Buttons {

	public static <T extends Enum<T>> CycleButton.Builder<T> cycleEnum(T[] values) {
		return CycleButton.<T>builder((e) -> Component.literal(e.name())).withInitialValue(values[0]).withValues(values);
	}
}
