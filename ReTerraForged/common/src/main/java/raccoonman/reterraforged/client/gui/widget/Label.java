package raccoonman.reterraforged.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class Label extends Button {
	private boolean playDownSound;

	public Label(int x, int y, int width, int height, Component component, boolean playDownSound) {
    	super(x, y, width, height, component, (v) -> {}, DEFAULT_NARRATION);

    	this.playDownSound = playDownSound;
	}
	
	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		
		int widgetX = this.getX();
		int widgetY = this.getY();
		int widgetWidth = this.getWidth();
		int widgetHeight = this.getHeight();
		int margin = 2;
		int textX = widgetX + margin;
		int textY = widgetY + widgetWidth - margin;
		int color = ARGB.color(this.alpha, this.active ? -1 : -6250336);
		renderScrollingString(graphics, minecraft.font, this.getMessage(), textX, textX, widgetY, textY, widgetY + widgetHeight, color);
	}

	@Override
    public void playDownSound(SoundManager soundManager) {
		if(this.playDownSound) {
			super.playDownSound(soundManager);
		}
    }
}
