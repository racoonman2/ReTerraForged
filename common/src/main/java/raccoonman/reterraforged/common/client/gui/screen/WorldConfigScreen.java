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

package raccoonman.reterraforged.common.client.gui.screen;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.common.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFLabel;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayScreen;
import raccoonman.reterraforged.common.client.gui.screen.page.Page;
import raccoonman.reterraforged.common.client.gui.screen.page.PresetsPage;
import raccoonman.reterraforged.common.client.gui.screen.page.SimplePage;
import raccoonman.reterraforged.common.client.gui.screen.page.SimplePreviewPage;
import raccoonman.reterraforged.common.client.gui.screen.page.WorldPage;
import raccoonman.reterraforged.common.client.gui.screen.preview.PreviewPage;

public class WorldConfigScreen extends OverlayScreen {

    private static final Button.OnPress NO_ACTION = b -> {};

    private final Page[] pages;
    private final PreviewPage preview;
    private final CreateWorldScreen parent;

    private int pageIndex = 0;
//    private DimensionGeneratorSettings outputSettings;

    public WorldConfigScreen(CreateWorldScreen parent, WorldCreationContext ctx) {
        this.parent = parent;
        this.preview = new PreviewPage((int) ctx.options().seed()); // i want to use Long.hashCode here but im gonna try to keep compatability with 1.16.5 seeds first
        this.pages = new Page[] {
        	new PresetsPage(this, this.preview),
        	new WorldPage(this.preview),
        	new SimplePreviewPage(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_CLIMATE), "climate", this.preview),
        	new SimplePreviewPage(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_TERRAIN), "terrain", this.preview),
        	new SimplePreviewPage(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_RIVER), "rivers", this.preview),
        	new SimplePreviewPage(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_FILTERS), "filters", this.preview),
        	new SimplePage(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_STRUCTURES), "structures"),
        	new SimplePage(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_MISCELLANEOUS), "miscellaneous")
        };
    }

    private boolean isPresetsPage() {
        return pages[pageIndex] instanceof PresetsPage;
    }

    @Override
    public void init() {
        super.clearWidgets();

        int buttonsCenter = width / 2;
        int buttonWidth = 50;
        int buttonHeight = 20;
        int buttonPad = 2;
        int buttonsRow = height - 25;

        if (pageIndex < pages.length) {
            Page page = pages[pageIndex];
            RTFLabel title = new RTFLabel(page.getTitle());
            title.visible = true;
            title.setX(16);
            title.setY(15);
            this.addRenderableOnly(title);
            
            page.initPage(10, 30, this);
            preview.initPage(10, 30, this);
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (b) -> this.onClose()).bounds(buttonsCenter - buttonWidth - buttonPad, buttonsRow, buttonWidth, buttonHeight).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> {
            for (Page page : pages) {
                page.save();
            }
            
            this.onClose();
        }).bounds(buttonsCenter + buttonPad, buttonsRow, buttonWidth, buttonHeight).build());
        this.addRenderableWidget(new Button(buttonsCenter - (buttonWidth * 2 + (buttonPad * 3)), buttonsRow, buttonWidth, buttonHeight, Component.literal("<<"), NO_ACTION, Supplier::get) {
        	
            @Override
            public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                super.active = hasPrevious();
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                if (hasPrevious()) {
                    pageIndex--;
                    init();
                }
            }
        });

        this.addRenderableWidget(new Button(buttonsCenter + buttonWidth + (buttonPad * 3), buttonsRow, buttonWidth, buttonHeight, Component.literal(">>"), NO_ACTION, Supplier::get) {
        	
            @Override
            public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            	boolean hasSelected = pages[pageIndex] instanceof PresetsPage presets ? presets.hasSelectedPreset() : true;
            	
                super.active = hasSelected && hasNext();
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                if (hasNext()) {
                    pageIndex++;
                    init();
                }
            }
        });

        super.init();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(matrixStack);
        pages[pageIndex].visit(pane -> pane.render(matrixStack, mouseX, mouseY, partialTicks));

        if (pageIndex > 0) {
            preview.visit(pane -> pane.render(matrixStack, mouseX, mouseY, partialTicks));
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks); 
    }

    @Override
    public void renderOverlays(PoseStack matrixStack, Screen screen, int mouseX, int mouseY) {
        super.renderOverlays(matrixStack, screen, mouseX, mouseY);
        pages[pageIndex].visit(pane -> pane.renderOverlays(matrixStack, screen, mouseX, mouseY));
        if (!isPresetsPage()) {
            preview.visit(pane -> pane.renderOverlays(matrixStack, screen, mouseX, mouseY));
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean a = pages[pageIndex].action(pane -> pane.mouseClicked(x, y, button));
        boolean b = isPresetsPage() || preview.action(pane -> pane.mouseClicked(x, y, button));
        boolean c = preview.getPreviewWidget().click(x, y);
        boolean d = super.mouseClicked(x, y, button);
        return a || b || c || d;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean a = pages[pageIndex].action(pane -> pane.mouseReleased(x, y, button));
        boolean b = isPresetsPage() || preview.action(pane -> pane.mouseReleased(x, y, button));
        boolean c = super.mouseReleased(x, y, button);
        return a || b || c;
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        boolean a = pages[pageIndex].action(pane -> pane.mouseDragged(x, y, button, dx, dy));
        boolean b = isPresetsPage() || preview.action(pane -> pane.mouseDragged(x, y, button, dx, dy));
        boolean c = super.mouseDragged(x, y, button, dx, dy);
        return a || b || c;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double direction) {
        boolean a = pages[pageIndex].action(pane -> pane.mouseScrolled(x, y, direction));
        boolean b = isPresetsPage() || preview.action(pane -> pane.mouseScrolled(x, y, direction));
        boolean c = super.mouseScrolled(x, y, direction);
        return a || b || c;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        boolean a = pages[pageIndex].action(pane -> pane.keyPressed(i, j, k));
        boolean b = isPresetsPage() || preview.action(pane -> pane.keyPressed(i, j, k));
        boolean c = super.keyPressed(i, j, k);
        return a || b || c;
    }

    @Override
    public boolean charTyped(char ch, int code) {
        boolean a = pages[pageIndex].action(pane -> pane.charTyped(ch, code));
        boolean b = isPresetsPage() || preview.action(pane -> pane.charTyped(ch, code));
        boolean c = super.charTyped(ch, code);
        return a || b || c;
    }

    @Override
    public void onClose() {
        for (Page page : this.pages) {
            page.close();
        }
        this.preview.close();
        this.minecraft.setScreen(this.parent);
    }

    private boolean hasNext() {
        return pageIndex + 1 < pages.length;
    }

    private boolean hasPrevious() {
        return pageIndex > 0;
    }
}
