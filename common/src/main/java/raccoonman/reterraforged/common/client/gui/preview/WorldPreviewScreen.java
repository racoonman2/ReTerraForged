package raccoonman.reterraforged.common.client.gui.preview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFRegistries;

// TODO finish this (also improve zoom)
public class WorldPreviewScreen extends Screen {
	private static final int MAX_SCALE = 250;
	
	private int seed;
	private CreateWorldScreen parent;
	private DynamicTexture framebuffer;
	private Layer currentLayer;
	private final List<Layer> layers;
	private int scale;
	
	public WorldPreviewScreen(CreateWorldScreen parent, WorldCreationContext ctx) {
		this(parent, ctx.worldgenLoadContext(), ctx.options().seed());
	}
	
	public WorldPreviewScreen(CreateWorldScreen parent, RegistryAccess registryAccess, long seed) {
		super(Component.translatable("createWorld.customize.reterraforged.title"));
		this.seed = (int) seed;
		this.parent = parent;
		this.layers = new ArrayList<>(registryAccess.lookupOrThrow(RTFRegistries.NOISE).listElements().map(NoiseLayer::new).toList());
		this.framebuffer = new DynamicTexture(256, 256, false);
		this.scale = 15;
		RandomState randomState = RandomState.create(registryAccess.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(NoiseGeneratorSettings.OVERWORLD).value(), registryAccess.lookupOrThrow(Registries.NOISE), seed);		
		this.layers.add(new DensityLayer(Component.literal("minecraft:ridges"), randomState.router().ridges(), DensityLayer.View.XZ));
		this.layers.add(new DensityLayer(Component.literal("minecraft:ridges_folded"), peaksAndValleys(randomState.router().ridges()), DensityLayer.View.XZ));
		this.layers.add(new DensityLayer(Component.literal("minecraft:temperature"), randomState.router().temperature(), DensityLayer.View.XZ));
		this.layers.add(new DensityLayer(Component.literal("minecraft:vegetation"), randomState.router().vegetation(), DensityLayer.View.XZ));
		this.layers.add(new DensityLayer(Component.literal("minecraft:continents"), randomState.router().continents(), DensityLayer.View.XZ));
		this.layers.add(new DensityLayer(Component.literal("minecraft:initial_density_without_jaggedness"), randomState.router().initialDensityWithoutJaggedness(), DensityLayer.View.XY));
		this.layers.add(new DensityLayer(Component.literal("minecraft:final_density"), randomState.router().finalDensity(), DensityLayer.View.XY));
		 
		this.setLayer(this.layers.get(0));
	}
	
	// we have to copy this from NoiseRouterData cause the AW won't work
	// TODO fix the AW
	private static DensityFunction peaksAndValleys(DensityFunction densityFunction) {
		return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(densityFunction.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)), DensityFunctions.constant(-3.0));
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
							
				            pixels.setPixelRGBA(tx, ty, layer.sampleColor(tx * scale, ty * scale, this.seed) | 255 << pixels.format().alphaOffset());
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
			CycleButton.builder(Layer::name)
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
			RenderSystem.colorMask(true, true, true, false);
			blit(stack, x, y, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
			RenderSystem.colorMask(true, true, true, true);
			
			if(this.isMouseOver(mouseX, mouseY)) {
				int relativeMouseX = (mouseX - this.getX()) * WorldPreviewScreen.this.scale;
				int relativeMouseY = (mouseY - this.getY()) * WorldPreviewScreen.this.scale;

				drawString(stack, WorldPreviewScreen.this.font, "x: " + relativeMouseX, x + 8, y + this.getWidth() - 26, 16777215);	
				drawString(stack, WorldPreviewScreen.this.font, "y: " + relativeMouseY, x + 8, y + this.getWidth() - 36, 16777215);	
				drawString(stack, WorldPreviewScreen.this.font, String.format("noise: %.3f", WorldPreviewScreen.this.currentLayer.sampleNoise(relativeMouseX, relativeMouseY, this.seed)), x + 8, y + this.getWidth() - 16, 16777215);	
			}
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput narration) {
			// TODO
		}
	}
    
	private interface Layer {
		float sampleNoise(float x, float y, int seed);
		
		int sampleColor(float x, float y, int seed);
		
		Component name();
		
		public interface Contrast extends Layer {
			float minValue();
			
			float maxValue();
			
			@Override
			default int sampleColor(float x, float y, int seed) {
				float range = (float) (this.maxValue() - this.minValue());
				float mapped = (this.sampleNoise(x, y, seed) + (range / 2.0F)) / (range);
				int color = (int) Math.floor(255 * mapped);
				return color + (color << 8) + (color << 16) + (255 << 24);
			}			
		}
	}
	
	private record NoiseLayer(Holder<Noise> holder) implements Layer.Contrast {

		@Override
		public float sampleNoise(float x, float y, int seed) {
			return this.holder.value().getValue(x, y, seed);
		}

		@Override
		public Component name() {
			return Component.literal(this.holder().unwrapKey().map((key) -> {
				return key.location().toString();
			}).orElse("[Inlined]"));
		}

		@Override
		public float minValue() {
			return this.holder.value().minValue();
		}

		@Override
		public float maxValue() {
			return this.holder.value().maxValue();
		}
	}
	
	private static record DensityLayer(Component name, DensityFunction function, View view) implements Layer.Contrast {
		
		@Override
		public float sampleNoise(float x, float y, int seed) {
			return this.view.sample(this.function, x, y, seed);
		}

		@Override
		public float minValue() {
			return (float) this.function.minValue();
		}

		@Override
		public float maxValue() {
			return (float) this.function.maxValue();
		}
		
		public enum View {
			XZ {
				@Override
				public float sample(DensityFunction function, float x, float y, int seed) {
					return (float) function.compute(new DensityFunction.SinglePointContext((int) x, 0, (int) y));
				}
			},
			XY {
				@Override
				public float sample(DensityFunction function, float x, float y, int seed) {
					return (float) function.compute(new DensityFunction.SinglePointContext((int) x, (int) y, 0));
				}
			};
			
			public abstract float sample(DensityFunction function, float x, float y, int seed);
		}
	}
}
