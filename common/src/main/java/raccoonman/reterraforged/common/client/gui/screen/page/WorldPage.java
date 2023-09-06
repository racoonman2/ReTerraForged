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

package raccoonman.reterraforged.common.client.gui.screen.page;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.common.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFTextBox;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayScreen;

public class WorldPage extends BasePage {

	private final UpdatablePage preview;

    private CompoundTag worldSettings = null;
    private CompoundTag dimSettings = null;
    
	public WorldPage(UpdatablePage preview) {
		this.preview = preview;
	}

	@Override
	public Component getTitle() {
		return Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_WORLD);
	}

	@Override
	public void save() {

	}

	@Override
	public void init(OverlayScreen parent) {
		// re-sync settings from the settings object to the data structure
		worldSettings = getWorldSettings();
		dimSettings = getDimSettings();

		Column left = getColumn(0);
		addElements(left.left, left.top, left, worldSettings, true, left.scrollPane::addButton, this::update);

		addElements(left.left, left.top, left, dimSettings, true, left.scrollPane::addButton, this::update);
	}

	@Override
	public void onAddWidget(AbstractWidget widget) {
		if (widget instanceof RTFTextBox) {
			RTFTextBox input = (RTFTextBox) widget;
			input.setColorValidator(string -> BuiltInRegistries.BLOCK.containsKey(new ResourceLocation(string)));
		}
	}

	protected void update() {
		super.update();
//		preview.apply(settings -> {
//			DataUtils.fromNBT(worldSettings, settings.world);
//			DataUtils.fromNBT(dimSettings, settings.dimensions);
//		});
	}

	private CompoundTag getWorldSettings() {
		CompoundTag tag = new CompoundTag();
		tag.putString("test3", "test4");
		return tag;
	}

	private CompoundTag getDimSettings() {
//		CompoundTag dimSettings = instance.settingsData.getCompound("dimensions");
//		CompoundTag generators = dimSettings.getCompound("dimensions");
//		for (String name : generators.getAllKeys()) {
//			if (name.startsWith("#")) {
//				INBT value = generators.get(name.substring(1));
//				if (value instanceof StringNBT) {
//					CompoundNBT metadata = generators.getCompound(name);
//					metadata.put("options", getWorldTypes());
//				}
//			}
//		}
		CompoundTag tag = new CompoundTag();
		tag.putString("test", "test2");
		return tag;
	}

	private static ListTag getWorldTypes() {
//		ListNBT options = new ListNBT();
//		for (ForgeWorldType type : ForgeRegistries.WORLD_TYPES) {
//			String name = DimUtils.getDisplayString(type);
//			INBT value = StringNBT.valueOf(name);
//			options.add(value);
//		}
		ListTag list = new ListTag();
		list.add(StringTag.valueOf("test"));
		return list;
	}
}
