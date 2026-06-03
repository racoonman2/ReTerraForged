package raccoonman.reterraforged.preset.option;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.preset.PresetFunction;

public class FloatOption extends NumberOption<Float> {
	
	public FloatOption(String name, Component displayName, Float defaultValue, List<Condition> conditions, List<Component> tooltipModifiers, PresetFunction<Float> min, PresetFunction<Float> max, Optional<PresetFunction<Float>> lowerBound, Optional<PresetFunction<Float>> upperBound, NumberFormatter<Float> formatter) {
		super(name, displayName, defaultValue, conditions, tooltipModifiers, Codec.FLOAT, min, max, lowerBound, upperBound, formatter);
	}
	
	public static FloatOption.Builder builder(String name, float defaultValue, PresetFunction<Float> min, PresetFunction<Float> max) {
		return new FloatOption.Builder(name, defaultValue, min, max);
	}

	public static FloatOption.Builder builder(String name, float defaultValue, float min, float max) {
		return builder(name, defaultValue, PresetFunction.constant(min), PresetFunction.constant(max));
	}

	public static class Builder extends NumberOption.Builder<Float> {

		protected Builder(String name, float defaultValue, PresetFunction<Float> min, PresetFunction<Float> max) {
			super(name, defaultValue, min, max, NumberFormatter.withTrailing(3));
		}

		@Override
		public FloatOption makeOption() {
			return new FloatOption(this.name, this.displayName, this.defaultValue, this.conditions, this.tooltipModifiers, this.min, this.max, this.lowerBound, this.upperBound, this.formatter);
		}
	}
}