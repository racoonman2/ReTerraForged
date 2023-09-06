package raccoonman.reterraforged.common.client.gui.screen.element;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;

public class RTFCheckBox extends RTFButton implements Element {
    private boolean checked = false;

    public RTFCheckBox(Component display, boolean isChecked) {
        super(display);
        this.visible = true;
        this.width = 70;
        this.height = 20;
        checked = isChecked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void onClick(double x, double y) {
        super.onClick(x, y);
        checked = !checked;
    }

    @Override
    public void render(PoseStack matrix, int x, int y, float ticks) {
        active = !checked;
        super.render(matrix, x, y, ticks);
        active = true;
    }
}
