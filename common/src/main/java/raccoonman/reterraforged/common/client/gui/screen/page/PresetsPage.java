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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.file.PathUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.common.client.gui.screen.ScrollPane;
import raccoonman.reterraforged.common.client.gui.screen.WorldConfigScreen;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFButton;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFLabel;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFTextBox;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayScreen;
import raccoonman.reterraforged.common.client.gui.screen.preview.PreviewPage;
import raccoonman.reterraforged.platform.config.Config;

public class PresetsPage extends BasePage {

    private static final Predicate<String> NAME_VALIDATOR = Pattern.compile("^[A-Za-z0-9\\-_ ]+$").asPredicate();

    private final PreviewPage preview;
    private final RTFTextBox nameInput;
    private final WorldConfigScreen parent;

    public PresetsPage(WorldConfigScreen parent, PreviewPage preview) {
        CompoundTag value = new CompoundTag();
        value.putString("name", "");
        this.parent = parent;
        this.preview = preview;
        this.nameInput = new RTFTextBox("name", value);
        this.nameInput.setColorValidator(NAME_VALIDATOR);
    }

    @Override
    public Component getTitle() {
        return Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_PRESETS);
    }

    @Override
    public void close() {
//        manager.saveAll();
    }

    @Override
    public void save() {

    }

    protected void update() {
//        preview.apply(settings -> DataUtils.fromNBT(instance.settingsData, settings));
    }

    @Override
    public void init(OverlayScreen parent) {
        rebuildPresetList();

        Column right = getColumn(1);
        right.scrollPane.addButton(nameInput);

        right.scrollPane.addButton(new RTFButton(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_CREATE)) {

            @Override
            public void render(PoseStack matrixStack, int x, int z, float ticks) {
                super.active = nameInput.isValid() && !isNameUsed(nameInput.getValue());
                super.render(matrixStack, x, z, ticks);
            }

            @Override
            public void onClick(double x, double y) {
                super.onClick(x, y);
                try {
                    Path config = resolveConfigPath(ReTerraForged.MOD_ID).resolve(nameInput.getValue() + ".json");
                	PathUtils.createParentDirectories(config);
					Files.createFile(config);
				} catch (IOException e) {
					e.printStackTrace();
				}
                
                nameInput.setValue("");

                rebuildPresetList();
            }
        });
        right.scrollPane.addButton(new RTFButton(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_SAVE)) {

            @Override
            public void render(PoseStack matrixStack, int x, int z, float ticks) {
                super.active = hasSelectedPreset();
                super.render(matrixStack, x, z, ticks);
            }

            @Override
            public void onClick(double x, double y) {
                super.onClick(x, y);
//                getSelected().ifPresent(preset -> {
//                    if (preset.internal()) {
//                        return;
//                    }
//
//                    // create a copy of the settings
//                    TerraSettings settings = instance.copySettings();
//
//                    // replace the current preset with the updated version
//                    manager.add(new Preset(preset.getName(), settings));
//                    manager.saveAll();

                    // update the ui
//                    rebuildPresetList();
//                });
            }
        });

        right.scrollPane.addButton(new RTFButton(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_RESET)) {

            @Override
            public void render(PoseStack matrixStack, int x, int z, float ticks) {
                super.active = hasSelectedPreset();
                super.render(matrixStack, x, z, ticks);
            }

            @Override
            public void onClick(double x, double y) {
                super.onClick(x, y);
//                getSelected().ifPresent(preset -> {
//                    if (preset.internal()) {
//                        return;
//                    }
//
//                    // create new preset with the same name but default settings
//                    Preset reset = new Preset(preset.getName(), new TerraSettings());
//
//                    // replaces by name
//                    manager.add(reset);
//
//                    // update the ui
//                    rebuildPresetList();
//                });
            }
        });

        right.scrollPane.addButton(new RTFButton(Component.translatable(RTFTranslationKeys.CUSTOMIZE_WORLD_DELETE)) {

            @Override
            public void render(PoseStack matrixStack, int x, int z, float ticks) {
                super.active = hasSelectedPreset();
                super.render(matrixStack, x, z, ticks);
            }

            @Override
            public void onClick(double x, double y) {
                super.onClick(x, y);
				try {
					Path config = resolveConfigPath(ReTerraForged.MOD_ID);
					Optional<Path> selected = getSelected().map((path) -> config.resolve(path + ".json"));
					if(selected.isPresent()) {
						Files.deleteIfExists(selected.get());
						rebuildPresetList();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        });

        right.scrollPane.addButton(this.preview.getPreviewWidget());

        // used to pad the scroll-pane out so that the preview legend scrolls on larger gui scales
        RTFButton spacer = createSpacer();
        for (int i = 0; i < 10; i++) {
            right.scrollPane.addButton(spacer);
        }
    }

    public boolean hasSelectedPreset() {
        return getSelected().isPresent();
    }

    private Optional<String> getSelected() {
        ScrollPane.Entry entry = getColumn(0).scrollPane.getSelected();
        if (entry == null) {
            return Optional.empty();
        }
        return Optional.of(entry.option.getMessage().getString());
    }

    private void rebuildPresetList() {
        Column left = getColumn(0);
        left.scrollPane.setSelected(null);
        left.scrollPane.setRenderSelection(true);
        left.scrollPane.children().clear();

        try {
        	List<Path> configs = new ArrayList<>();
        	listConfigPaths("terraforged", configs);
        	listConfigPaths(ReTerraForged.MOD_ID, configs);
        	for(Path path : configs) {
              left.scrollPane.addButton(new RTFLabel(Component.literal(FileNameUtils.getBaseName(path.getFileName().toString()))));
            }
        } catch(IOException e) {
        	e.printStackTrace();
        }
    }
    
    private boolean isNameUsed(String name) {
    	return getColumn(0).scrollPane.children().stream().filter((entry) -> {
    		return entry.getFocused().getMessage().getString().equals(name);
    	}).findAny().isPresent();
    }
    
    private static void listConfigPaths(String edition, List<Path> paths) throws IOException {
        Path configPath = resolveConfigPath(edition);
        if(Files.exists(configPath)) {
	        Files.list(configPath).filter((path) -> {
	        	return path.getFileName().toString().endsWith(".json");
	        }).forEach(paths::add);
        }
    }

    private static RTFButton createSpacer() {
        return new RTFButton(Component.empty()) {
            @Override
            public void render(PoseStack matrixStack, int x, int y, float tick) { }
        };
    }
    
    private static Path resolveConfigPath(String edition) throws IOException {
    	return Config.getConfigPath().resolve(edition).resolve("presets");
    }
}
