package raccoonman.reterraforged.mixin.compat;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.serialization.MapCodec;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import raccoonman.reterraforged.world.worldgen.modifier.RTFWorldGenModifiers;

@Mixin(Lithostitched.class)
class MixinLTLithostitched {

	@Inject(
		method = "registerCommonModifiers",
		at = @At("TAIL"),
		remap = false
	)
	private static void registerCommonModifiers(BiConsumer<String, MapCodec<? extends Modifier>> consumer, CallbackInfo callback) {
		RTFWorldGenModifiers.bootstrap();
	}
}
