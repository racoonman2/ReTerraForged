package raccoonman.reterraforged.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.BuiltInRegistries;

public class RTFArgumentTypeInfos {

	public static void bootstrap() {
		register("parameter", ParameterArgument.class, new ParameterArgument.Info());
	}	
	
	public static <A extends ArgumentType<?>> void register(String name, Class<? extends A> cls, ArgumentTypeInfo<A, ?> value) {
		ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, name, cls, value);
	}
}
