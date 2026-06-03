package raccoonman.reterraforged.preset;

import java.util.function.Function;
import java.util.function.Supplier;

import raccoonman.reterraforged.preset.option.Option;

public interface PresetFunction<T> extends Function<Preset, T> {
	
	public static <T> PresetFunction<T> option(Supplier<Option<T>> option) {
		return (preset) -> preset.getOption(option.get());
	}
		
	public static <T> PresetFunction<T> constant(T value) {
		return (v) -> value;
	}
}