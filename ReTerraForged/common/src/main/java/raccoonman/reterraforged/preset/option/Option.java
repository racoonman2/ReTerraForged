package raccoonman.reterraforged.preset.option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.data.LanguageProvider;

public abstract class Option<A> {
	private String name;
	private Component displayName;
	private A defaultValue;
	private List<Condition> conditions;
	private List<Component> tooltipModifiers;
	private Codec<A> codec;
	
	public Option(String name, Component displayName, A defaultValue, List<Condition> conditions, List<Component> tooltipModifiers, Codec<A> codec) {
		this.name = name;
		this.displayName = displayName;
		this.defaultValue = defaultValue;
		this.conditions = conditions;
		this.tooltipModifiers = tooltipModifiers;
		this.codec = codec;
	}

	public String name() {
		return this.name;
	}
	
	public Component displayName() {
		return this.displayName;
	}
	
	public A defaultValue() {
		return this.defaultValue;
	}
	
	public List<Condition> conditions() {
		return this.conditions;
	}
	
	public List<Component> tooltipModifiers() {
		return this.tooltipModifiers;
	}

	public Codec<A> codec() {
		return this.codec;
	}
	
	public static abstract class Builder<A> {
		protected String name;
		protected A defaultValue;
		@Nullable
		protected Component displayName;
		protected List<Condition> conditions;
		protected List<Component> tooltipModifiers;
		
		protected Builder(String name, A defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
			this.conditions = new ArrayList<>();
			this.tooltipModifiers = new ArrayList<>();
		}
		
		public abstract Option<A> makeOption();
		
		@SuppressWarnings("unchecked")
		public <E> Builder<A> condition(Option<E> option, boolean inclusive, E... values) {
			return this.condition(option, inclusive, ImmutableSet.copyOf(values));
		}
		
		public <E> Builder<A> condition(Option<E> option, boolean inclusive, Collection<E> values) {
			return this.condition((preset) -> {
				E value = preset.getOption(option);
				boolean hasValue = values.contains(value);
				return inclusive ? hasValue : !hasValue;
			});
		}
		
		public Builder<A> condition(Option<Boolean> option) {
			return this.condition((preset) -> preset.getOption(option));
		}
		
		public Builder<A> condition(Condition condition) {
			this.conditions.add(condition);
			return this;
		}
		
		public Builder<A> displayName(String translationKey) {
			return this.displayName(Component.translatable(translationKey));
		}
		
		public Builder<A> displayName(Component displayName) {
			this.displayName = displayName;
			return this;
		}
		
		public Builder<A> tooltipModifier(Component... modifiers) {
			Collections.addAll(this.tooltipModifiers, modifiers);
			return this;
		}
		
		public Option<A> add(Category category) {
			// TODO do this somewhere else
			if(this.displayName == null) {
				this.displayName(Component.translatable(LanguageProvider.translationKey(category.displayName(), this.name)));
			}

			Option<A> option = this.makeOption();
			category.addOption(option);
			return option;
		}
		
		public Option<A> build() {
			if(this.displayName == null) {
				this.displayName(this.name);
			}

			return this.makeOption();
		}
	}
}
