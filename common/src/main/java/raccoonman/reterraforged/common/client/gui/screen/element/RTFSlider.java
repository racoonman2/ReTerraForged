package raccoonman.reterraforged.common.client.gui.screen.element;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class RTFSlider extends AbstractSliderButton {
    private double min;
    private double max;
	private Component name;
	private Precision precision;
    
    public RTFSlider(int i, int j, int k, float l, float min, float max, Component name, Precision precision) {
        super(i, j, k, 20, CommonComponents.EMPTY, 0.0);
        this.name = name;
        this.precision = precision;
        this.min = min;
        this.max = max;
        this.value = (Mth.clamp((float)l, min, max) - min) / (max - min);
        this.updateMessage();
    }

    public float getScaledValue() {
        return this.precision.scale((float) Mth.lerp(Mth.clamp(this.value, 0.0, 1.0F), this.min, this.max));
    }
    
    @Override
    public void applyValue() {
    }

    @Override
    protected void updateMessage() {
        this.setMessage(CommonComponents.optionNameValue(this.name, Component.literal(String.valueOf(this.getScaledValue()))));
    }
    
    public static enum Precision {
    	WHOLE {
			@Override
			public float scale(float input) {
				return (int) input;
			}
		},
    	DECIMAL {
			@Override
			public float scale(float input) {
				return input;
			}
		};
    	
    	public abstract float scale(float input);
    }
}