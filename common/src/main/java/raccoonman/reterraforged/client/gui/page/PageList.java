package raccoonman.reterraforged.client.gui.page;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class PageList {
	private List<Page> pages;
	private int pageIndex;
	
	public PageList(Page... pages) {
		this.pages = ImmutableList.copyOf(pages);
	}
	
	public boolean atBottom() {
		return this.pageIndex <= 0;
	}
	
	public boolean atTop() {
		return this.pageIndex >= this.pages.size() - 1;
	}
	
	public Page init() {
		Page current = this.pages.get(this.pageIndex);
		current.init();
		return current;
	}
			
	public Page next() {
		if(!this.atTop()) {
			return this.pages.get(this.pageIndex++);
		} else {
			return this.pages.get(this.pages.size() - 1);
		}
	}
	
	public Page previous() {
		if(!this.atBottom()) {
			return this.pages.get(this.pageIndex--);
		} else {
			return this.pages.get(0);
		}
	}
}