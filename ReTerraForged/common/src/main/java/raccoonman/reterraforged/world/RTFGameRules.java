package raccoonman.reterraforged.world;

import net.minecraft.world.level.GameRules;
import raccoonman.reterraforged.platform.RegistryUtil;

public class RTFGameRules {
	public static final GameRules.Key<GameRules.BooleanValue> DISABLE_STEEP_SNOW = RegistryUtil.registerGameRule("disableSteepSnow", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
	
	public static void bootstrap() {
	}
}
