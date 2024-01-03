package raccoonman.reterraforged.world.worldgen.biome.modifier;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum Order implements StringRepresentable {
	PREPEND("prepend") {
		
		@Override
		public <T> List<T> add(List<T> list1, List<T> list2) {
			List<T> newList = new ArrayList<>(list1.size() + list2.size());
			newList.addAll(list2);
			newList.addAll(list1);
			return newList;
		}
	},
	APPEND("append") {
		
		@Override
		public <T> List<T> add(List<T> list1, List<T> list2) {
			List<T> newList = new ArrayList<>(list1.size() + list2.size());
			newList.addAll(list1);
			newList.addAll(list2);
			return newList;
		}
	};

	public static final Codec<Order> CODEC = StringRepresentable.fromEnum(Order::values);
	
	private String name;
	
	private Order(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	public abstract <T> List<T> add(List<T> list1, List<T> list2);
}
