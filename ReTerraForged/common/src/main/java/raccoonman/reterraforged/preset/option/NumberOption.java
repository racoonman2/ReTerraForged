package raccoonman.reterraforged.preset.option;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.preset.PresetFunction;

public abstract class NumberOption<T extends Number> extends Option<T> {
	private PresetFunction<T> min;
	private PresetFunction<T> max;
	private Optional<PresetFunction<T>> lowerBound; 
	private Optional<PresetFunction<T>> upperBound;
	private NumberFormatter<T> formatter;
	
	public NumberOption(String name, Component displayName, T defaultValue, List<Condition> conditions, List<Component> tooltipModifiers, Codec<T> codec, PresetFunction<T> min, PresetFunction<T> max, Optional<PresetFunction<T>> lowerBound, Optional<PresetFunction<T>> upperBound, NumberFormatter<T> formatter) {
		super(name, displayName, defaultValue, conditions, tooltipModifiers, codec);
		
		this.min = min;
		this.max = max;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.formatter = formatter;
	}
	
	public PresetFunction<T> min() {
		return this.min;
	}
	
	public PresetFunction<T> max() {
		return this.max;
	}
	
	public Optional<PresetFunction<T>> lowerBound() {
		return this.lowerBound;
	}
	
	public Optional<PresetFunction<T>> upperBound() {
		return this.upperBound;
	}
	
	public NumberFormatter<T> formatter() {
		return this.formatter;
	}
	
	public static abstract class Builder<T extends Number> extends Option.Builder<T> {
		protected PresetFunction<T> min;
		protected PresetFunction<T> max;
		protected Optional<PresetFunction<T>> lowerBound;
		protected Optional<PresetFunction<T>> upperBound;
		protected NumberFormatter<T> formatter;
		
		protected Builder(String name, T defaultValue, PresetFunction<T> min, PresetFunction<T> max, NumberFormatter<T> formatter) {
			super(name, defaultValue);
			
			this.min = min;
			this.max = max;
			this.lowerBound = Optional.empty();
			this.upperBound = Optional.empty();
			this.formatter = formatter;
		}

		public Builder<T> lower(Supplier<Option<T>> lower) {
			return this.lower(PresetFunction.option(lower));
		}

		public Builder<T> lower(PresetFunction<T> lower) {
			this.lowerBound = Optional.of(lower);
			return this;
		}

		public Builder<T> upper(Supplier<Option<T>> upper) {
			return this.upper(PresetFunction.option(upper));
		}
		
		public Builder<T> upper(PresetFunction<T> upper) {
			this.upperBound = Optional.of(upper);
			return this;
		}
		
		public Builder<T> bound(Supplier<Option<T>> lower, Supplier<Option<T>> upper) {
			return this.bound(PresetFunction.option(lower), PresetFunction.option(upper));
		}
		
		public Builder<T> bound(PresetFunction<T> lower, PresetFunction<T> upper) {
			return this.lower(lower).upper(upper);
		}
		
		public Builder<T> formatter(NumberFormatter<T> formatter) {
			this.formatter = formatter;
			return this;
		}
	}
}
