package raccoonman.reterraforged.preset.option;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import com.mojang.serialization.Codec;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.preset.PresetFunction;

public class IntOption extends NumberOption<Integer> {
	private OptionalInt multiple;
	
	public IntOption(String name, Component displayName, Integer defaultValue, List<Condition> conditions, List<Component> tooltipModifiers, PresetFunction<Integer> min, PresetFunction<Integer> max, Optional<PresetFunction<Integer>> lowerBound, Optional<PresetFunction<Integer>> upperBound, OptionalInt multiple, NumberFormatter<Integer> formatter) {
		super(name, displayName, defaultValue, conditions, tooltipModifiers, Codec.INT, min, max, lowerBound, upperBound, formatter);
		
		this.multiple = multiple;
	}
	
	public OptionalInt multiple() {
		return this.multiple;
	}
	
	public static IntOption.Builder builder(String name, int defaultValue, PresetFunction<Integer> min, PresetFunction<Integer> max) {
		return new IntOption.Builder(name, defaultValue, min, max);
	}

	public static IntOption.Builder builder(String name, int defaultValue, int min, int max) {
		return builder(name, defaultValue, PresetFunction.constant(min), PresetFunction.constant(max));
	}
	
	public static class Builder extends NumberOption.Builder<Integer> {
		private OptionalInt multiple;
		
		protected Builder(String name, int defaultValue, PresetFunction<Integer> min, PresetFunction<Integer> max) {
			super(name, defaultValue, min, max, NumberFormatter.literal());
			
			this.multiple = OptionalInt.empty();
		}

		public Builder multiple(int multiple) {
			this.multiple = OptionalInt.of(multiple);
			return this;
		}

		@Override
		public IntOption makeOption() {
			return new IntOption(this.name, this.displayName, this.defaultValue, this.conditions, this.tooltipModifiers, this.min, this.max, this.lowerBound, this.upperBound, this.multiple, this.formatter);
		}
	}
}