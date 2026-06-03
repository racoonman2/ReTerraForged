package raccoonman.reterraforged.commands.arguments;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Climate;
import raccoonman.reterraforged.data.RTFTranslationKeys;

public record ParameterArgument(Map<String, Climate.Parameter> builtinParameters) implements ArgumentType<Climate.Parameter> {
	private static final char FULL_RANGE_MARKER = '~';
	private static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
	private static final Dynamic2CommandExceptionType MIN_GREATER_THAN_MAX = new Dynamic2CommandExceptionType((input1, input2) -> Component.translatable(RTFTranslationKeys.MIN_GREATER_THAN_MAX, input1, input2));
	private static final List<String> GLOBAL_EXAMPLES = ImmutableList.of("0.0", "[0.0,1.0]");
    
	@Override
	public Climate.Parameter parse(StringReader reader) throws CommandSyntaxException {
		char next = reader.peek();
		if(next == FULL_RANGE_MARKER) {
			reader.skip();
			return FULL_RANGE;
		}
		
		if(next == '[') {
			MutableObject<String> paramName = new MutableObject<>();
			
			reader.skip();
			long min = this.readParameterBound(reader, Climate.Parameter::min, paramName);
			String minParamName = paramName.getValue();
			
			reader.expect(',');
			
			long max = this.readParameterBound(reader, Climate.Parameter::max, paramName);
			String maxParamName = paramName.getValue();
			
			reader.expect(']');
			if (min >= max) {
	    		throw MIN_GREATER_THAN_MAX.create(
	    			paramNameOrValue(minParamName, min),
	    			paramNameOrValue(maxParamName, max)
	    		);
	        }
			return new Climate.Parameter(min, max);
		} else {
			int cursor = reader.getCursor();
			Optional<Climate.Parameter> parameter = this.readParameter(reader, new MutableObject<>());
			if(parameter.isPresent()) {
				return parameter.get();
			}
			
			reader.setCursor(cursor);
			return Climate.Parameter.point(reader.readFloat());
		}
	}

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
    	String input = commandContext.getInput();
    	return SharedSuggestionProvider.suggest(Stream.concat(this.builtinParameters.keySet().stream(), Stream.of(String.valueOf(FULL_RANGE_MARKER))).toList(), getParameterIndex(input).map(suggestionsBuilder::createOffset).orElse(suggestionsBuilder));
    }

    @Override
    public Collection<String> getExamples() {
    	List<String> keys = ImmutableList.copyOf(this.builtinParameters.keySet());
    	
    	ImmutableList.Builder<String> list = ImmutableList.builder();
    	list.addAll(GLOBAL_EXAMPLES);
    	list.add(keys.get(0), "[" + keys.get(1) + "," + keys.get(2) + "]");
    	return list.build();
    }

    public static ParameterArgument parameter(Map<String, Climate.Parameter> builtinParameters) {
    	return new ParameterArgument(builtinParameters);
    }
    
    private long readParameterBound(StringReader reader, Function<Climate.Parameter, Long> boundGetter, MutableObject<String> name) throws CommandSyntaxException {
    	int cursor = reader.getCursor();
    	Optional<Climate.Parameter> parameter = this.readParameter(reader, name);
    	if(parameter.isPresent()) {
    		return boundGetter.apply(parameter.get());
    	}
    	reader.setCursor(cursor);
		float value = reader.readFloat();
		return Climate.quantizeCoord(value);
	}
    
    private Optional<Climate.Parameter> readParameter(StringReader reader, MutableObject<String> name) throws CommandSyntaxException {
    	String param = reader.readUnquotedString();
    	Climate.Parameter builtinParameter = this.builtinParameters.get(param);
    	if(builtinParameter != null) {
    		name.setValue(param);
			return Optional.of(builtinParameter);
		} else {
			name.setValue(null);
		}

    	if(!isNumeric(param)) {
    		throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
		}
		return Optional.empty();
    }
    
    private static Optional<Integer> getParameterIndex(String input) {
    	int length = input.length();
    	int start = -1;
    	for(int i = length - 1; i >= 0; i--) {
    		char c = input.charAt(i);
    		if(c == ']') {
    			return Optional.empty();
    		}
    		if(c == '[' || c == ',') {
    			start = i + 1;
    			break;
    		}
    	}
    	return start != -1 ? Optional.of(start) : Optional.empty();
    }
    
    private static boolean isNumeric(String input) {
    	input = input.substring(input.indexOf('-') + 1, input.length());
    	
    	String[] split = input.split("\\.");
    	if(split.length == 1) {
    		return StringUtils.isNumeric(split[0]);
    	} else if(split.length == 2) {
    		return isNumericOrEmpty(split[0]) && isNumericOrEmpty(split[1]);
    	} else {
        	return false;
    	}
    }
    
    private static boolean isNumericOrEmpty(String input) {
    	return input.isEmpty() || StringUtils.isNumeric(input);
    }
    
    private static String paramNameOrValue(@Nullable String name, long value) {
    	return name != null ? name : String.valueOf(Climate.unquantizeCoord(value));
    }
    
    public static class Info implements ArgumentTypeInfo<ParameterArgument, Info.Template> {
    	private static final Codec<Map<String, Climate.Parameter>> BUILTIN_PARAMETERS_CODEC = Codec.unboundedMap(Codec.STRING, Climate.Parameter.CODEC);
    	
		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf byteBuf) {
			byteBuf.writeJsonWithCodec(BUILTIN_PARAMETERS_CODEC, template.builtinParameters());
		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf byteBuf) {
			return new Template(byteBuf.readLenientJsonWithCodec(BUILTIN_PARAMETERS_CODEC));
		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
			BUILTIN_PARAMETERS_CODEC.encode(template.builtinParameters(), JsonOps.INSTANCE, json);
		}

		@Override
		public Template unpack(ParameterArgument argument) {
			return new Template(argument.builtinParameters());
		}

        public class Template implements ArgumentTypeInfo.Template<ParameterArgument> {
        	private Map<String, Climate.Parameter> builtinParameters;

        	public Template(Map<String, Climate.Parameter> builtinParameters) {
        		this.builtinParameters = builtinParameters;
        	}

        	public Map<String, Climate.Parameter> builtinParameters() {
        		return this.builtinParameters;
        	}
        	
        	@Override
            public ParameterArgument instantiate(CommandBuildContext commandBuildContext) {
                return ParameterArgument.parameter(this.builtinParameters);
            }

            @Override
            public ArgumentTypeInfo<ParameterArgument, ?> type() {
                return Info.this;
            }
        }
    }
}
