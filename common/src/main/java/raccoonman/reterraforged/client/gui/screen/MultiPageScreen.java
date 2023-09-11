package raccoonman.reterraforged.client.gui.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.gui.widget.Label;

public abstract class MultiPageScreen extends Screen {
	private List<Page> pages;
	private int pageIndex;
	protected Button previous;
	protected Button next;
	protected Button cancel;
	protected Button done;
	
	protected MultiPageScreen(Component component) {
		super(component);
		
		this.pages = new ArrayList<>();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void init() {
		super.init();
		
		int buttonsCenter = this.width / 2;
        int buttonWidth = 50;
        int buttonHeight = 20;
        int buttonPad = 2;
        int buttonsRow = this.height - 25;
       
		this.previous = Button.builder(Component.literal("<<"), (b) -> {
			this.setPageIndex(this.pageIndex - 1);
		}).bounds(buttonsCenter - (buttonWidth * 2 + (buttonPad * 3)), buttonsRow, buttonWidth, buttonHeight).build();
		this.previous.active = this.hasPrevious();
		this.next = Button.builder(Component.literal(">>"), (b) -> {
			this.setPageIndex(this.pageIndex + 1);
		}).bounds(buttonsCenter + buttonWidth + (buttonPad * 3), buttonsRow, buttonWidth, buttonHeight).build();
		this.next.active = this.hasNext();
		this.cancel = Button.builder(CommonComponents.GUI_CANCEL, (b) -> {
			this.onClose();
		}).bounds(buttonsCenter - buttonWidth - buttonPad, buttonsRow, buttonWidth, buttonHeight).build();
		this.done = Button.builder(CommonComponents.GUI_DONE, (b) -> {
			this.onDone();
		}).bounds(buttonsCenter + buttonPad, buttonsRow, buttonWidth, buttonHeight).build();
		
		Page current = this.pages.get(this.pageIndex);
		current.init();

		this.addRenderableOnly(new Label(16, 15, 20, 20, current.title()));

		this.addRenderableWidget(this.cancel);
		this.addRenderableWidget(this.done);
		this.addRenderableWidget(this.previous);
		this.addRenderableWidget(this.next);
	}
	
	protected boolean hasPrevious() {
		return this.pageIndex > 0;
	}
	
	protected boolean hasNext() {
		return this.pageIndex + 1 < this.pages.size();
	}
	
	protected void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
		
		this.rebuildWidgets();
	}
	
	protected abstract void onDone();
	
	protected void addPage(Page page) {
		this.pages.add(page);
	}
	
	public interface Page {
		Component title();
		
		void init();
	}
}
