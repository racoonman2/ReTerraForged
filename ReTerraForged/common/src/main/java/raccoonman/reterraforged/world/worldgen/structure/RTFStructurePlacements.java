package raccoonman.reterraforged.world.worldgen.structure;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import raccoonman.reterraforged.platform.RegistryUtil;

public class RTFStructurePlacements {
	public static final StructurePlacementType<DisabledStructurePlacement> DISABLED = register("disabled", DisabledStructurePlacement.CODEC);
	
	public static void bootstrap() {
	}
	
    private static <S extends StructurePlacement> StructurePlacementType<S> register(String name, MapCodec<S> codec) {
    	StructurePlacementType<S> type = () -> codec;
    	RegistryUtil.register(BuiltInRegistries.STRUCTURE_PLACEMENT, name, type);
    	return type;
    }
}
