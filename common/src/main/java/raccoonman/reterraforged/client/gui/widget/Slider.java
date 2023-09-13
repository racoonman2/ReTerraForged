package raccoonman.reterraforged.client.gui.widget;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class Slider extends AbstractSliderButton {
    private double min;
    private double max;
	private Component name;
	private Format format;
	@Nullable
	private DoubleUnaryOperator callback;
    
    public Slider(int x, int y, int width, int height, float initialValue, float min, float max, Component name, Format format) {
        super(x, y, width, height, CommonComponents.EMPTY, 0.0);
        this.name = name;
        this.format = format;
        this.min = min;
        this.max = max;
        this.value = (Mth.clamp(initialValue, min, max) - min) / (max - min);
        this.updateMessage();
    }

    public void setCallback(DoubleUnaryOperator callback) {
    	this.callback = callback;
    }
    
    public double getLerpedValue() {
    	return Mth.lerp(Mth.clamp(this.value, 0.0, 1.0F), this.min, this.max);
    }
    
    public float getScaledValue() {
        return this.format.scale((float) this.getLerpedValue());
    }
    
    @Override
    public void applyValue() {
    	if(this.callback != null) {
        	this.value = this.callback.applyAsDouble(this.value);
    	}
    }

    @Override
    protected void updateMessage() {
        this.setMessage(CommonComponents.optionNameValue(this.name, Component.literal(this.format.getMessage(this.getScaledValue()))));
    }
    
    public static DoubleUnaryOperator clamp(DoubleSupplier min, DoubleSupplier max)	{
    	return (value) -> {
    		return Mth.clamp(value,
	    		min != null ? min.getAsDouble() : 0.0F,
	    		max != null ? max.getAsDouble() : 1.0F
	    	);
    	};
    }
    
    public static enum Format {
    	INT {
			@Override
			public float scale(float input) {
				return (int) input;
			}

			@Override
			public String getMessage(float input) {
				return String.valueOf((int) input);
			}
		},
    	FLOAT {
			@Override
			public float scale(float input) {
				return input;
			}

			@Override
			public String getMessage(float input) {
				return String.format("%.3f", input);
			}
		};
    	
    	public abstract float scale(float input);
    	
    	public abstract String getMessage(float input);
    }
}