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

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.common.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.common.client.gui.screen.element.Buttons;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFLabel;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFSlider;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFTextBox;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFSlider.Precision;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayScreen;
import raccoonman.reterraforged.common.client.gui.screen.page.Page;
import raccoonman.reterraforged.common.client.gui.screen.page.PresetsPage;
import raccoonman.reterraforged.common.client.gui.screen.page.SimplePage;
import raccoonman.reterraforged.common.client.gui.screen.preview.PreviewPage;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentType;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;

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
        this.pages = new Page[] { new PresetsPage(this, this.preview), new WorldPage(), new ClimatePage(), new TerrainPage(), new RiverPage(), new FilterPage(), new StructuresPage(), new MiscellaneousPage() };
    }

    @Override
    public void init() {
        super.clearWidgets();

        int buttonsCenter = this.width / 2;
        int buttonWidth = 50;
        int buttonHeight = 20;
        int buttonPad = 2;
        int buttonsRow = this.height - 25;

        if (this.pageIndex < this.pages.length) {
            Page page = this.pages[this.pageIndex];
            RTFLabel title = new RTFLabel(page.getTitle());
            title.visible = true;
            title.setX(16);
            title.setY(15);
            this.addRenderableOnly(title);
            
            page.initPage(10, 30, this);
            this.preview.initPage(10, 30, this);
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (b) -> this.onClose()).bounds(buttonsCenter - buttonWidth - buttonPad, buttonsRow, buttonWidth, buttonHeight).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> {
            for (Page page : this.pages) {
                page.save();
            }
            
            this.onClose();
        }).bounds(buttonsCenter + buttonPad, buttonsRow, buttonWidth, buttonHeight).build());
        this.addRenderableWidget(new Button(buttonsCenter - (buttonWidth * 2 + (buttonPad * 3)), buttonsRow, buttonWidth, buttonHeight, Component.literal("<<"), NO_ACTION, Supplier::get) {
        	
            @Override
            public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                super.active = WorldConfigScreen.this.hasPrevious();
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                if (WorldConfigScreen.this.hasPrevious()) {
                    WorldConfigScreen.this.pageIndex--;
                    WorldConfigScreen.this.init();
                }
            }
        });

        this.addRenderableWidget(new Button(buttonsCenter + buttonWidth + (buttonPad * 3), buttonsRow, buttonWidth, buttonHeight, Component.literal(">>"), NO_ACTION, Supplier::get) {
        	
            @Override
            public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            	boolean hasSelected = WorldConfigScreen.this.pages[WorldConfigScreen.this.pageIndex] instanceof PresetsPage presets ? presets.hasSelectedPreset() : true;
            	
                super.active = hasSelected && WorldConfigScreen.this.hasNext();
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                if (WorldConfigScreen.this.hasNext()) {
                	WorldConfigScreen.this.pageIndex++;
                	WorldConfigScreen.this.init();
                }
            }
        });

        super.init();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(matrixStack);
        this.pages[this.pageIndex].visit(pane -> pane.render(matrixStack, mouseX, mouseY, partialTicks));

        if (this.pageIndex > 0) {
        	this.preview.visit(pane -> pane.render(matrixStack, mouseX, mouseY, partialTicks));
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks); 
    }

    @Override
    public void renderOverlays(PoseStack matrixStack, Screen screen, int mouseX, int mouseY) {
        super.renderOverlays(matrixStack, screen, mouseX, mouseY);
        this.pages[this.pageIndex].visit(pane -> pane.renderOverlays(matrixStack, screen, mouseX, mouseY));
        if (!this.isPresetsPage()) {
        	this.preview.visit(pane -> pane.renderOverlays(matrixStack, screen, mouseX, mouseY));
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean a = this.pages[this.pageIndex].action(pane -> pane.mouseClicked(x, y, button));
        boolean b = this.isPresetsPage() || this.preview.action(pane -> pane.mouseClicked(x, y, button));
        boolean c = this.preview.getPreviewWidget().click(x, y);
        boolean d = super.mouseClicked(x, y, button);
        return a || b || c || d;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean a = this.pages[this.pageIndex].action(pane -> pane.mouseReleased(x, y, button));
        boolean b = this.isPresetsPage() || this.preview.action(pane -> pane.mouseReleased(x, y, button));
        boolean c = super.mouseReleased(x, y, button);
        return a || b || c;
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        boolean a = this.pages[this.pageIndex].action(pane -> pane.mouseDragged(x, y, button, dx, dy));
        boolean b = this.isPresetsPage() || this.preview.action(pane -> pane.mouseDragged(x, y, button, dx, dy));
        boolean c = super.mouseDragged(x, y, button, dx, dy);
        return a || b || c;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double direction) {
        boolean a = this.pages[this.pageIndex].action(pane -> pane.mouseScrolled(x, y, direction));
        boolean b = this.isPresetsPage() || this.preview.action(pane -> pane.mouseScrolled(x, y, direction));
        boolean c = super.mouseScrolled(x, y, direction);
        return a || b || c;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        boolean a = this.pages[pageIndex].action(pane -> pane.keyPressed(i, j, k));
        boolean b = this.isPresetsPage() || this.preview.action(pane -> pane.keyPressed(i, j, k));
        boolean c = super.keyPressed(i, j, k);
        return a || b || c;
    }

    @Override
    public boolean charTyped(char ch, int code) {
        boolean a = this.pages[pageIndex].action(pane -> pane.charTyped(ch, code));
        boolean b = this.isPresetsPage() || this.preview.action(pane -> pane.charTyped(ch, code));
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
        return this.pageIndex + 1 < this.pages.length;
    }

    private boolean hasPrevious() {
        return this.pageIndex > 0;
    }
    
    private boolean isPresetsPage() {
        return this.pages[this.pageIndex] instanceof PresetsPage;
    }
    
    private static class WorldPage extends SimplePage {
    	public CycleButton<ContinentType> continentType = Buttons.cycleEnum(ContinentType.values())
    		.create(-1, -1, -1, -1, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_TYPE));
    	public CycleButton<DistanceFunction> continentShape = Buttons.cycleEnum(DistanceFunction.values())
    		.create(-1, -1, -1, -1, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_SHAPE));
    	public RTFSlider continentScale = new RTFSlider(-1, -1, -1, 3000, 100, 10000, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_SCALE), Precision.WHOLE);
    	public RTFSlider continentJitter = new RTFSlider(-1, -1, -1, 0.699F, 0.5F, 1.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_JITTER), Precision.DECIMAL);
    	public RTFSlider continentSkipping = new RTFSlider(-1, -1, -1, 0.25F, 0.0F, 1.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_SKIPPING), Precision.DECIMAL);
    	public RTFSlider continentSizeVariance = new RTFSlider(-1, -1, -1, 0.25F, 0.0F, 0.75F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_SIZE_VARIANCE), Precision.DECIMAL);
    	public RTFSlider continentNoiseOctaves = new RTFSlider(-1, -1, -1, 5, 1.0F, 5.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_OCTAVES), Precision.WHOLE);
    	public RTFSlider continentNoiseGain = new RTFSlider(-1, -1, -1, 0.259F, 0.0F, 0.5F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_GAIN), Precision.DECIMAL);
    	public RTFSlider continentNoiseLacunarity = new RTFSlider(-1, -1, -1, 4.329F, 1.0F, 10.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_LACUNARITY), Precision.DECIMAL);
    	public RTFSlider deepOcean = new RTFSlider(-1, -1, -1, 0.1F, 0.0F, 1.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_LACUNARITY), Precision.DECIMAL);
    	public RTFSlider shallowOcean = new RTFSlider(-1, -1, -1, 0.25F, 0.0F, 1.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_LACUNARITY), Precision.DECIMAL);
    	public RTFSlider beach = new RTFSlider(-1, -1, -1, 0.326F, 0.0F, 1.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_LACUNARITY), Precision.DECIMAL);
    	public RTFSlider coast = new RTFSlider(-1, -1, -1, 0.448F, 0.0F, 1.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_LACUNARITY), Precision.DECIMAL);
    	public RTFSlider inland = new RTFSlider(-1, -1, -1, 0.5F, 0.0F, 1.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_LACUNARITY), Precision.DECIMAL);
    	public RTFSlider worldHeight = new RTFSlider(-1, -1, -1, 256.0F, 0.0F, 1024.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_WORLD_HEIGHT), Precision.WHOLE);
    	public RTFSlider seaLevel = new RTFSlider(-1, -1, -1, 63.0F, 0.0F, 255.0F, Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_SEA_LEVEL), Precision.WHOLE);
    	
		public WorldPage() {
			super(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_WORLD));
		}

		@Override
	    public void init(OverlayScreen parent) {
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_CONTINENT)));
	    	this.addWidget(0, this.continentType);
	    	this.addWidget(0, this.continentShape);
	    	this.addWidget(0, this.continentScale);
	    	this.addWidget(0, this.continentJitter);
	    	this.addWidget(0, this.continentSkipping);
	    	this.addWidget(0, this.continentSizeVariance);
	    	this.addWidget(0, this.continentNoiseOctaves);
	    	this.addWidget(0, this.continentNoiseGain);
	    	this.addWidget(0, this.continentNoiseLacunarity);
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_CONTROL_POINTS)));
	    	this.addWidget(0, this.deepOcean);
	    	this.addWidget(0, this.shallowOcean);
	    	this.addWidget(0, this.beach);
	    	this.addWidget(0, this.coast);
	    	this.addWidget(0, this.inland);
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_PROPERTIES)));
	    	this.addWidget(0, this.worldHeight);
	    	this.addWidget(0, this.seaLevel);
	    } 
		
		@Override
		public void onAddWidget(AbstractWidget widget) {
			if (widget instanceof RTFTextBox input) {
				input.setColorValidator(string -> BuiltInRegistries.BLOCK.containsKey(new ResourceLocation(string)));
			}
		}
    }
    
    private static class ClimatePage extends SimplePage {

		public ClimatePage() {
			super(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_CLIMATE));
		}

		@Override
	    public void init(OverlayScreen parent) {
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_TEMPERATURE)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_MOISTURE)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_BIOME_SHAPE)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_BIOME_EDGE_SHAPE)));
	    }
    }
    
    private static class TerrainPage extends SimplePage {

		public TerrainPage() {
			super(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_TERRAIN));
		}

		@Override
	    public void init(OverlayScreen parent) {
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_GENERAL)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_STEPPE)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_PLAINS)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_HILLS)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_DALES)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_PLATEAU)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_BADLANDS)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_TORRIDONIAN)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_MOUNTAINS)));
	    }
    }
    
    private static class RiverPage extends SimplePage {

		public RiverPage() {
			super(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_RIVER));
		}

		@Override
	    public void init(OverlayScreen parent) {
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_MAIN_RIVERS)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_BRANCH_RIVERS)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_LAKES)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_WETLANDS)));
	    }
    }
    
    private static class FilterPage extends SimplePage {

		public FilterPage() {
			super(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_FILTERS));
		}
    	
		@Override
	    public void init(OverlayScreen parent) {
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_EROSION)));
	    	this.addWidget(0, new RTFLabel(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_SMOOTHING)));
	    }
    }
    
    private static class StructuresPage extends SimplePage {

		public StructuresPage() {
			super(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_STRUCTURES));
		}
    }
    
    private static class MiscellaneousPage extends SimplePage {

		public MiscellaneousPage() {
			super(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_MISCELLANEOUS));
		}
    }
}
