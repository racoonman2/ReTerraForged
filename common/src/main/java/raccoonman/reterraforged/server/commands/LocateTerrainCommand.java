package raccoonman.reterraforged.server.commands;

import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.heightmap.WorldLookup;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;

public class LocateTerrainCommand {
    private static final DynamicCommandExceptionType ERROR_TERRAIN_NOT_FOUND = new DynamicCommandExceptionType(terrain -> Component.translatable(RTFTranslationKeys.TERRAIN_NOT_FOUND, terrain));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext commandBuildContext) {
    	commandDispatcher.register(
    		Commands.literal("reterraforged").requires((stack) -> stack.hasPermission(2)).then(
    			Commands.literal("locate").then(
    				Commands.argument("terrain", TerrainArgument.terrain()).executes((ctx) -> {
	    				CommandSourceStack stack = ctx.getSource();
	    				Terrain terrain = ctx.getArgument("terrain", Terrain.class);
	    				String terrainName = terrain.getName();
	    				@Nullable
	    				BlockPos blockPos = locate(stack, terrain, 256, 256, 24000, 30L);
	    				if(blockPos != null) {
		    			    stack.sendSuccess(() -> Component.translatable(RTFTranslationKeys.TERRAIN_FOUND, terrainName, createTeleportMessage(blockPos)), false);
		    			    return Command.SINGLE_SUCCESS;
	    				}
	    	            throw ERROR_TERRAIN_NOT_FOUND.create(terrainName);
    				})
    			)
	    	)
    	);
    }
    
    private static Component createTeleportMessage(BlockPos pos) {
        return ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ())).withStyle(s -> s
        	.withColor(ChatFormatting.GREEN)
        	.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))
        	.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
        );
    }

    @Nullable
    private static BlockPos locate(CommandSourceStack commandSourceStack, Terrain target, int step, int minRadius, int maxRadius, long timeout) {
    	ServerLevel level = commandSourceStack.getLevel();

    	@Nullable
    	GeneratorContext generatorContext;
    	if((Object) level.getChunkSource().randomState() instanceof RTFRandomState rtfRandomState && (generatorContext = rtfRandomState.generatorContext()) != null) {
    		return search(commandSourceStack, generatorContext.lookup, target, step, minRadius, maxRadius, timeout);
    	}
    	
    	return null;
    }

    @Nullable
    private static BlockPos search(CommandSourceStack commandSourceStack, WorldLookup lookup, Terrain target, int step, int minRadius, int maxRadius, long timeout) {
        int radius = maxRadius;
        double minRadiusSq = minRadius * minRadius;
        Vec3 origin = commandSourceStack.getPosition();
        int x = 0;
        int z = 0;
        int dx = 0;
        int dz = -1;
        int size = radius + 1 + radius;
        long max = (long) size * (long) size;
        long timeOut = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeout);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        Cell cell = new Cell();
        
        for (long i = 0; i < max; i++) {
            if (System.currentTimeMillis() > timeOut) {
                break;
            }

            if ((-radius <= x) && (x <= radius) && (-radius <= z) && (z <= radius)) {
                pos.set(origin.x() + (x * step), origin.y(), origin.z() + (z * step));
                if (minRadiusSq == 0 || origin.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) >= minRadiusSq) {
                	if(test(lookup, cell, pos, target)) {
                		return pos;
                	}
                }
            }

            if ((x == z) || ((x < 0) && (x == -z)) || ((x > 0) && (x == 1 - z))) {
                size = dx;
                dx = -dz;
                dz = size;
            }

            x += dx;
            z += dz;
        }

        return null;
    }
    
    @Nullable
    private static boolean test(WorldLookup lookup, Cell cell, BlockPos pos, Terrain target) {
        lookup.apply(cell, pos.getX(), pos.getZ());
        return cell.terrain.equals(target);
    }
}

