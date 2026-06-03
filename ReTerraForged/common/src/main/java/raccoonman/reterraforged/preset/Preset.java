package raccoonman.reterraforged.preset;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.preset.option.Option;

public class Preset {
	private Optional<Component> description;
	private Map<Option<?>, ?> options;
	
	public Preset(Optional<Component> description) {
		this.description = description;
		this.options = new HashMap<>();
	}
	
	public Optional<Component> getDescription() {
		return this.description;
	}
	
	public Map<Option<?>, ?> getOptions() {
		return this.options;
	}
	
	@SuppressWarnings("unchecked")
	public <B, A extends Option<?>> Stream<A> getOptionsOfType(Class<A> cls) {
		return this.options.keySet().stream().filter(cls::isInstance).map((option) -> (A) option);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> void setOption(Option<T> option, T value) {
		((Map) this.options).put(option, value);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T getOption(Option<T> option) {
		T t = (T) ((Map) this.options).get(option);
		if(t != null) {
			return t;
		}
		return this.getDefaultValue(option);
	}
	
	public <T> T getDefaultValue(Option<T> option) {
		return option.defaultValue();
	}
	
	public <T> boolean isModified(Option<T> option) {
		return !this.getOption(option).equals(this.getDefaultValue(option));
	}

	public <A> DataResult<Preset> updateOptionReferences(Encoder<Preset> encoder, Decoder<Preset> decoder, DynamicOps<A> ops) {
		return encoder.encodeStart(ops, this).flatMap((serialized) -> {
			return decoder.parse(ops, serialized);
		});
	}
	
	public static Preset make(Component description) {
		return new Preset(Optional.ofNullable(description));
	}
	
	public static Preset make() {
		return make(null);
	}
}
