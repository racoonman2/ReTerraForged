package raccoonman.reterraforged.world.worldgen.modifier;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum InsertionOrder implements StringRepresentable {
	PREPEND("prepend") {
		
		@Override
		public <T> List<T> add(List<T> list, T value) {
			ImmutableList.Builder<T> builder = ImmutableList.builder();
			builder.add(value);
			builder.addAll(list);
			return builder.build();
		}
	},
	APPEND("append") {
		
		@Override
		public <T> List<T> add(List<T> list, T value) {
			ImmutableList.Builder<T> builder = ImmutableList.builder();
			builder.addAll(list);
			builder.add(value);
			return builder.build();
		}
	};
	
	public static final Codec<InsertionOrder> CODEC = StringRepresentable.fromEnum(InsertionOrder::values);
	
	private String name;
	
	private InsertionOrder(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	public abstract <T> List<T> add(List<T> list, T value);
	
	public <T> List<T> addAll(List<T> list, Iterable<T> values) {
		for(T t : values) {
			list = this.add(list, t);
		}
		return list;
	}
}
