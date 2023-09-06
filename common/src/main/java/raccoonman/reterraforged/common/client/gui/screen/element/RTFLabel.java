package raccoonman.reterraforged.common.client.gui.screen.element;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class RTFLabel extends RTFButton {
    private int color = 0xFFFFFF;

    public RTFLabel(Component text) {
        super(text);
        visible = true;
    }

    public RTFLabel(Component text, String toolTip, int color) {
        super(text, toolTip);
        this.color = color;
        this.visible = true;
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        fontrenderer.draw(matrix, getMessage().getString(), this.getX(), this.getY() + (height - 8) / 2F, color);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }
}
