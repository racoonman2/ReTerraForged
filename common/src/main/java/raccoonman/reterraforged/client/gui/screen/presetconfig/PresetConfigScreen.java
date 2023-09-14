package raccoonman.reterraforged.client.gui.screen.presetconfig;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;

public class PresetConfigScreen extends LinkedPageScreen {
	private CreateWorldScreen parent;
	
	public PresetConfigScreen(CreateWorldScreen parent, WorldCreationContext settings) {
		this.parent = parent;
		this.currentPage = new SelectPresetPage(this);
	}
	
	@Override
	public void onClose() {
		super.onClose();

		this.minecraft.setScreen(this.parent);
	}
}
