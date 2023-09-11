package raccoonman.reterraforged.client.gui;

import java.util.function.Supplier;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.gui.widget.Label;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.Slider.Format;

public final class UnsizedWidgets {
	
	public static EditBox createEditBox(Font font) {
		return new EditBox(font, -1, -1, -1, -1, CommonComponents.EMPTY);
	}
	
	public static Label createLabel(String text) {
		return new Label(-1, -1, -1, -1, Component.translatable(text));
	}

	private static Slider createSlider(float initial, float min, float max, String text, Slider.Format format) {
		Slider slider = new Slider(-1, -1, -1, -1, initial, min, max, Component.translatable(text), format);
		slider.setTooltip(Tooltips.create(Tooltips.translationKey(text)));
		return slider;
	}

	public static Slider createFloatSlider(float initial, float min, float max, String text) {
		return createSlider(initial, min, max, text, Format.FLOAT);
	}
	
	public static Slider createIntSlider(int initial, int min, int max, String text) {
		return createSlider(initial, min, max, text, Format.INT);
	}
	
	public static <T extends Enum<T>> CycleButton<T> createCycle(Supplier<T[]> values, T initial, String text) {
		CycleButton<T> button = CycleButton.<T>builder((e) -> {
			return Component.literal(e.name());
		}).withValues(values.get()).withInitialValue(initial).create(-1, -1, -1, -1, Component.translatable(text));
		button.setTooltip(Tooltips.create(Tooltips.translationKey(text)));
		return button;
	}
	
	public static Button createReportingButton(String text, Toasts.ThrowingRunnable run) {
		return Button.builder(Component.translatable(text), (b) -> {
			Toasts.tryOrToast(Tooltips.failTranslationKey(text), run);
		}).build();
	}
}
