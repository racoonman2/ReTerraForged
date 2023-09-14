package raccoonman.reterraforged.client.gui.screen.presetconfig;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.WorldDimensions;
import raccoonman.reterraforged.client.gui.screen.page.LinkedPageScreen;
import raccoonman.reterraforged.common.data.preset.Preset;

public class PresetConfigScreen extends LinkedPageScreen {
	private CreateWorldScreen parent;
	
	public PresetConfigScreen(CreateWorldScreen parent) {
		this.parent = parent;
		this.currentPage = new SelectPresetPage(this);
	}
	
	@Override
	public void onClose() {
		super.onClose();

		this.minecraft.setScreen(this.parent);
	}
	
	public RegistryAccess.Frozen getRegistryAccess() {
		return this.parent.getUiState().getSettings().worldgenLoadContext();
	}
	
	public WorldDimensions getDimensions() {
		return this.parent.getUiState().getSettings().selectedDimensions();
	}

	public void applyPreset(Preset preset) {
		System.out.println("applying preset!");
	}
}
