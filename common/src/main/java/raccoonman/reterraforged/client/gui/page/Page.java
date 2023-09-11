package raccoonman.reterraforged.client.gui.page;

import net.minecraft.network.chat.Component;

public interface Page {
	Component title();
	
	void init();
}