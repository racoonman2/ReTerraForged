package raccoonman.reterraforged.forge;

import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.client.event.RegisterPresetEditorsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import raccoonman.reterraforged.client.gui.screen.presetconfig.PresetConfigScreen;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.platform.registries.forge.RegistryUtilImpl;

@Mod(ReTerraForged.MOD_ID)
public final class ReTerraForgedForge {

    public ReTerraForgedForge() {
    	ReTerraForged.bootstrap();
    	
    	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    	modBus.addListener(ReTerraForgedForge::registerPresetEditors);
    	RegistryUtilImpl.register(modBus);
    }
    
    private static void registerPresetEditors(RegisterPresetEditorsEvent event) {
    	// TODO we probably shouldn't register this for the default preset
    	event.register(WorldPresets.NORMAL, (screen, ctx) -> new PresetConfigScreen(screen));
    }
}