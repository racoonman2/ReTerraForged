package raccoonman.reterraforged.preset.option;

import java.util.function.BiFunction;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.preset.Preset;

public interface NumberFormatter<T extends Number> {
	Component format(T t, Preset preset);
	
	public static <T extends Number> NumberFormatter<T> literal() {
		return (number, v) -> Component.literal(number.toString());
	}
	
	public static <T extends Number> NumberFormatter<T> format(String formatString) {
		return (number, v) -> Component.literal(String.format(formatString, number));
	}
	
	public static <T extends Number> NumberFormatter<T> withTrailing(int trailing) {
		return format("%." + trailing + "f");
	}
	
	public static <T extends Number> NumberFormatter<T> append(NumberFormatter<T> input, BiFunction<T, Preset, Component> text) {
		return (number, preset) -> {
			return input.format(number, preset).copy().append(text.apply(number, preset));
		};
	}
	
	public static NumberFormatter<Float> scaledFloat(NumberFormatter<Float> input, float scaler, Component unitName) {
		return (number, preset) -> {
			float value = number / scaler;
			return input.format(value, preset).copy().append(unitName);
		};
	}
	
	public static NumberFormatter<Double> scaledDouble(NumberFormatter<Double> input, double scaler, Component unitName) {
		return (number, preset) -> {
			double value = number / scaler;
			return input.format(value, preset).copy().append(unitName);
		};
	}
	
	public static NumberFormatter<Integer> ratio() {
		return (number, v) -> {
			return Component.literal("1:").append(Component.literal(number.toString()));
		};
	}
}
