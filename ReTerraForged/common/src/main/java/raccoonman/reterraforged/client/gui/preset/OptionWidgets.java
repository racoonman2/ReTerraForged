package raccoonman.reterraforged.client.gui.preset;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.mutable.MutableObject;

import com.mojang.serialization.DataResult;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import raccoonman.reterraforged.client.gui.InputTooltip;
import raccoonman.reterraforged.client.gui.widget.EditBox2;
import raccoonman.reterraforged.client.gui.widget.Slider;
import raccoonman.reterraforged.client.gui.widget.Slider.DoubleSlider;
import raccoonman.reterraforged.client.gui.widget.Slider.FloatSlider;
import raccoonman.reterraforged.client.gui.widget.Slider.IntSlider;
import raccoonman.reterraforged.data.RTFTranslationKeys;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.PresetFunction;
import raccoonman.reterraforged.preset.option.BoolOption;
import raccoonman.reterraforged.preset.option.Condition;
import raccoonman.reterraforged.preset.option.DoubleOption;
import raccoonman.reterraforged.preset.option.EnumOption;
import raccoonman.reterraforged.preset.option.FloatOption;
import raccoonman.reterraforged.preset.option.IntOption;
import raccoonman.reterraforged.preset.option.NumberFormatter;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.option.TextOption;
import raccoonman.reterraforged.preset.option.TooltipModifier;

public class OptionWidgets {
	private static final int WHITE = -2039584;
	private static final int RED = 0xFFFF3F30;
	private static final int GRAY = -9408400;
	private static final int DARK_RED = -6933966;
	
	public static AbstractWidget makeWidget(Preset preset, int x, int y, int width, int height, Option<?> option, List<OptionWidgets.ChangeListener> optionListeners) {
		AbstractWidget widget = makeBaseWidget(preset, x, y, width, height, option, optionListeners);
		List<Condition> conditions = option.conditions();
		optionListeners.add(() -> {
			for(Condition condition : conditions) {
				if(!(widget.active = condition.test(preset))) {
					break;
				}
			}
		});
		return widget;
	}
	
	private static AbstractWidget makeBaseWidget(Preset preset, int x, int y, int width, int height, Option<?> option, List<OptionWidgets.ChangeListener> optionListeners) {
		if(option instanceof IntOption intOption) {
			return makeIntSlider(preset, x, y, width, height, intOption, optionListeners);
		}
		
		if(option instanceof FloatOption floatOption) {
			return makeFloatSlider(preset, x, y, width, height, floatOption, optionListeners);
		}
		
		if(option instanceof DoubleOption doubleOption) {
			return makeDoubleSlider(preset, x, y, width, height, doubleOption, optionListeners);
		}

		if(option instanceof EnumOption<?> enumOption) {
			return makeCycleButton(preset, x, y, width, height, enumOption, optionListeners);
		}
		
		if(option instanceof BoolOption booleanOption) {
			return makeToggleButton(preset, x, y, width, height, booleanOption, optionListeners);
		}
		
		if(option instanceof TextOption<?> textOption) {
			return makeTextInputBox(preset, x, y, width, height, textOption, optionListeners);
		}
		
		throw new IllegalArgumentException("Unrecognized option type: " + option);
	}

	private static Slider<Integer> makeIntSlider(Preset preset, int x, int y, int width, int height, IntOption option, List<OptionWidgets.ChangeListener> optionListeners) {
		int initialValue = preset.getOption(option);
		int min = option.min().apply(preset);
		int max = option.max().apply(preset);
		Slider<Integer> slider = IntSlider.make(x, y, width, height, option.displayName(), initialValue, min, max, 
			bound(preset, option.lowerBound(), min), 
			bound(preset, option.upperBound(), max), 
			(widget, value) -> {
				return updateOrResetOption(widget, preset, option, value, optionListeners, Slider::setValue);
			}, 
			option.multiple().orElse(1),
			makeFormatter(option.formatter(), preset)
		);
		setOptionTooltip(slider, option);
		return slider;
	}
	
	private static <T extends Number> Slider.Bound<T> bound(Preset preset, Optional<PresetFunction<T>> bound, T defaultValue) {
		return () -> bound.orElse(PresetFunction.constant(defaultValue)).apply(preset);
	}
	
	private static Slider<Float> makeFloatSlider(Preset preset, int x, int y, int width, int height, FloatOption option, List<OptionWidgets.ChangeListener> optionListeners) {
		float initialValue = preset.getOption(option);
		float min = option.min().apply(preset);
		float max = option.max().apply(preset);
		Slider<Float> slider = FloatSlider.make(x, y, width, height, option.displayName(), initialValue, min, max, 
			bound(preset, option.lowerBound(), min), 
			bound(preset, option.upperBound(), max), 
			(widget, value) -> {
				return updateOrResetOption(widget, preset, option, value, optionListeners, Slider::setValue);
			},
			makeFormatter(option.formatter(), preset)
		);
		setOptionTooltip(slider, option);
		return slider;
	}

