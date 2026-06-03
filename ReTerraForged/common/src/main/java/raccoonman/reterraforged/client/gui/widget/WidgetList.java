package raccoonman.reterraforged.client.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import raccoonman.reterraforged.client.gui.preset.WorldPreview;

public class WidgetList<A extends AbstractWidget> extends ContainerObjectSelectionList<WidgetList.Entry<A>> {
	private int optionWidth;
	private int padding;
	private boolean renderSelected;
	
	@Nullable
	private SelectionCallback<A> selectionCallback;
	
    public WidgetList(Minecraft minecraft, int x, int y, int width, int height, int optionHeight) {
        super(minecraft, width, height, y, optionHeight);
        
        int maxOptionWidth = 396;
        int rowWidth = this.getRowWidth();
        this.optionWidth = Math.min(maxOptionWidth, rowWidth);
    	this.padding = (rowWidth - this.optionWidth) / 2;
    	
    	this.setX(x);
    }
    
	@Override
	public boolean mouseScrolled(double d, double e, double f, double g) {
		WidgetList.Entry<A> focused = this.getFocused();
		if(focused != null && focused.widget instanceof WorldPreview preview && preview.mouseScrolled(d, e, f, g)) {
			return true;
		}
		return super.mouseScrolled(d, e, f, g);
	}

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
	protected boolean entriesCanBeSelected() {
		return this.renderSelected;
	}

    @Override
    public int getRowWidth() {
        return this.width - 20;
    }

    @Override
    protected int scrollBarX() {
        return this.getRowRight();
    }

    @Override
    public void clearEntries() {
    	super.clearEntries();
    }
    
    @Override
    public void removeEntry(WidgetList.Entry<A> entry) {
    	super.removeEntry(entry);
    }

    @Override
	public void setSelected(@Nullable WidgetList.Entry<A> entry) {
		super.setSelected(entry);
		
		if(this.selectionCallback != null) {
			this.selectionCallback.accept(entry);
		}
	}

    public void setRenderSelected(boolean renderSelected) {
    	this.renderSelected = renderSelected;
    }
    
    public void setSelectionCallback(SelectionCallback<A> callback) {
    	this.selectionCallback = callback;
    }
    
    public <W extends A> W add(WidgetFactory<W> factory) {
		int x = this.getRowLeft() + this.padding;
		int y = this.getNextY();
		int width = this.optionWidth;
		int padding = 5;
		
    	W widget = factory.makeWidget(x, y, width, this.defaultEntryHeight - padding);
        super.addEntry(new WidgetList.Entry<>(widget), widget.height + padding);
        return widget;
    }
    
	public OptionalInt getIndex(WidgetList.Entry<A> entry) {
		List<WidgetList.Entry<A>> children = this.children();
		for(int i = 0; i < children.size(); i++) {
			if(children.get(i).equals(entry)) {
				return OptionalInt.of(i);
			}
		}
		return OptionalInt.empty();
	}
    
    public Optional<WidgetList.Entry<A>> getEntry(A widget) {
    	return this.children().stream().filter((entry) -> entry.getWidget().equals(widget)).findFirst();
    }
    
    public interface SelectionCallback<A extends AbstractWidget> {
    	void accept(WidgetList.Entry<A> entry);
    }
    
	public interface WidgetFactory<A extends AbstractWidget> {
		A makeWidget(int x, int y, int width, int height);
	}
	
    public static class Entry<A extends AbstractWidget> extends ContainerObjectSelectionList.Entry<Entry<A>> {
        private A widget;

        public Entry(A widget) {
            this.widget = widget;
        }

        public A getWidget() {
        	return this.widget;
        }
        
        @Override
        public void setY(int y) {
        	this.widget.setY(y);

        	super.setY(y);
        }

        @Override
        public List<A> children() {
        	return Collections.singletonList(this.widget);
        }

		@Override
		public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            this.widget.render(guiGraphics, mouseX, mouseY, partialTicks);
		}

		@Override
		public List<A> narratables() {
			return Collections.singletonList(this.widget);
		}
    }
}
