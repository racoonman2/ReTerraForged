package raccoonman.reterraforged.server.commands.fabric;

import java.util.function.BiConsumer;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public class RTFCommandsImpl {

	public static void register(BiConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext> register) {
	    CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> register.accept(dispatcher, buildContext));
	}
}
