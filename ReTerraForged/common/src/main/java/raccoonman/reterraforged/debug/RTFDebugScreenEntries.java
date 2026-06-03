package raccoonman.reterraforged.debug;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.RTFCommon;

public class RTFDebugScreenEntries {
	public static final ResourceLocation LAYER = register("layer", new DebugEntryLayer());
	
	public static void bootstrap() {
	}
	
	private static ResourceLocation register(String name, DebugScreenEntry entry) {
		ResourceLocation location = RTFCommon.location(name);
		return DebugScreenEntries.register(location, entry);
	}
}
