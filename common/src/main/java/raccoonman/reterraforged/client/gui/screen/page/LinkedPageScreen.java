package raccoonman.reterraforged.client.gui.screen.page;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.client.gui.widget.Label;

public abstract class LinkedPageScreen extends Screen {
	public Button previousButton,
				  nextButton,
				  cancelButton,
				  doneButton;
	protected Page currentPage;
	
	protected LinkedPageScreen() {
		super(CommonComponents.EMPTY);
	}
	
	public void setPage(Page page) {
		this.currentPage.onClose();
		this.currentPage = page;
		this.rebuildWidgets();
	}
	
	@Override
	public void init() {
		super.init();
		
		int buttonsCenter = this.width / 2;
        int buttonWidth = 50;
        int buttonHeight = 20;
        int buttonPad = 2;
        int buttonsRow = this.height - 25;
       
		this.previousButton = Button.builder(Component.literal("<<"), (b) -> {
			this.currentPage.previous().ifPresent(this::setPage);
		}).bounds(buttonsCenter - (buttonWidth * 2 + (buttonPad * 3)), buttonsRow, buttonWidth, buttonHeight).build();
		this.previousButton.active = this.currentPage.previous().isPresent();

		this.nextButton = Button.builder(Component.literal(">>"), (b) -> {
			this.currentPage.next().ifPresent(this::setPage);
		}).bounds(buttonsCenter + buttonWidth + (buttonPad * 3), buttonsRow, buttonWidth, buttonHeight).build();
		this.nextButton.active = this.currentPage.next().isPresent();
		
		this.cancelButton = Button.builder(CommonComponents.GUI_CANCEL, (b) -> {
			this.onClose();
		}).bounds(buttonsCenter - buttonWidth - buttonPad, buttonsRow, buttonWidth, buttonHeight).build();

		this.doneButton = Button.builder(CommonComponents.GUI_DONE, (b) -> {
			this.onDone();
			this.onClose();
		}).bounds(buttonsCenter + buttonPad, buttonsRow, buttonWidth, buttonHeight).build();
		
		this.currentPage.init();

		// these must be overlayed onto the current page
		this.addRenderableOnly(new Label(16, 15, 20, 20, this.currentPage.title()));

		this.addRenderableWidget(this.cancelButton);
		this.addRenderableWidget(this.doneButton);
		this.addRenderableWidget(this.previousButton);
		this.addRenderableWidget(this.nextButton);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		super.renderBackground(guiGraphics);
		super.render(guiGraphics, i, j, f);
	}
	
	@Override
	public void onClose() {
		this.currentPage.onClose();
	}
	
	public void onDone() {
		this.currentPage.onDone();
	}
	
	public interface Page {
		Component title();
		
		void init();
		
		Optional<Page> previous();
		
		Optional<Page> next();
		
		default void onClose() {
		}
		
		default void onDone() {
		}
	}
}
