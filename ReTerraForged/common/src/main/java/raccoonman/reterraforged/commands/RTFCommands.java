package raccoonman.reterraforged.commands;

import com.mojang.brigadier.CommandDispatcher;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class RTFCommands {

	public static void bootstrap() {
		register(LocatePointCommand::register);
		register(ExportCommand::register);
	}
	
	@ExpectPlatform
	public static void register(Callback callback) {
		throw new UnsupportedOperationException();
	}
	
	public interface Callback {
		void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment);
	}
}
