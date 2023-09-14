package raccoonman.reterraforged.client.gui.screen.presetconfig;

import java.io.IOException;

import net.minecraft.client.gui.components.AbstractWidget;
import raccoonman.reterraforged.client.gui.screen.page.BisectedPage;
import raccoonman.reterraforged.client.gui.screen.presetconfig.SelectPresetPage.PresetEntry;

abstract class PresetEditorPage extends BisectedPage<PresetConfigScreen, AbstractWidget, AbstractWidget> {
	protected PresetEntry preset;
	
	public PresetEditorPage(PresetConfigScreen screen, PresetEntry preset) {
		super(screen);
		
		this.preset = preset;
	}
	
	@Override
	public void close() {
		super.close();
	
		try {
			this.preset.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
