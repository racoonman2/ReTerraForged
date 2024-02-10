package raccoonman.reterraforged.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ExportHeightmapCommand {

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext commandBuildContext) {
    	commandDispatcher.register(
    		Commands.literal("rtf").requires((stack) -> stack.hasPermission(2)).then(
    			Commands.literal("export").then(
    				Commands.literal("heightmap").then(
    					Commands.argument("x", IntegerArgumentType.integer()).then(
        					Commands.argument("z", IntegerArgumentType.integer()).then(
    	        				Commands.argument("size", IntegerArgumentType.integer()).executes((ctx) -> {
    	        					throw new UnsupportedOperationException("TODO");
    	        				})
    						)
    					)
        			)
    			)
    		)
    	);
    }
}