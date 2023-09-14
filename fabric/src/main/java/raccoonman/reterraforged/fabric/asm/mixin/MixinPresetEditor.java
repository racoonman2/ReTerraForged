package raccoonman.reterraforged.fabric.asm.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import raccoonman.reterraforged.client.gui.screen.presetconfig.PresetConfigScreen;

// why the fuck does mojang hardcode everything??
@Deprecated 
@Mixin(PresetEditor.class)
interface MixinPresetEditor {
	
	// there has to be a better way to do this right?
	@Redirect(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"
		),
		remap = false
	)
	private static Map<Object, Object> of(Object k1, Object v1, Object k2, Object v2) {
		Map<Object, Object> map = new HashMap<>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(Optional.of(WorldPresets.NORMAL), (PresetEditor) (screen, ctx) -> new PresetConfigScreen(screen));
		return map;
    }
}
