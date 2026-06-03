package raccoonman.reterraforged.preset.option;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.data.RTFTranslationKeys;

public record Page(String name, Component displayName, List<Category> categories) {
	
	public Page(String name, Component displayName) {
		this(name, displayName, new ArrayList<>());
	}

	public Category addCategory(String name) {
		return this.addCategory(name, Component.translatable(RTFTranslationKeys.prependModId(this.name) + "." + name));
	}
	
	public Category addCategory(String name, Component displayName) {
		Category category = new Category(name, displayName);
		this.addCategory(category);
		return category;
	}
	
	public void addCategory(Category category) {
		this.categories.add(category);
	}
	
	public static Page make(String name) {
		String prefixedName = RTFTranslationKeys.prependModId(name);
		return new Page(name, Component.translatable(prefixedName));
	};
}
