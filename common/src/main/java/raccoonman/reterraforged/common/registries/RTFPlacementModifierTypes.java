package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.common.level.levelgen.placement.NeverPlacementModifier;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFPlacementModifierTypes {
	public static final PlacementModifierType<NeverPlacementModifier> NEVER = register("never", NeverPlacementModifier.CODEC);

    public static void bootstrap() {
    }
    
    private static <P extends PlacementModifier> PlacementModifierType<P> register(String name, Codec<P> codec) {
    	PlacementModifierType<P> type = () -> codec;
		RegistryUtil.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, name, type);
		return type;
    }
}
