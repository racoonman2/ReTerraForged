package raccoonman.reterraforged.platform;

import com.mojang.serialization.MapCodec;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Registry;

public class LithostitchedUtil {
	
	@ExpectPlatform
	public static Registry<MapCodec<? extends Modifier>> getModifierTypeRegistry() {
		throw new IllegalStateException();
	}
}
