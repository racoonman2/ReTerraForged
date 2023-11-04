package raccoonman.reterraforged.client.gui.widget;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class Label extends Button {
    
	public Label(int x, int y, int width, int height, Component component) {
    	this(x, y, width, height, (b) -> {}, component);
	}
	
	public Label(int x, int y, int width, int height, OnPress onPress, Component component) {
    	super(x, y, width, height, component, onPress, Supplier::get);
	}

	@Override
    public void playDownSound(SoundManager soundManager) {
    }

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		guiGraphics.drawString(minecraft.gui.getFont(), this.getMessage(), this.getX(), (int) (this.getY() + (this.height - 8.0F) / 2.0F), 0xFFFFFF);
	}
}
