package raccoonman.reterraforged.preset.option;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;

public record Category(String name, Component displayName, List<Option<?>> options) {
	
	public Category(String name, Component displayName) {
		this(name, displayName, new ArrayList<>());
	}
	
	public <T> void addOption(Option<T> option) {
		this.options.add(option);
	}
}
