package raccoonman.reterraforged.common.client.gui.screen.overlay;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;

public interface OverlayRenderer {
	void renderOverlays(PoseStack matrixStack, Screen screen, int mouseX, int mouseY);
}
