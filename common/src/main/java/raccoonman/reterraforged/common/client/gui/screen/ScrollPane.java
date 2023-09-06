package raccoonman.reterraforged.common.client.gui.screen;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import raccoonman.reterraforged.common.client.gui.screen.element.Element;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayRenderer;
import raccoonman.reterraforged.common.client.gui.screen.preview.Preview;

public class ScrollPane extends ContainerObjectSelectionList<ScrollPane.Entry> implements OverlayRenderer {

    private boolean hovered = false;
    private boolean renderSelection = true;

    public ScrollPane(int slotHeightIn) {
        super(Minecraft.getInstance(), 0, 0, 0, 0, slotHeightIn);
        setRenderSelection(false);
    }

    public void addButton(AbstractWidget button) {
        super.addEntry(new Entry(button));
    }

    @Override
    public void renderOverlays(PoseStack matrixStack, Screen screen, int x, int y) {
        for (Entry entry : this.children()) {
            if (entry.isMouseOver(x, y) && entry.option.isMouseOver(x, y)) {
                AbstractWidget button = entry.option;
                if (button instanceof Element) {
                    Element element = (Element) button;
                    if (!element.getTooltip().isEmpty()) {
                        screen.renderTooltip(matrixStack, element.getToolTipText(), x, y);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public int getRowWidth() {
        return width - 20;
    }

    @Override
    public void render(PoseStack matrixStack, int x, int y, float partialTicks) {
        super.render(matrixStack, x, y, partialTicks);
        hovered = isMouseOver(x, y);
    }

    @Override
    protected int getScrollbarPosition() {
        return getRowRight();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Entry entry : children()) {
            if (!entry.isMouseOver(mouseX, mouseY) && entry.option.isFocused()) {
                entry.option.setFocused(true);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double direction) {
        return hovered && super.mouseScrolled(x, y, direction);
    }

    @Override
    protected boolean isSelectedItem(int index) {
        return renderSelection && Objects.equals(getSelected(), children().get(index));
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        public final AbstractWidget option;

        public Entry(AbstractWidget option) {
            this.option = option;
        }

        @Nullable
        public AbstractWidget getFocused() {
            return option;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(option);
        }

        @Override
        public boolean mouseClicked(double x, double y, int button) {
            if (super.mouseClicked(x, y, button)) {
                setSelected(this);
                option.active = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double x, double y, int button) {
            super.mouseReleased(x, y, button);
            return option.mouseReleased(x, y, button);
        }

        @Override
        public void render(PoseStack matrixStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            int optionWidth = Math.min(396, width);
            int padding = (width - optionWidth) / 2;
            option.setX(left + padding);
            option.setY(top);
            option.visible = true;
            option.setWidth(optionWidth);
            option.height = height - 1;
            if (option instanceof Preview) {
                option.height = (option.getWidth());
            }
            option.render(matrixStack, mouseX, mouseY, partialTicks);
        }

		@Override
		public List<? extends NarratableEntry> narratables() {
			return Collections.singletonList(this.option);
		}
    }
}
