package raccoonman.reterraforged.preset.option;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.preset.PresetFunction;

public class DoubleOption extends NumberOption<Double> {
	
	public DoubleOption(String name, Component displayName, double defaultValue, List<Condition> conditions, List<Component> tooltipModifiers, PresetFunction<Double> min, PresetFunction<Double> max, Optional<PresetFunction<Double>> lowerBound, Optional<PresetFunction<Double>> upperBound, NumberFormatter<Double> formatter) {
		super(name, displayName, defaultValue, conditions, tooltipModifiers, Codec.DOUBLE, min, max, lowerBound, upperBound, formatter);
	}
	
	public static DoubleOption.Builder builder(String name, double defaultValue, PresetFunction<Double> min, PresetFunction<Double> max) {
		return new DoubleOption.Builder(name, defaultValue, min, max);
	}

	public static DoubleOption.Builder builder(String name, double defaultValue, double min, double max) {
		return builder(name, defaultValue, PresetFunction.constant(min), PresetFunction.constant(max));
	}

	public static class Builder extends NumberOption.Builder<Double> {

		protected Builder(String name, double defaultValue, PresetFunction<Double> min, PresetFunction<Double> max) {
			super(name, defaultValue, min, max, NumberFormatter.withTrailing(3));
		}

		@Override
		public DoubleOption makeOption() {
			return new DoubleOption(this.name, this.displayName, this.defaultValue, this.conditions, this.tooltipModifiers, this.min, this.max, this.lowerBound, this.upperBound, this.formatter);
		}
	}
}