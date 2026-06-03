package raccoonman.reterraforged.client.gui;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public interface Gui {
	Component getTitle();
	
	void init(ScreenRectangle area);

	void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);
	
	void tick();

	void visitChildren(Consumer<AbstractWidget> visitor);

	void close();
	
	default void rebuild(ScreenRectangle area, Consumer<AbstractWidget> adder, Consumer<AbstractWidget> remover) {
		this.visitChildren(remover);
		this.init(area);
		this.visitChildren(adder);
	}
}
