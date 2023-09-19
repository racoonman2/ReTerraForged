package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.Toasts;
import raccoonman.reterraforged.client.gui.Tooltips;
import raccoonman.reterraforged.client.gui.widget.Label;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.Slider.Format;
import raccoonman.reterraforged.client.gui.widget.ValueButton;

public final class PresetWidgets {
	
	public static EditBox createEditBox(Font font, Consumer<String> responder) {
		EditBox box = new EditBox(font, -1, -1, -1, -1, CommonComponents.EMPTY);
		box.setResponder(responder);
		return box;
	}
	
	public static Label createLabel(String text) {
		return createLabel(Component.translatable(text));
	}
	
	public static Label createLabel(Component text) {
		return new Label(-1, -1, -1, -1, text);
	}

	private static Slider createSlider(float initial, float min, float max, String text, Slider.Format format, Slider.Callback callback) {
		Slider slider = new Slider(-1, -1, -1, -1, initial, min, max, Component.translatable(text), format, callback);
		slider.setTooltip(Tooltips.create(Tooltips.translationKey(text)));
		return slider;
	}

	public static Slider createFloatSlider(float initial, float min, float max, String text, Slider.Callback callback) {
		return createSlider(initial, min, max, text, Format.FLOAT, callback);
	}
	
	public static Slider createIntSlider(int initial, int min, int max, String text, Slider.Callback callback) {
		return createSlider(initial, min, max, text, Format.INT, callback);
	}
	
	public static <T extends Enum<T>> CycleButton<T> createCycle(T[] values, T initial, String text, CycleButton.OnValueChange<T> callback) {
		return createCycle(ImmutableList.copyOf(values), initial, text, callback, T::name);
	}
	
	public static <T> CycleButton<T> createCycle(Collection<T> values, T initial, String text, CycleButton.OnValueChange<T> callback, Function<T, String> name) {
		CycleButton<T> button = CycleButton.<T>builder((e) -> {
			return Component.literal(name.apply(e));
		}).withInitialValue(initial).withValues(values).create(-1, -1, -1, -1, Component.translatable(text), callback);
		button.setTooltip(Tooltips.create(Tooltips.translationKey(text)));
		return button;
	}
	
	public static CycleButton<Boolean> createToggle(boolean initial, String text, CycleButton.OnValueChange<Boolean> callback) {
		CycleButton<Boolean> button = CycleButton.booleanBuilder(Component.translatable(RTFTranslationKeys.GUI_BUTTON_TRUE), Component.translatable(RTFTranslationKeys.GUI_BUTTON_FALSE)).withInitialValue(initial).create(-1, -1, -1, -1, Component.translatable(text), callback);
		button.setTooltip(Tooltips.create(Tooltips.translationKey(text)));
		return button;
	}
	
	public static Button createThrowingButton(String text, Toasts.ThrowingRunnable run) {
		return Button.builder(Component.translatable(text), (b) -> {
			Toasts.tryOrToast(Tooltips.failTranslationKey(text), run);
		}).build();
	}
	
	public static <T> ValueButton<T> createValueButton(String text, Button.OnPress onPress, T initial) {
		ValueButton<T> button = new ValueButton<>(-1, -1, -1, -1, Component.translatable(text), onPress, initial);
		button.setTooltip(Tooltips.create(Tooltips.translationKey(text)));
		return button;
	}
	
	@SuppressWarnings("unchecked")
	public static ValueButton<Integer> createRandomButton(String text, int initial, Consumer<Integer> onPress) {
		return createValueButton(text, (button) -> {
			if(button instanceof ValueButton valueButton) {
				valueButton.setValue(ThreadLocalRandom.current().nextInt());
				onPress.accept((Integer) valueButton.getValue());
			}
		}, initial);
	}
}
