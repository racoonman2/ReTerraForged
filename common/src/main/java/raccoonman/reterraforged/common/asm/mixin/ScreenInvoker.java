package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

//a bit unnecessary but whatever
//addRenderableWidget can be overridden so we don't use an access widener
@Mixin(Screen.class)
public interface ScreenInvoker {

	@Invoker("addRenderableWidget")
    <T extends GuiEventListener & Renderable> T invokeAddRenderableWidget(T guiEventListener);
}
