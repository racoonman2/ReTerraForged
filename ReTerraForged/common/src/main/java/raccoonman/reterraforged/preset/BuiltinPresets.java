package raccoonman.reterraforged.preset;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.data.RTFTranslationKeys;

public class BuiltinPresets {
	public static final List<Pair<Component, Preset>> REGISTRY = new ArrayList<>();
	
	public static void addBuiltin(String translationKey, Preset preset) {
		addBuiltin(Component.translatable(translationKey).withStyle(ChatFormatting.GRAY), preset);
	}
	
	public static void addBuiltin(Component title, Preset preset) {
		REGISTRY.add(Pair.of(title, preset));
	}
	
	private static Preset makePreset(String translationKey) {
		Component component = Component.translatable(translationKey);
		return Preset.make(component);
	}

	private static Preset makeDefault() {
		return makePreset(RTFTranslationKeys.DEFAULT_PRESET_DESCRIPTION);
	}
	
	static {
		addBuiltin(RTFTranslationKeys.DEFAULT_PRESET, makeDefault());
	}
}
