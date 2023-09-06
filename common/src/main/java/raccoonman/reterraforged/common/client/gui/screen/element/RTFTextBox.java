/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.client.gui.screen.element;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;

public class RTFTextBox extends EditBox implements Element, Consumer<String> {

	private final String name;
	private final CompoundTag value;
	private final List<String> tooltip;

	private String stringValue = "";
	private boolean valid = true;
	private Predicate<String> validator = s -> true;
	private Consumer<RTFTextBox> callback = t -> {
	};

	public RTFTextBox(String name, CompoundTag value) {
		super(Minecraft.getInstance().font, 0, 0, 100, 20, Component.literal(Element.getDisplayName(name, value) + ": "));
		this.name = name;
		this.value = value;
		this.tooltip = Element.getToolTip(name, value);
		this.stringValue = value.getString(name);
		setValue(value.getString(name));
		setResponder(this);
		setEditable(true);
	}

	public boolean isValid() {
		return valid;
	}

	public String getValue() {
		return stringValue;
	}

	public void setColorValidator(Predicate<String> validator) {
		this.validator = validator;

		// update validity immediately
		if (validator.test(stringValue)) {
			valid = true;
			setTextColor(14737632);
		} else {
			valid = false;
			setTextColor(0xffff3f30);
		}
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		if (!active) {
			setFocused(false);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			setFocused(true);
			setEditable(true);
			active = true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public List<String> getTooltip() {
		return tooltip;
	}

	@Override
	public void accept(String text) {
		value.put(name, StringTag.valueOf(text));

		stringValue = text;
		if (validator.test(text)) {
			valid = true;
			setTextColor(14737632);
		} else {
			valid = false;
			setTextColor(0xffff3f30);
		}

		callback.accept(this);
	}
}
