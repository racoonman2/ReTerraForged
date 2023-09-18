package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.client.gui.screen.page.BisectedPage;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.MutableFunctionContext;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.RTFNoiseData;

public abstract class PresetEditorPage extends BisectedPage<PresetConfigScreen, AbstractWidget, AbstractWidget> {
	private Preview preview;
	protected PresetEntry preset;
	
	public PresetEditorPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen);
		
		this.preset = preset;
	}
	
	protected void regenerate() {
		this.preview.regenerate();
	}
	
	@Override
	public void init() {
		super.init();
		
		if(this.preview != null) {
			this.preview.close();
		}
		this.preview = new Preview(this.screen.getSettings().options().seed());
		this.preview.regenerate();
		this.right.addWidget(this.preview);
	}
	
	@Override
	public void onClose() {
		super.onClose();
	
		try {
			this.preset.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.preview.close();
	}
	
	@Override
	public void onDone() {
		super.onDone();
		
		try {
			this.screen.applyPreset(this.preset);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//FIXME: this should be private
	public class Preview extends Button {
	    private static final int FACTOR = 4;
	    public static final int SIZE = (1 << 4) << FACTOR;
	    private static final float[] LEGEND_SCALES = {1, 0.9F, 0.75F, 0.6F};

	    private final int offsetX;
	    private final int offsetZ;
	    private final Random random = new Random(System.currentTimeMillis());
	    private final DynamicTexture texture = new DynamicTexture(new NativeImage(SIZE, SIZE, false));

	    private long seed;
	    
	    private String hoveredCoords = "";
	    private String[] values = {"", "", ""};
	    private String[] labels = {"preview_area", "preview_terrain", "preview_biome"};

	    public Preview(long seed) {
	        super(0, 0, 0, 0, Component.literal(""), (b) -> {}, DEFAULT_NARRATION);
	        this.seed = seed;
	        this.offsetX = 0;
	        this.offsetZ = 0;
	    }

	    public long getSeed() {
	        return seed;
	    }

	    public void regenerate() {
	        this.seed = random.nextInt();
	        WorldCreationContext settings = PresetEditorPage.this.screen.getSettings();
	        RegistryAccess.Frozen registries = settings.worldgenLoadContext();
	        HolderLookup.Provider provider = PresetEditorPage.this.preset.getPreset().buildPatch(registries);
	        HolderGetter<Noise> noiseLookup = provider.lookupOrThrow(RTFRegistries.NOISE);
	        Noise rootNoise = noiseLookup.getOrThrow(RTFNoiseData.ROOT).value();
	        
	        final int cellCount = 16;
			NativeImage pixels = this.texture.getPixels();
			int cellWidth = pixels.getWidth() / cellCount;
			int cellHeight = pixels.getHeight() / cellCount;

			CompletableFuture<?>[] futures = new CompletableFuture[cellCount * cellCount];
			for(int x = 0; x < cellCount; x++) {
				for(int y = 0; y < cellCount; y++) {
					final int cx = x * cellWidth;
					final int cy = y * cellHeight;
					futures[x + y * cellCount] = CompletableFuture.runAsync(() -> {
						for(int lx = 0; lx < cellWidth; lx++) {
							for(int ly = 0; ly < cellHeight; ly++) {
								int tx = cx + lx;
								int ty = cy + ly;
								int color = (int) (rootNoise.compute(tx * 30, ty * 30, (int) PresetEditorPage.this.screen.getSettings().options().seed()) * 255);
								
					            pixels.setPixelRGBA(tx, ty, rgba(color, color, color));
							}
						}
					}, Util.backgroundExecutor());
				}
			}
			CompletableFuture.allOf(futures).join();
	        this.texture.upload();
	    }
	    
	    private static int rgba(int r, int g, int b) {
	        return r + (g << 8) + (b << 16) + (255 << 24);
	    }
	    
	    public void close() {
	        texture.close();
	    }

	    public boolean click(double mx, double my) {
	        if (updateLegend((int) mx, (int) my) && !hoveredCoords.isEmpty()) {
	            super.playDownSound(Minecraft.getInstance().getSoundManager());
	            PresetEditorPage.this.screen.minecraft.keyboardHandler.setClipboard(hoveredCoords);
	            return true;
	        }
	        return false;
	    }

	    @Override
	    public void render(PoseStack matrixStack, int mx, int my, float partialTicks) {
//	        this.height = getSize();
	//
//	        preRender();
	//
	    	RenderSystem.setShaderTexture(0, this.texture.getId());
	        Screen.blit(matrixStack, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
	        
//	        updateLegend(mx, my);
	//
//	        renderLegend(matrixStack, mx, my, labels, values, x, y + width, 10, 0xFFFFFF);
	    }

	    private boolean updateLegend(int mx ,int my) {
//	        if (tile != null) {
//	            int left = this.x;
//	            int top = this.y;
//	            float size = this.width;
	//
//	            int zoom = getZoom();
//	            int width = Math.max(1, tile.getBlockSize().size * zoom);
//	            int height = Math.max(1, tile.getBlockSize().size * zoom);
//	            values[0] = width + "x" + height;
//	            if (mx >= left && mx <= left + size && my >= top && my <= top + size) {
//	                float fx = (mx - left) / size;
//	                float fz = (my - top) / size;
//	                int ix = NoiseUtil.round(fx * tile.getBlockSize().size);
//	                int iz = NoiseUtil.round(fz * tile.getBlockSize().size);
//	                Cell cell = tile.getCell(ix, iz);
//	                values[1] = getTerrainName(cell);
//	                values[2] = getBiomeName(cell);
	//
//	                int dx = (ix - (tile.getBlockSize().size / 2)) * zoom;
//	                int dz = (iz - (tile.getBlockSize().size / 2)) * zoom;
	//
//	                hoveredCoords = (center.x + dx) + ":" + (center.z + dz);
//	                return true;
//	            } else {
//	                hoveredCoords = "";
//	            }
//	        }
	        return false;
	    }

	    private float getLegendScale() {
	        int index = PresetEditorPage.this.screen.minecraft.options.guiScale().get() - 1;
	        if (index < 0 || index >= LEGEND_SCALES.length) {
	            // index=-1 == GuiScale(AUTO) which is the same as GuiScale(4)
	            // values above 4 don't exist but who knows what mods might try set it to
	            // in both cases use the smallest acceptable scale
	            index = LEGEND_SCALES.length - 1;
	        }
	        return LEGEND_SCALES[index];
	    }

	    private void renderLegend(PoseStack matrixStack, int mx, int my, String[] labels, String[] values, int left, int top, int lineHeight, int color) {
//	        float scale = getLegendScale();
	//
//	        RenderSystem.pushMatrix();
//	        RenderSystem.translatef(left + 3.75F * scale, top - lineHeight * (3.2F * scale), 0);
//	        RenderSystem.scalef(scale, scale, 1);
	//
//	        FontRenderer renderer = Minecraft.getInstance().font;
//	        int spacing = 0;
//	        for (String s : labels) {
//	            spacing = Math.max(spacing, renderer.width(s));
//	        }
	//
//	        float maxWidth = (width - 4) / scale;
//	        for (int i = 0; i < labels.length && i < values.length; i++) {
//	            String label = labels[i];
//	            String value = values[i];
	//
//	            while (value.length() > 0 && spacing + renderer.width(value) > maxWidth) {
//	                value = value.substring(0, value.length() - 1);
//	            }
	//
//	            drawString(matrixStack, renderer, label, 0, i * lineHeight, color);
//	            drawString(matrixStack, renderer, value, spacing, i * lineHeight, color);
//	        }
	//
//	        RenderSystem.popMatrix();
	//
//	        if (PreviewSettings.showCoords && !hoveredCoords.isEmpty()) {
//	            drawCenteredString(matrixStack, renderer, hoveredCoords, mx, my - 10, 0xFFFFFF);
//	        }
	    }
	//
//	    private int getZoom() {
//	        return NoiseUtil.round(1.5F * (101 - previewSettings.zoom));
//	    }
	//
//	    private static String getTerrainName(Cell cell) {
//	        if (cell.terrain.isRiver()) {
//	            return "river";
//	        }
//	        return cell.terrain.getName().toLowerCase();
//	    }
	//
//	    private static String getBiomeName(Cell cell) {
//	        String terrain = cell.terrain.getName().toLowerCase();
//	        if (terrain.contains("ocean")) {
//	            if (cell.temperature < 0.3) {
//	                return "cold_" + terrain;
//	            }
//	            if (cell.temperature > 0.6) {
//	                return "warm_" + terrain;
//	            }
//	            return terrain;
//	        }
//	        if (terrain.contains("river")) {
//	            return "river";
//	        }
//	        return cell.biome.name().toLowerCase();
//	    }
	}
}
