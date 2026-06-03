package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFChunk;

@Mixin(ImposterProtoChunk.class)
public abstract class MixinImposterProtoChunk implements RTFChunk {

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	public void init(LevelChunk levelChunk, boolean bl, CallbackInfo callback) {
		RTFChunk rtfChunk = ExtensionUtil.cast(levelChunk);
		rtfChunk.getMaxHeight().ifPresent(this::setMaxHeight);
	}
}
