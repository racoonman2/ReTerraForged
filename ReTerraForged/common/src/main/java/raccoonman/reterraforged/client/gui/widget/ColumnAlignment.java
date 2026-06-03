package raccoonman.reterraforged.client.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class ColumnAlignment {
	private int width;
	private int height;
	private int horizontalPadding;
	private int horizontalMargin; 
	private int left;
	
	public ColumnAlignment(int width, int height, int horizontalPadding, int horizontalMargin) {
		this.width = width;
		this.height = height;
		this.horizontalPadding = horizontalPadding;
		this.horizontalMargin = horizontalMargin;
		this.left = horizontalMargin;
	}
	
	public <A extends AbstractWidget> A addColumn(float columnSize, ColumnFactory<A> factory) {
		int pageWidth = this.width - (this.horizontalMargin * 2);
		int height = this.height;
		int columnWidth = Math.max(0, Math.round(columnSize * pageWidth) - (2 * this.horizontalPadding));
		A column = factory.apply(this.left, columnWidth, height);
		this.left += columnWidth > 0 ? columnWidth + (2 * this.horizontalPadding) : 0;
		return column;
	}
	
	public interface ColumnFactory<A extends GuiEventListener> {
		A apply(int left, int columnWidth, int height);
	}
}
