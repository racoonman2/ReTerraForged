package raccoonman.reterraforged.preset.option;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import raccoonman.reterraforged.data.RTFTranslationKeys;

public class TooltipModifier {
	public static final Component EXPERIMENTAL = modifier(RTFTranslationKeys.EXPERIMENTAL, ChatFormatting.GOLD);
	public static final Component MEDIUM_PERFORMANCE_IMPACT = modifier(RTFTranslationKeys.MEDIUM_PERFORMANCE_IMPACT, ChatFormatting.YELLOW);
	public static final Component HEAVY_PERFORMANCE_IMPACT = modifier(RTFTranslationKeys.HEAVY_PERFORMANCE_IMPACT, ChatFormatting.RED);

	public static MutableComponent apply(Component tooltip, List<Component> modifiers) {
		MutableComponent component = Component.empty();
		for(Component modifier : modifiers) {
			component = component.append(modifier).append("\n");
		}
		return component.append(tooltip);
	}
	
	public static Component modifier(String translationKey, ChatFormatting color) {
		return Component.literal("[")
			.append(Component.translatable(translationKey).withStyle(color))
			.append("]")
			.withStyle(color);
	}
}
