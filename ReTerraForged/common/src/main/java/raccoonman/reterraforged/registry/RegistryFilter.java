package raccoonman.reterraforged.registry;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;

public record RegistryFilter<T, C extends Iterable<T>>(Type type, C targets) implements Predicate<T> {

	@Override
	public boolean test(T value) {
		return this.type.test(value, this.targets);
	}
	
	public static <T, C extends Iterable<T>> RegistryFilter<T, C> inclusive(C targets) {
		return new RegistryFilter<>(Type.INCLUSIVE, targets);
	}

	public static <T, C extends Iterable<T>> RegistryFilter<T, C> exclusive(C targets) {
		return new RegistryFilter<>(Type.EXCLUSIVE, targets);
	}
	
	public static <T, C extends Iterable<T>> RegistryFilter<T, C> always(Function<Collection<T>, C> maker) {
		return exclusive(maker.apply(ImmutableList.of()));
	}

	public static <T, C extends Iterable<T>> Codec<RegistryFilter<T, C>> codec(Codec<C> valuesCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
			Type.CODEC.fieldOf("type").forGetter(RegistryFilter::type),
			valuesCodec.fieldOf("targets").forGetter(RegistryFilter::targets)
		).apply(instance, RegistryFilter::new));
	}
	
	public enum Type implements StringRepresentable {
		INCLUSIVE("inclusive") {

			@Override
			public <T> boolean test(T value, Iterable<T> targets) {
				for(T t : targets) {
					if(value.equals(t)) {
						return true;
					}
				}
				return false;
			}
		},
		EXCLUSIVE("exclusive") {

			@Override
			public <T> boolean test(T value, Iterable<T> targets) {
				for(T t : targets) {
					if(value.equals(t)) {
						return false;
					}
				}
				return true;
			}
		};
	
		public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
		
		private String name;
		
		private Type(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
		
		public abstract <T> boolean test(T value, Iterable<T> targets);
	}
}
