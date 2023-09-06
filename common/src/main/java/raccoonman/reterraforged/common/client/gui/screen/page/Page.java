package raccoonman.reterraforged.common.client.gui.screen.page;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import raccoonman.reterraforged.common.client.gui.screen.ScrollPane;
import raccoonman.reterraforged.common.client.gui.screen.element.Element;
import raccoonman.reterraforged.common.client.gui.screen.element.RTFLabel;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayRenderer;
import raccoonman.reterraforged.common.client.gui.screen.overlay.OverlayScreen;

public abstract class Page implements GuiEventListener, OverlayRenderer {

	protected static final Runnable NO_CALLBACK = () -> {
	};

	private static final int SLIDER_HEIGHT = 20;
	private static final int SLIDER_PAD = 2;

	private final Column[] columns;
	private final float[] sizes;
	private final int hpad;
	private final int vpad;
	protected OverlayScreen parent;

	public Page(int hpad, int vpad, float... columnSizes) {
		this.hpad = hpad;
		this.vpad = vpad;
		this.sizes = columnSizes;
		this.columns = new Column[columnSizes.length];
	}

	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}

	public abstract void callback(Runnable runnable);

	public abstract void save();

	public abstract void init(OverlayScreen parent);

	@Override
	public void renderOverlays(PoseStack matrixStack, Screen screen, int mouseX, int mouseY) {
		for (Column column : columns) {
			if (column.scrollPane.children().isEmpty()) {
				continue;
			}
			column.scrollPane.renderOverlays(matrixStack, screen, mouseX, mouseY);
		}
	}

	public void visit(Consumer<ScrollPane> consumer) {
		for (Column column : columns) {
			if (column.scrollPane.children().isEmpty()) {
				continue;
			}
			consumer.accept(column.scrollPane);
		}
	}

	public boolean action(Function<ScrollPane, Boolean> action) {
		boolean result = false;
		for (Column column : columns) {
			if (column.scrollPane.children().isEmpty()) {
				continue;
			}
			boolean b = action.apply(column.scrollPane);
			result = b || result;
		}
		return result;
	}

	public void close() {

	}

	public Component getTitle() {
		return Component.empty();
	}

	public Column getColumn(int index) {
		return columns[index];
	}

	public final void initPage(int marginH, int marginV, OverlayScreen parent) {
		this.parent = parent;
		int top = marginV;
		int left = marginH;
		int pageWidth = parent.width - (marginH * 2);
		int pageHeight = parent.height;
		for (int i = 0; i < columns.length; i++) {
			int columnWidth = Math.max(0, Math.round(sizes[i] * pageWidth) - (2 * hpad));
			Column column = new Column(left, top, columnWidth, pageHeight, hpad, vpad);
			columns[i] = column;
			left += columnWidth > 0 ? columnWidth + (2 * hpad) : 0;
		}
		init(parent);
	}

	public void addElements(int x, int y, Column column, CompoundTag settings, Consumer<AbstractWidget> consumer,
			Runnable callback) {
		addElements(x, y, column, settings, false, consumer, callback);
	}

	public void addElements(int x, int y, Column column, CompoundTag settings, boolean deep, Consumer<AbstractWidget> consumer,
			Runnable callback) {
		// Don't add if element is hidden
		if (skip(settings)) {
			return;
		}

		AtomicInteger top = new AtomicInteger(y);
		AbstractWidget button = new RTFLabel(Component.literal("setting"));// createButton(name, settings, callback);
		if (button != null) {
			button.setWidth(column.width);
			button.height = SLIDER_HEIGHT;
			button.setX(x);
			button.setY(top.getAndAdd(SLIDER_HEIGHT + SLIDER_PAD));
			consumer.accept(button);
			onAddWidget(button);
		}
	}

//	public AbstractWidget createButton(String name, CompoundTag value, Runnable callback) {
//		Tag tag = value.get(name);
//		if (tag == null) {
//			return null;
//		}
//
//		byte type = tag.getId();
//		if (type == Tag.TAG_INT) {
//			if (isRand(name, value)) {
//				return new RTFRandButton(name, value).callback(callback);
//			}
//			if (hasLimit(name, value)) {
//				return new RTFSlider.BoundInt(name, value).callback(callback);
//			}
//			return new RTFSlider.Int(name, value).callback(callback);
//		} else if (type == Constants.NBT.TAG_FLOAT) {
//			if (hasLimit(name, value)) {
//				return new RTFSlider.BoundFloat(name, value).callback(callback);
//			}
//			return new RTFSlider.Float(name, value).callback(callback);
//		} else if (hasOptions(name, value)) {
//			return new RTFToggle(name, value).callback(callback);
//		} else if (type == Constants.NBT.TAG_STRING) {
//			return new RTFTextBox(name, value);
//		} else {
//			return null;
//		}
//	}

	public AbstractWidget createLabel(String name, CompoundTag settings) {
		if (settings.getCompound("#" + name).contains("noname")) {
			return null;
		}
		return new RTFLabel(Component.literal(Element.getDisplayName(name, settings)));
	}

	public void onAddWidget(AbstractWidget widget) {

	}

	public static class Column {

		public final int left;
		public final int right;
		public final int top;
		public final int bottom;
		public final int width;
		public final int height;
		public final ScrollPane scrollPane;

		private Column(int left, int top, int width, int height, int vpad, int hpad) {
			this.left = left + vpad;
			this.right = left + width - vpad;
			this.top = top + hpad;
			this.bottom = height - hpad;
			this.width = width;
			this.height = height;
			this.scrollPane = new ScrollPane(25);
			this.scrollPane.updateSize(width, height, 30, height - 30);
			this.scrollPane.setLeftPos(this.left);
		}
	}

	private static boolean skip(CompoundTag value) {
		Tag tag = value.get("#hide");
		return tag instanceof ByteTag && ((ByteTag) tag).getAsByte() == 1;
	}

	private static boolean hasOptions(String name, CompoundTag value) {
		return value.getCompound("#" + name).contains("options");
	}

	private static boolean hasLimit(String name, CompoundTag value) {
		String key = "#" + name;
		return value.getCompound(key).contains("limit_lower")
				|| value.getCompound(key).contains("limit_upper");
	}

	private static boolean isRand(String name, CompoundTag value) {
		return value.getCompound("#" + name).contains("random");
	}
}
