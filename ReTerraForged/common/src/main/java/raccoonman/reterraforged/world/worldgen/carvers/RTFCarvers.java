package raccoonman.reterraforged.world.worldgen.carvers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import raccoonman.reterraforged.platform.RegistryUtil;

public class RTFCarvers {
	public static final WorldCarver<ScalableCaveCarver.Config> SCALABLE_CAVE_CARVER = register("scalable_cave_carver", new ScalableCaveCarver(ScalableCaveCarver.Config.CODEC));
	
	public static void bootstrap() {
	}

    private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String name, F carver) {
    	RegistryUtil.register(BuiltInRegistries.CARVER, name, carver);
    	return carver;
    }
}
