package raccoonman.reterraforged.world.worldgen.feature.template.template;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface TemplateContext {
	void recordState(BlockPos pos, BlockState state);
	
    public interface Factory<T extends TemplateContext> {
    	T createContext();
    }
}
