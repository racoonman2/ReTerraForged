package raccoonman.reterraforged.preset.option;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TextOption<A> extends Option<A> {
	private TextOption.Formatter<A> formatter;
	
	public TextOption(String name, Component displayName, A defaultValue, List<Condition> conditions, List<Component> tooltipModifiers, Codec<A> codec, TextOption.Formatter<A> formatter) {
		super(name, displayName, defaultValue, conditions, tooltipModifiers, codec);
		
		this.formatter = formatter;
	}
	
	public TextOption.Formatter<A> getFormatter() {
		return this.formatter;
	}
	
	public static <A> TextOption.Builder<A> builder(String name, A defaultValue, TextOption.Formatter<A> formatter, Codec<A> valueCodec) {
		return new TextOption.Builder<>(name, defaultValue, formatter, valueCodec);
	}
	
	public static TextOption.Builder<String> stringBuilder(String name, String defaultValue) {
		return new TextOption.Builder<>(name, defaultValue, StringFormatter.INSTANCE, Codec.STRING);
	}

	public static TextOption.Builder<Integer> intBuilder(String name, int defaultValue, int minInclusive, int maxInclusive) {
		IntFormatter formatter = new IntFormatter(minInclusive, maxInclusive);
		return new TextOption.Builder<>(name, defaultValue, formatter, Codec.INT);
	}
	
	public static TextOption.Builder<Float> floatBuilder(String name, float defaultValue, float minInclusive, float maxInclusive) {
		FloatFormatter formatter = new FloatFormatter(minInclusive, maxInclusive);
		return new TextOption.Builder<>(name, defaultValue, formatter, Codec.FLOAT);
	}
	
	public static TextOption.Builder<Double> doubleBuilder(String name, double defaultValue, double minInclusive, double maxInclusive) {
		DoubleFormatter formatter = new DoubleFormatter(minInclusive, maxInclusive);
		return new TextOption.Builder<>(name, defaultValue, formatter, Codec.DOUBLE);
	}
	
	public static <A> ListFormatter<A> list(Formatter<A> formatter, int minElements) {
		return new ListFormatter<>(formatter, minElements);
	}
	
	public static class Builder<A> extends Option.Builder<A> {
		private TextOption.Formatter<A> formatter;
		private Codec<A> valueCodec;
		
		protected Builder(String name, A defaultValue, TextOption.Formatter<A> formatter, Codec<A> valueCodec) {
			super(name, defaultValue);
			
			this.formatter = formatter;
			this.valueCodec = valueCodec;
		}
		
		@Override
		public Option<A> makeOption() {
			return new TextOption<>(this.name, this.displayName, this.defaultValue, this.conditions, this.tooltipModifiers, this.valueCodec, this.formatter);
		}
	}

	public interface Formatter<A> {
		String format(A value);
		
		DataResult<A> parse(String input);
	}
	
	public record StringFormatter() implements Formatter<String> {
		public static final StringFormatter INSTANCE = new StringFormatter();

		@Override
		public String format(String value) {
			return value;
		}

		@Override
		public DataResult<String> parse(String input) {
			return DataResult.success(input);
		}
	}

	public record IntFormatter(int minInclusive, int maxInclusive) implements Formatter<Integer> {

		@Override
		public String format(Integer value) {
			return value.toString();
		}

		@Override
		public DataResult<Integer> parse(String input) {
			try {
				int result = Integer.parseInt(input);
				if(result >= this.minInclusive && result <= this.maxInclusive) {
					return DataResult.success(result);
				}
				return DataResult.error(() -> "Value out of bounds; Value: " + result + ", Min: " + this.minInclusive + ", Max: " + this.maxInclusive);
			} catch(NumberFormatException e) {
				return DataResult.error(e::getMessage);
			}
		}
	}
	
	public record FloatFormatter(float minInclusive, float maxInclusive) implements Formatter<Float> {

		@Override
		public String format(Float value) {
			return value.toString();
		}

		@Override
		public DataResult<Float> parse(String input) {
			try {
				float result = Float.parseFloat(input);
				if(result >= this.minInclusive && result <= this.maxInclusive) {
					return DataResult.success(result);
				}
				return DataResult.error(() -> "Value out of bounds; Value: " + result + ", Min: " + this.minInclusive + ", Max: " + this.maxInclusive);
			} catch(NumberFormatException e) {
				return DataResult.error(e::getMessage);
			}
		}
	}
	
	public record DoubleFormatter(double minInclusive, double maxInclusive) implements Formatter<Double> {

		@Override
		public String format(Double value) {
			return value.toString();
		}

		@Override
		public DataResult<Double> parse(String input) {
			try {
				double result = Double.parseDouble(input);
				if(result >= this.minInclusive && result <= this.maxInclusive) {
					return DataResult.success(result);
				}
				return DataResult.error(() -> "Value out of bounds; Value: " + result + ", Min: " + this.minInclusive + ", Max: " + this.maxInclusive);
			} catch(NumberFormatException e) {
				return DataResult.error(e::getMessage);
			}
		}
	}
	
	public record ListFormatter<A>(TextOption.Formatter<A> valueFormatter, int minElements) implements TextOption.Formatter<List<A>> {

		@Override
		public String format(List<A> value) {
			StringBuilder builder = new StringBuilder();
			boolean appendComma = false;
			for(A a : value) {
				String formatted = this.valueFormatter.format(a);
				if(appendComma) {
					builder.append(", ");
				}
				builder.append(formatted);
				appendComma = true;
			}
			return builder.toString();
		}

		@Override
		public DataResult<List<A>> parse(String input) {
			String[] split = input.split(",");
			if(split.length < this.minElements) {
				return DataResult.error(() -> "Missing elements");
			}
			
			ImmutableList.Builder<A> elements = ImmutableList.builder();
			for(String value : split) {
				DataResult<A> parsedValue = this.valueFormatter.parse(value.strip());
				if(parsedValue.isError()) {
					return DataResult.error(parsedValue.error().get().messageSupplier());
				}
				elements.add(parsedValue.getOrThrow());
			}
			return DataResult.success(elements.build());
		}
	}
	
	public record ResourceLocationFormatter() implements Formatter<ResourceLocation> {
		public static final ResourceLocationFormatter INSTANCE = new ResourceLocationFormatter();

		@Override
		public String format(ResourceLocation value) {
			return value.toString();
		}

		@Override
		public DataResult<ResourceLocation> parse(String input) {
			return ResourceLocation.read(input);
		}
	}
}
