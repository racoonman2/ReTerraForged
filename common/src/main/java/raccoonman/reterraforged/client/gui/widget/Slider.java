package raccoonman.reterraforged.client.gui.widget;

import java.util.function.DoubleConsumer;

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
	private Slider minSlider, maxSlider;
	@Nullable
	private DoubleConsumer callback;
    
    public Slider(int x, int y, int width, int height, float initialValue, float min, float max, Component name, Format format) {
        super(x, y, width, height, CommonComponents.EMPTY, 0.0);
        this.name = name;
        this.format = format;
        this.min = min;
        this.max = max;
        this.value = (Mth.clamp(initialValue, min, max) - min) / (max - min);
        this.updateMessage();
    }
    
    public void setClamp(Slider min, Slider max) {
    	this.minSlider = min;
    	this.maxSlider = max;
    }
    
    public void setMin(Slider min) {
    	this.minSlider = min;
    }
    
    public void setMax(Slider max) {
    	this.maxSlider = max;
    }

    public void setCallback(DoubleConsumer callback) {
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
    	this.value = Mth.clamp(this.value, 
    		this.minSlider != null ? this.minSlider.getLerpedValue() : 0.0F,
    		this.maxSlider != null ? this.maxSlider.getLerpedValue() : 1.0F
    	);
    	if(this.callback != null) {
        	this.callback.accept(this.value);
    	}
    }

    @Override
    protected void updateMessage() {
        this.setMessage(CommonComponents.optionNameValue(this.name, Component.literal(this.format.getMessage(this.getScaledValue()))));
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