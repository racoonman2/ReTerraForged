package raccoonman.reterraforged.preset.option;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;

import net.minecraft.network.chat.Component;

public class DynamicOption extends Option<Dynamic<?>> {
	private String page;
	private String category;
	
	public DynamicOption(String name, Dynamic<?> defaultValue, String page, String category) {
		super(name, Component.empty(), defaultValue, ImmutableList.of(), ImmutableList.of(), Codec.PASSTHROUGH);
		
		this.page = page;
		this.category = category;
	}
	
	public String page() {
		return this.page;
	}
	
	public String category() {
		return this.category;
	}
}
