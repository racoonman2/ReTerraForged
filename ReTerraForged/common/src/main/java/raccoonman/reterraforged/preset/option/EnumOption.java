package raccoonman.reterraforged.preset.option;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public class EnumOption<T> extends Option<T> {
	private List<T> values;
	private Function<T, Component> nameGetter;
	
	public EnumOption(String name, Component displayName, T defaultValue, List<Condition> conditions, List<Component> tooltipModifiers, List<T> values, Codec<T> valueCodec, Function<T, Component> nameGetter) {
		super(name, displayName, defaultValue, conditions, tooltipModifiers, valueCodec);
		
		this.values = values;
		this.nameGetter = nameGetter;
	}
	
	public List<T> values() {
		return this.values;
	}
	
	public Function<T, Component> nameGetter() {
		return this.nameGetter;
	}

	public static <T> EnumOption.Builder<T> builder(String name, T defaultValue, List<T> values, Codec<T> codec, Function<T, Component> nameGetter) {
		return new EnumOption.Builder<>(name, defaultValue, values, codec, nameGetter);
	}

	public static <T extends Enum<T> & StringRepresentable> EnumOption.Builder<T> builder(String name, T defaultValue, Supplier<T[]> defaultValues) {
		return builder(name, defaultValue, ImmutableList.copyOf(defaultValues.get()), StringRepresentable.fromEnum(defaultValues), (t) -> Component.literal(t.name()));
	}
	
	public static class Builder<T> extends Option.Builder<T> {
		private List<T> values;
		private Codec<T> valueCodec;
		private Function<T, Component> nameGetter;
		
		protected Builder(String name, T defaultValue, List<T> values, Codec<T> valueCodec, Function<T, Component> nameGetter) {
			super(name, defaultValue);
			
			this.values = values;
			this.valueCodec = valueCodec;
			this.nameGetter = nameGetter;
		}

		@Override
		public EnumOption<T> makeOption() {
			return new EnumOption<>(this.name, this.displayName, this.defaultValue, this.conditions, this.tooltipModifiers, this.values, this.valueCodec, this.nameGetter);
		}
	}
}