package raccoonman.reterraforged.neoforge;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.RTFDataGen;
import raccoonman.reterraforged.platform.neoforge.RegistryUtilImpl;

@Mod(RTFCommon.MOD_ID)
public class RTFNeoForge {
	
    public RTFNeoForge(IEventBus modEventBus, ModContainer container) {
    	RTFCommon.bootstrap();

    	modEventBus.addListener(RTFNeoForge::gatherClientData);
    	RegistryUtilImpl.register(modEventBus);
    }

    private static void gatherClientData(GatherDataEvent.Client event) {
    	DataGenerator generator = event.getGenerator();
    	PackOutput output = generator.getPackOutput();
    	
    	DataGenerator.PackGenerator pack = generator.new PackGenerator(true, RTFCommon.MOD_ID, output);
    	RTFDataGen.generateResourcePack(pack);
    }
}