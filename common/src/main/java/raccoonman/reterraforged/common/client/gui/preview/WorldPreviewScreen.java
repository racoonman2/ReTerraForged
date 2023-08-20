package raccoonman.reterraforged.common.client.gui.preview;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunctions;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.common.noise.Noise;

// TODO finish this (also make zoom better)
public class WorldPreviewScreen extends Screen {
	private static final int MAX_SCALE = 250;
	
	private int seed;
	private CreateWorldScreen parent;
	private DynamicTexture framebuffer;
	private Layer currentLayer;
	private final List<Layer> layers;
	private int scale;
	
	public WorldPreviewScreen(int seed, CreateWorldScreen parent, RegistryLookup<Noise> noiseGetter) {
		super(Component.translatable("createWorld.customize.reterraforged.title"));
		this.seed = seed;
		this.parent = parent;
		this.layers = noiseGetter.listElements().map((ref) -> {
			return new Layer(ref, Float2IntFunctions.primitive((value) -> {
				return rgba(value, value, value);//MathUtil.step(1 - value, 8) * 0.65F, saturation, brightness);
			}));
		}).toList();
		this.framebuffer = new DynamicTexture(256, 256, false);
		this.scale = 15;
				
		this.setLayer(this.layers.get(0));
	}

	@Override
	public void onClose() {
		this.framebuffer.close();
		
		this.minecraft.setScreen(this.parent);
	}
	
	public void setLayer(Layer layer) {
		this.currentLayer = layer;
		
		this.rebuildFramebuffer(this.scale, this.framebuffer, this.currentLayer).join();
	}
	
	private CompletableFuture<Void> rebuildFramebuffer(int scale, DynamicTexture framebuffer, Layer layer) {
		final int cellCount = 16;
		NativeImage pixels = framebuffer.getPixels();
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
							
				            pixels.setPixelRGBA(tx, ty, layer.color.applyAsInt(layer.noise.value().getValue(tx * scale, ty * scale, this.seed)));
						}
					}
				}, Util.backgroundExecutor());
			}
		}
		return CompletableFuture.allOf(futures).thenRun(framebuffer::upload);
	}
	
	@Override
	protected void init() {		
		this.addRenderableWidget(new FramebufferWidget(this.framebuffer, this.width / 2 - 128, this.height - 512 + 64, 256, 256, Component.empty(), this.seed));
		this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 128 + 16, this.height - 256 + 64 + 7, 256 - 32, 20, CommonComponents.EMPTY, (float) this.scale / MAX_SCALE) {
	         {
	            this.updateMessage();
	         }

	         @Override
	         protected void updateMessage() {
	            this.setMessage(Component.translatable("createWorld.customize.reterraforged.world_preview.zoom").append(": ").append(String.valueOf((float) (MAX_SCALE - WorldPreviewScreen.this.scale) / MAX_SCALE)));
	         }

	         @Override
	         protected void applyValue() {
	        	 WorldPreviewScreen.this.scale = (int) ((1 - this.value) * MAX_SCALE);
	        	 
	        	 WorldPreviewScreen.this.rebuildFramebuffer(WorldPreviewScreen.this.scale, WorldPreviewScreen.this.framebuffer, WorldPreviewScreen.this.currentLayer).join();
	         }
		});

		this.addRenderableWidget(
			CycleButton.builder((Layer layer) -> Component.literal(layer.noise.key().location().toString()))
				.withValues(this.layers.toArray(Layer[]::new))
				.withInitialValue(this.currentLayer)
				.create(this.width / 2 - 128 + 16, this.height - 256 + 64 + 35, 256 - 32, 20, Component.translatable("createWorld.customize.reterraforged.world_preview.layer"), (button, layer) -> {
					this.setLayer(layer);
				})
		);
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			this.minecraft.setScreen(this.parent);
		}).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(this.parent);
		}).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(stack);
		super.render(stack, mouseX, mouseY, partialTick);
		drawCenteredString(stack, this.font, this.title, this.width / 2, 8, 16777215);
	}
	
	// this looks kinda messy being here but oh well
	private static int rgba(float h, float s, float b) {
        int argb = Color.HSBtoRGB(h, s, b);
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue =  argb & 0xFF;
        return rgba(red, green, blue);
    }

    private static int rgba(int r, int g, int b) {
        return r + (g << 8) + (b << 16) + (255 << 24);
    }
    
	private record Layer(Holder.Reference<Noise> noise, Float2IntFunction color) {
	}
	
	private class FramebufferWidget extends AbstractWidget {
		private int seed;
		
		public FramebufferWidget(AbstractTexture framebuffer, int x, int y, int w, int h, Component message, int seed) {
			super(x, y, w, h, message);

			this.seed = seed;
		}

		@Override
		public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTick) {
			int x = this.getX();
			int y = this.getY();
			
			RenderSystem.setShaderTexture(0, WorldPreviewScreen.this.framebuffer.getId());
			blit(stack, x, y, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
			
			if(this.isMouseOver(mouseX, mouseY)) {
				int relativeMouseX = (mouseX - this.getX()) * WorldPreviewScreen.this.scale;
				int relativeMouseY = (mouseY - this.getY()) * WorldPreviewScreen.this.scale;
				
				drawString(stack, WorldPreviewScreen.this.font, "noise: " + WorldPreviewScreen.this.currentLayer.noise.value().getValue(relativeMouseX, relativeMouseY, this.seed), x + 8, y + this.getWidth() - 16, 16777215);	
				drawString(stack, WorldPreviewScreen.this.font, "x: " + relativeMouseX, x + 8, y + this.getWidth() - 26, 16777215);	
				drawString(stack, WorldPreviewScreen.this.font, "y: " + relativeMouseY, x + 8, y + this.getWidth() - 36, 16777215);	
			}
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput narration) {
			// TODO
		}
	}
}
