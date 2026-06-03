package raccoonman.reterraforged.mixin.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.WorldDataConfiguration;
import raccoonman.reterraforged.client.gui.Gui;
import raccoonman.reterraforged.client.gui.GuiTab;
import raccoonman.reterraforged.client.gui.preset.PackChangeListener;
import raccoonman.reterraforged.client.gui.preset.PresetListGui;
import raccoonman.reterraforged.data.RTFTranslationKeys;
import raccoonman.reterraforged.extensions.ExtensionUtil;

@Mixin(CreateWorldScreen.class)
public abstract class MixinCreateWorldScreen extends Screen {
	@Shadow
	@Final
	private TabManager tabManager;
	@Shadow
	@Final
    private HeaderAndFooterLayout layout;

	private GuiTab presetTab;
		
    protected MixinCreateWorldScreen(Component component) {
		super(component);
	}
    
	@Redirect(
		method = "init",
    	at = @At(
    		value = "INVOKE",
    		target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;addTabs([Lnet/minecraft/client/gui/components/tabs/Tab;)Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;"
    	)
    )
    protected TabNavigationBar.Builder init(TabNavigationBar.Builder builder, Tab[] tabs) {
		this.presetTab = new GuiTab(
			Component.translatable(RTFTranslationKeys.GUI_TAB_TITLE),
			this::addRenderableWidget,
			this::removeWidget,
			this.layout::visitWidgets,
			new PresetListGui(
				ExtensionUtil.cast(this),
				(gui) -> this.presetTab.setGui(gui)
			)
		);
		
    	Tab[] newTabs = new Tab[tabs.length + 1];
    	for(int i = 0; i < tabs.length; i++) {
    		newTabs[i] = tabs[i];
    	}
    	newTabs[newTabs.length - 1] = this.presetTab;
    	return builder.addTabs(newTabs);
    }

	@Inject(
		method = "render",
		at = @At("TAIL")
	)
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
    	if(this.tabManager.getCurrentTab() == this.presetTab) {
    		this.presetTab.render(graphics, mouseX, mouseY, partialTicks);
    	}
    }
	
	@Redirect(
		method = "applyNewPackConfig",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/concurrent/CompletableFuture;handleAsync(Ljava/util/function/BiFunction;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
		)
	)
    private CompletableFuture<Void> applyNewPackConfig(CompletableFuture<Void> future, BiFunction<Void, Throwable, Void> handler, Executor executor, PackRepository packRepository, WorldDataConfiguration worldDataConfiguration, Consumer<WorldDataConfiguration> consumer) {
		return future.handleAsync((value, err) -> {
			Void result = handler.apply(value, err);
			if(err == null) {
				Gui gui = this.presetTab.getGui();
				if(gui != null && gui instanceof PackChangeListener listener) {
		    		listener.onPackUpdated();
		    	}
			}
			return result;
		}, executor);
    }
    
    @Override
    public void tick() {
    	super.tick();
    	
    	if(this.tabManager.getCurrentTab() instanceof Tickable tickable) {
    		tickable.tick();
    	}
    }
	
	@Override
    public void removed() {
		this.presetTab.close();
	}
}
