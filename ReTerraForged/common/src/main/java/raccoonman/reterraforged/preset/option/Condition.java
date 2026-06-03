package raccoonman.reterraforged.preset.option;

import raccoonman.reterraforged.preset.Preset;

public interface Condition {
	boolean test(Preset preset);
}
