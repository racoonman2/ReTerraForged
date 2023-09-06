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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class RTFButton extends Button implements Element {
    private final List<String> tooltip;

    public RTFButton(Component displayString) {
        super(0, 0, 200, 20, displayString, b -> {}, DEFAULT_NARRATION);
        this.tooltip = Collections.emptyList();
    }

    public RTFButton(Component displayString, String... tooltip) {
        super(0, 0, 200, 20, displayString, b -> {}, DEFAULT_NARRATION);
        this.tooltip = Arrays.asList(tooltip);
    }

    public RTFButton init(int x, int y, int width, int height) {
        setX(x);
        setY(y);
        setWidth(width);
        this.height = height;
        return this;
    }

    @Override
    public List<String> getTooltip() {
        return tooltip;
    }

    public static RTFButton create(Component title, int x, int y, int width, int height, String tooltip, Runnable action) {
    	RTFButton button = new RTFButton(title) {

            private final List<String> tooltips = Collections.singletonList(tooltip);

            @Override
            public List<String> getTooltip() {
                return tooltips;
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                action.run();
            }
        };

        button.setX(x);
        button.setY(y);
        button.setWidth(width);
        button.height = height;

        return button;
    }
}
