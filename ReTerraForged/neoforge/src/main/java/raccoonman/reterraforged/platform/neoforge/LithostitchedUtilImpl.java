package raccoonman.reterraforged.platform.neoforge;

import com.mojang.serialization.MapCodec;

import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Registry;

public class LithostitchedUtilImpl {
	
	public static Registry<MapCodec<? extends Modifier>> getModifierTypeRegistry() {
		return LithostitchedBuiltInRegistries.MODIFIER_TYPE;
	}
}
