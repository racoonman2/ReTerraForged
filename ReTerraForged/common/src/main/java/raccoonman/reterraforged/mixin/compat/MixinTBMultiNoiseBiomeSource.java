package raccoonman.reterraforged.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.compat.TBParameterList;
import terrablender.worldgen.IExtendedParameterList;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1001)
abstract class MixinTBMultiNoiseBiomeSource {
    @Shadow
    public abstract Climate.ParameterList<Holder<Biome>> parameters();

    @Inject(
    	method = "addDebugInfo",
    	at = @At("TAIL"),
    	locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void addDebugInfo$Redirect(List<String> lines, BlockPos pos, Climate.Sampler sampler, CallbackInfo callback, int quartX, int quartY, int quartZ, Climate.TargetPoint targetPoint) {
    	Climate.ParameterList<Holder<Biome>> parameters = this.parameters();
        TBParameterList tbParameters = ExtensionUtil.cast(parameters);
		IExtendedParameterList<Holder<Biome>> extParameters = ExtensionUtil.cast(parameters);
		lines.set(lines.size() - 1, "Region: " + extParameters.getRegion(tbParameters.getIndex(targetPoint, quartX, 0, quartZ)).getName().toString());
    }
}
