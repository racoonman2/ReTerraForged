package raccoonman.reterraforged.client.gui.widget;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

//TODO this should be cleaned up
public class Slider extends AbstractSliderButton {
    private double min;
    private double max;
	private Component name;
	private Format format;
	@Nullable
	private Callback callback;
    
    public Slider(int x, int y, int width, int height, float initialValue, float min, float max, Component name, Format format, @Nullable Callback callback) {
        super(x, y, width, height, CommonComponents.EMPTY, 0.0);
        this.name = name;
        this.format = format;
        this.min = min;
        this.max = max;
        this.value = this.getSliderValue(initialValue);
        this.callback = callback;
        this.updateMessage();
    }
    
    public double getValue() {
    	return this.value;
    }
    
	public double getMin() {
		return this.min;
	}

	public double getMax() {
		return this.max;
	}
    
    public double getSliderValue(float value) {
    	return (Mth.clamp(value, this.min, this.max) - this.min) / (this.max - this.min);
    }
    
    public double getLerpedValue() {
    	return this.lerpValue(this.value);
    }
    
    public double lerpValue(double value) {
    	return Mth.lerp(value, this.min, this.max);
    }
    
    public double scaleValue(double value) {
        return this.format.scale(this.lerpValue(value));
    }
    
    @Override
    public void applyValue() {
    	if(this.callback != null) {
        	this.value = this.callback.apply(this, this.value);
    	}
    }

    @Override
    protected void updateMessage() {
        this.setMessage(CommonComponents.optionNameValue(this.name, Component.literal(this.format.getMessage(this.scaleValue((float) this.value)))));
    }
    
    public static enum Format {
    	INT {
			@Override
			public double scale(double input) {
				return (int) input;
			}

			@Override
			public String getMessage(double input) {
				return String.valueOf((int) input);
			}
		},
    	FLOAT {
			@Override
			public double scale(double input) {
				return input;
			}

			@Override
			public String getMessage(double input) {
				return String.format("%.3f", input);
			}
		};
    	
    	public abstract double scale(double input);
    	
    	public abstract String getMessage(double input);
    }
    
    public interface Callback {
    	double apply(Slider slider, double value);
    }
}