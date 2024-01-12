package raccoonman.reterraforged.forge.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.server.ServerLifecycleHooks;
import raccoonman.reterraforged.registries.RTFRegistries;

@Mixin(ServerLifecycleHooks.class)
public class MixinServerLifecycleHooks {

	@ModifyVariable(
		method = "runModifiers",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/stream/Stream;toList()Ljava/util/List;",
			shift = Shift.AFTER
		),
		index = 2,
		ordinal = 0,
		name = "biomeModifiers",
		remap = false,
		require = 1
	)
    private static List<BiomeModifier> runModifiers(List<BiomeModifier> forgeBiomeModifiers, MinecraftServer server) {
    	List<BiomeModifier> biomeModifiers = new ArrayList<>(forgeBiomeModifiers);
    	server.registryAccess().lookup(RTFRegistries.BIOME_MODIFIER).ifPresent((biomeModifierRegistry) -> {
    		biomeModifiers.addAll(biomeModifierRegistry.listElements().map((holder) -> {
    			return (BiomeModifier) holder.value();
    		}).toList());
    	});
		return biomeModifiers;
    }
}
