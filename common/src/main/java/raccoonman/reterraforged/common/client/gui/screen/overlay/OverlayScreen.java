package raccoonman.reterraforged.common.client.gui.screen.overlay;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.common.client.gui.screen.element.Element;

public class OverlayScreen extends Screen implements OverlayRenderer {

	public OverlayScreen() {
		super(Component.translatable(""));
		super.minecraft = Minecraft.getInstance();
		super.font = minecraft.font;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
//		if (PreviewSettings.showTooltips) {
			renderOverlays(matrixStack, this, mouseX, mouseY);
//		}
	}

	@Override
	public void renderOverlays(PoseStack matrixStack, Screen screen, int mouseX, int mouseY) {
		for (GuiEventListener button : children()) {
			if (button.isMouseOver(mouseX, mouseY)) {
				if (button instanceof Element) {
					Element element = (Element) button;
					if (!element.getTooltip().isEmpty()) {
						screen.renderTooltip(matrixStack, element.getToolTipText(), mouseX, mouseY);
					}
					return;
				}
			}
		}
	}

	@Override
	protected void init() {
//		addButton(new RTFCheckBox(GuiKeys.TOOLTIPS.get(), PreviewSettings.showTooltips) {
//			@Override
//			public void onClick(double mouseX, double mouseY) {
//				super.onClick(mouseX, mouseY);
//				PreviewSettings.showTooltips = isChecked();
//				config.set("tooltips", PreviewSettings.showTooltips);
//				config.save();
//			}
//
//			@Override
//			public void render(PoseStack matrixStack, int mouseX, int mouseY, float partial) {
//				this.x = OverlayScreen.this.width - width - 13;
//				this.y = 6;
//				super.render(matrixStack, mouseX, mouseY, partial);
//			}
//		});
//
//		addButton(new RTFCheckBox("coords", PreviewSettings.showCoords) {
//			@Override
//			public void onClick(double mouseX, double mouseY) {
//				super.onClick(mouseX, mouseY);
//				PreviewSettings.showCoords = isChecked();
//				config.set(GuiKeys.COORDS_KEY, PreviewSettings.showCoords);
//				config.save();
//			}
//
//			@Override
//			public void render(PoseStack matrixStack, int mouseX, int mouseY, float partial) {
//				setChecked(PreviewSettings.showCoords);
//				this.x = OverlayScreen.this.width - (width * 2) - 15;
//				this.y = 6;
//				super.render(matrixStack, mouseX, mouseY, partial);
//			}
//		});
	}
}
