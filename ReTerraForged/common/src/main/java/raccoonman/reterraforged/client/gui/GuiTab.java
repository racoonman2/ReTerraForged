package raccoonman.reterraforged.client.gui;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;

public class GuiTab implements Tab, Tickable {
	private Component title;
	private Consumer<AbstractWidget> widgetAdder;
	private Consumer<AbstractWidget> widgetRemover;
	private Consumer<Consumer<AbstractWidget>> bottomButtonCallback;
	private Gui gui;
	private ScreenRectangle area;
	
	private boolean addedComponents;
	
	public GuiTab(Component title, Consumer<AbstractWidget> widgetAdder, Consumer<AbstractWidget> widgetRemover, Consumer<Consumer<AbstractWidget>> bottomButtonCallback, Gui gui) {
		this.title = title;
		this.widgetAdder = widgetAdder;
		this.widgetRemover = widgetRemover;
		this.bottomButtonCallback = bottomButtonCallback;
		this.gui = gui;
	}
	
	public Gui getGui() {
		return this.gui;
	}
	
	public void setGui(Gui gui) {
		this.gui.visitChildren(this.widgetRemover);
		this.gui = gui;	
		this.gui.init(this.area);
		this.gui.visitChildren(this.widgetAdder);
	}
	
	public void rebuild() {
		this.gui.rebuild(this.area, this.widgetAdder, this.widgetRemover);
	}
	
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.gui.render(graphics, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public Component getTabTitle() {
		return this.title;
	}

	@Override
	public void visitChildren(Consumer<AbstractWidget> consumer) {
		if(this.addedComponents) {
			this.gui.visitChildren(this.widgetRemover);
			this.bottomButtonCallback.accept(this.widgetAdder::accept);
		} else {
			this.bottomButtonCallback.accept(this.widgetRemover::accept);
		}
		this.addedComponents = !this.addedComponents;
	}

	@Override
	public void doLayout(ScreenRectangle area) {
		this.area = area;
		this.rebuild();
	}
	
	@Override
	public void tick() {
		this.gui.tick();
	}
	
	public void close() {
		this.gui.close();
	}

	//TODO
	@Override
	public Component getTabExtraNarration() {
		return Component.empty();
	}
}
