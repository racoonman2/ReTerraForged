package raccoonman.reterraforged.preset.option;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.network.chat.Component;

public class BoolOption extends Option<Boolean> {

	public BoolOption(String name, Component displayName, Boolean defaultValue, List<Condition> conditions, List<Component> tooltipModifiers) {
		super(name, displayName, defaultValue, conditions, tooltipModifiers, Codec.BOOL);
	}

	public static BoolOption.Builder builder(String name, boolean defaultValue) {
		return new BoolOption.Builder(name, defaultValue);
	}

	public static class Builder extends Option.Builder<Boolean> {
		
		protected Builder(String name, boolean defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public BoolOption makeOption() {
			return new BoolOption(this.name, this.displayName, this.defaultValue, this.conditions, this.tooltipModifiers);
		}
	}
}