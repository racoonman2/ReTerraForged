package raccoonman.reterraforged.common.client.gui.screen.element;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface Element {

	default Component getToolTipText() {
		MutableComponent base = Component.literal("");
		for (String s : getTooltip()) {
			base.append(Component.literal(s));
		}
		return base;
	}

	default List<String> getTooltip() {
		return Collections.emptyList();
	}

	static String getDisplayName(String name, CompoundTag value) {
		if (name.contains(":")) {
			return name;
		}
		String key = getDisplayKey(name, value);
		if (key.endsWith(".")) {
			return "";
		}
		return I18n.get(key);
	}

	static List<String> getToolTip(String name, CompoundTag value) {
		String key = getCommentKey(name, value);
		if (key.endsWith(".")) {
			return Collections.emptyList();
		}
		return Collections.singletonList(I18n.get(key));
	}

	static String getDisplayKey(String name, CompoundTag value) {
		return "display.reterraforged." + getKey(name, value);
	}

	static String getCommentKey(String name, CompoundTag value) {
		return "tooltip.reterraforged." + getKey(name, value);
	}

	static String getKey(String name, CompoundTag value) {
		return value.getCompound("#" + name).getString("key");
	}
}
