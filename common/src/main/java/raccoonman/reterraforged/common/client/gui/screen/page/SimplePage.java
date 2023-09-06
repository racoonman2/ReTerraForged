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

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFLabel;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayScreen;
import raccoonman.reterraforged.common.client.gui.screen.page.Page.Column;

public abstract class SimplePage extends BasePage {
	private static final int SLIDER_HEIGHT = 20;
	private static final int SLIDER_PAD = 2;

    private final Component title;

    public SimplePage(Component title) {
        this.title = title;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public void save() {

    }

    @Override
    public void init(OverlayScreen parent) {
        Column left = getColumn(0);
        addElements(left.left, left.top, left, new CompoundTag(), true, left.scrollPane::addButton, this::update);
    }

	AtomicInteger top = new AtomicInteger();
    public void addWidget(int columnIndex, AbstractWidget widget) {
		Column column = this.getColumn(columnIndex);
		if (widget != null) {
			widget.setWidth(column.width);
			widget.height = SLIDER_HEIGHT;
			widget.setX(column.left);
			widget.setY(column.top + top.getAndAdd(SLIDER_HEIGHT + SLIDER_PAD));
			column.scrollPane.addButton(widget);
			onAddWidget(widget);
		}
    }
    
    @Override
    protected void update() {
        super.update();
    }
}
