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

package raccoonman.reterraforged.common.client.gui.screen.preview;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFButton;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayScreen;
import raccoonman.reterraforged.common.client.gui.screen.page.UpdatablePage;

public class PreviewPage extends UpdatablePage {
	private final Preview preview;

	public PreviewPage(int seed) {
		this.preview = new Preview(seed);
	}

    public Preview getPreviewWidget() {
        return preview;
    }

    public int getSeed() {
        return preview.getSeed();
    }

	@Override
	public void close() {
		preview.close();
	}

	@Override
	public void init(OverlayScreen parent) {
		Column right = getColumn(1);
		preview.setX(0);
		preview.setY(0);
		preview.setWidth(Preview.SIZE);
		preview.height = Preview.SIZE;

		CompoundTag tag = new CompoundTag();
		tag.putString("test settings 1", "value");
		tag.putBoolean("test settings 2", true);
		addElements(right.left, right.top, right, tag, right.scrollPane::addButton, this::update);

		right.scrollPane.addButton(new RTFButton(Component.literal("preview_seed")) {
			@Override
			public void onPress() {
				preview.regenerate();
				update();
			}
		});

		right.scrollPane.addButton(preview);

		// used to pad the scroll-pane out so that the preview legend scrolls on larger
		// gui scales
		RTFButton spacer = createSpacer();
		for (int i = 0; i < 10; i++) {
			right.scrollPane.addButton(spacer);
		}

		update();
	}

	@Override
	public void update() {
//		preview.update(settings, previewerSettings);
	}

	private static RTFButton createSpacer() {
		return new RTFButton(Component.empty()) {
			
			@Override
			public void render(PoseStack matrixStack, int x, int y, float tick) {
			}
		};
	}
}
