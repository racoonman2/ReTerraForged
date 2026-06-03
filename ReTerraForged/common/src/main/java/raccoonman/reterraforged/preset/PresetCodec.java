package raccoonman.reterraforged.preset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;

import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.preset.option.Category;
import raccoonman.reterraforged.preset.option.DynamicOption;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.option.Page;

// TODO clean this up
public record PresetCodec(List<Page> pages) implements Codec<Preset> {
	
	@Override
	public <T> DataResult<T> encode(Preset input, DynamicOps<T> ops, T prefix) {
		Map<String, Map<String, List<DynamicOption>>> dynamicOptions = new HashMap<>();
		input.getOptions()
			.entrySet()
			.forEach((entry) -> {
				Option<?> option = entry.getKey();
				if(option instanceof DynamicOption dynamic) {
					dynamicOptions.computeIfAbsent(dynamic.page(), (v) -> {
						return new HashMap<>();
					}).computeIfAbsent(dynamic.category(), (v) -> {
						return new ArrayList<>();
					}).add(dynamic);
				}
			});
			
		RecordBuilder<T> presetBuilder = ops.mapBuilder();
		for(Page page : this.pages) {
			String pageName = page.name();
			RecordBuilder<T> pageBuilder = ops.mapBuilder();

			for(Category category : page.categories()) {
				RecordBuilder<T> categoryBuilder = ops.mapBuilder();
				
				for(Option<?> option : category.options()) {
					categoryBuilder.add(option.name(), encodeOption(input, option, ops));
				}
				
				pageBuilder.add(category.name(), categoryBuilder.build(prefix));
			}

			Map<String, List<DynamicOption>> categories = dynamicOptions.get(pageName);			
			if(categories != null) {
				categories.forEach((name, options) -> {
					RecordBuilder<T> categoryBuilder = ops.mapBuilder();
					options.forEach((option) -> {
						categoryBuilder.add(option.name(), input.getOption(option).convert(ops).getValue());
					});
					pageBuilder.add(name, categoryBuilder.build(prefix));
				});
			}
			presetBuilder.add(pageName, pageBuilder.build(prefix));
		}
		return presetBuilder.build(prefix);
	}

	@Override
	public <T> DataResult<Pair<Preset, T>> decode(DynamicOps<T> ops, T input) {
		Preset preset = Preset.make();
		for(Page page : this.pages) {
			String pageName = page.name();
			ops.get(input, pageName)
			   .flatMap(ops::getMap)
			   .resultOrPartial(RTFCommon.LOGGER::error)
			   .ifPresent((categories) -> categories.entries().forEach((pair) -> {
				   this.decodeCategory(preset, pageName, page, ops, pair);
			   }));
		}
		return DataResult.success(Pair.of(preset, input));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> void decodeCategory(Preset preset, String pageName, Page page, DynamicOps<T> ops, Pair<T, T> pair) {
		String categoryName = ops.getStringValue(pair.getFirst()).getOrThrow(RuntimeException::new);
		Optional<Category> category = getCategoryByName(page.categories(), categoryName);
		T input = pair.getSecond();
		if(category.isPresent()) {
			for(Option option : category.get().options()) {
				ops.get(input, option.name()).flatMap((optionObject) -> {
					return option.codec().parse(ops, optionObject);
				}).resultOrPartial(RTFCommon.LOGGER::error).ifPresent((result) -> {
					preset.setOption(option, result);
				});
			}
		} else {
			ops.getMapValues(input).getOrThrow(RuntimeException::new).forEach((option) -> {
				String optionName = ops.getStringValue(option.getFirst()).getOrThrow(RuntimeException::new);
				preset.setOption(new DynamicOption(optionName, new Dynamic<>(ops), pageName, categoryName), new Dynamic<>(ops, option.getSecond()));
			});
		}
	}
	
	private static <V, T> DataResult<V> encodeOption(Preset preset, Option<T> option, DynamicOps<V> ops) {
		T value = preset.getOption(option);
		return option.codec().encodeStart(ops, value);
	}
	
	private static Optional<Category> getCategoryByName(List<Category> categories, String name) {
		return categories.stream().filter((category) -> category.name().equals(name)).findFirst();
	}
}