	private static Slider<Double> makeDoubleSlider(Preset preset, int x, int y, int width, int height, DoubleOption option, List<OptionWidgets.ChangeListener> optionListeners) {
		double initialValue = preset.getOption(option);
		double min = option.min().apply(preset);
		double max = option.max().apply(preset);
		Slider<Double> slider = DoubleSlider.make(x, y, width, height, option.displayName(), initialValue, min, max, 
			bound(preset, option.lowerBound(), min), 
			bound(preset, option.upperBound(), max), 
			(widget, value) -> {
				return updateOrResetOption(widget, preset, option, value, optionListeners, Slider::setValue);
			},
			makeFormatter(option.formatter(), preset)
		);
		setOptionTooltip(slider, option);
		return slider;
	}
	
	private static <T> CycleButton<T> makeCycleButton(Preset preset, int x, int y, int width, int height, EnumOption<T> option, List<OptionWidgets.ChangeListener> optionListeners) {
		CycleButton<T> button = CycleButton.builder(option.nameGetter())
			.withInitialValue(preset.getOption(option))
			.withValues(option.values())
			.create(x, y, width, height, option.displayName(), (widget, value) -> {
				updateOrResetOption(widget, preset, option, value, optionListeners, CycleButton::setValue);
			});
		setOptionTooltip(button, option);
		return button;
	}
	
	private static CycleButton<Boolean> makeToggleButton(Preset preset, int x, int y, int width, int height, BoolOption option, List<OptionWidgets.ChangeListener> optionListeners) {
		CycleButton<Boolean> button = CycleButton.booleanBuilder(Component.translatable(RTFTranslationKeys.GUI_TRUE), Component.translatable(RTFTranslationKeys.GUI_FALSE))
			.withInitialValue(preset.getOption(option))
			.create(x, y, width, height, option.displayName(), (widget, value) -> {
				updateOrResetOption(widget, preset, option, value, optionListeners, CycleButton::setValue);
			});
		setOptionTooltip(button, option);
		return button;
	}
	
	private static <A> EditBox2 makeTextInputBox(Preset preset, int x, int y, int width, int height, TextOption<A> option, List<OptionWidgets.ChangeListener> optionListeners) {
		TextOption.Formatter<A> formatter = option.getFormatter();
		
		Minecraft mc = Minecraft.getInstance();
		Component title = option.displayName();
		EditBox2 editBox = new EditBox2(mc.font, x, y, width, height, title, (widget) -> {
			resetOption(widget, preset, option, optionListeners, (box, value) -> {
				box.setText(formatter.format(value), false);
			});
		});
		
		MutableObject<String> oldValueHolder = new MutableObject<>();
		String initialValue = formatter.format(preset.getOption(option));

		editBox.setResponder((input) -> {
			DataResult<A> value = formatter.parse(input);
			if(value.isError()) {
				editBox.setTextColor(RED);
				editBox.setTextColorUneditable(DARK_RED);
				return;
			}
			editBox.setTextColor(WHITE);
			editBox.setTextColorUneditable(GRAY);
			String oldValue = oldValueHolder.getValue();
			if(oldValue != null && !oldValue.equals(input)) {
				updateOption(preset, option, value.getOrThrow(), optionListeners);
			}
			oldValueHolder.setValue(input);
		});
		editBox.setText(initialValue, true);
		setOptionTooltip(editBox, option);
		return editBox;
	}
	
	private static <A extends AbstractWidget, B> boolean updateOrResetOption(A widget, Preset preset, Option<B> option, B value, List<OptionWidgets.ChangeListener> optionListeners, BiConsumer<A, B> setWidgetValue) {
		if(!resetOption(widget, preset, option, optionListeners, setWidgetValue)) {
			updateOption(preset, option, value, optionListeners);
			return false;
		}
		return true;
	}
	
	private static <A extends AbstractWidget, B> boolean resetOption(A widget, Preset preset, Option<B> option, List<OptionWidgets.ChangeListener> optionListeners, BiConsumer<A, B> setWidgetValue) {
		if(Minecraft.getInstance().hasControlDown()) {
			B defaultValue = preset.getDefaultValue(option);
			setWidgetValue.accept(widget, defaultValue);
			updateOption(preset, option, defaultValue, optionListeners);
			return true;
		} else {
			return false;
		}
	}

	private static <A> void updateOption(Preset preset, Option<A> option, A newValue, List<OptionWidgets.ChangeListener> optionListeners) {
		preset.setOption(option, newValue);
		optionListeners.forEach(OptionWidgets.ChangeListener::onChange);
	}
	
	private static void setOptionTooltip(AbstractWidget widget, Option<?> option) {
		MutableComponent component;
		if(option.displayName().getContents() instanceof TranslatableContents contents) {
			component = Component.translatable(RTFTranslationKeys.tooltipKey(contents.getKey()));
		} else {
			component = Component.translatable(RTFTranslationKeys.tooltipKey(option.name()));
		}
		component = TooltipModifier.apply(component, option.tooltipModifiers());
		component = component.append("\n\n");
		component = component.append(InputTooltip.make(RTFTranslationKeys.GUI_ACTION_RESET, RTFTranslationKeys.GUI_INPUT_LEFT_CONTROL, RTFTranslationKeys.GUI_INPUT_LEFT_CLICK));
		widget.setTooltip(Tooltip.create(component));
	}

	private static <A extends Number> Slider.Formatter<A> makeFormatter(NumberFormatter<A> numberFormatter, Preset preset) {
		return (a) -> numberFormatter.format(a, preset);
	}
	
	public interface ChangeListener {
		void onChange();
	}
}
