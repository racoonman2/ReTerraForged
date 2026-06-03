package raccoonman.reterraforged.client.gui.widget;

import java.util.Optional;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public abstract class Slider<A extends Number> extends AbstractSliderButton {
	private Component title;
	protected A min, max;
	private Slider.Bound<A> lower, upper;
	private Optional<Slider.ChangeCallback<A>> changeCallback;
	private Slider.Formatter<A> formatter;
	
	public Slider(int x, int y, int width, int height, Component title, double initialProgress, A min, A max, Slider.Bound<A> lower, Slider.Bound<A> upper, Optional<Slider.ChangeCallback<A>> changeCallback, Slider.Formatter<A> formatter) {
		super(x, y, width, height, title, initialProgress);
		
		this.title = title;
		this.min = min;
		this.max = max;
		this.lower = lower;
		this.upper = upper;
		this.changeCallback = changeCallback;
		this.formatter = formatter;
	}
	
	public A getMin() {
		return this.min;
	}
	
	public A getMax() {
		return this.max;
	}

	@Override
	protected void updateMessage() {
		A updatedValue = this.getValue();
        this.setMessage(CommonComponents.optionNameValue(this.title, this.formatter.format(updatedValue)));
	}
    
	@Deprecated
	@Override
	public void onRelease(MouseButtonEvent mouseButtonEvent) {
		this.changeCallback.get().apply(this, this.getValue());
	}
	
	@Override
	public void applyValue() {
		this.value = this.clampProgress(this.value);
	}
	
	public A getValue() {
		return this.getValueFromProgress(this.value);
	}
	
	public void setValue(A value) {
		this.value = this.getProgressFromValue(value);
		this.applyValue();
		this.updateMessage();
	}
	
	protected abstract double getProgressFromValue(A value);
	
	protected abstract A getValueFromProgress(double progress);
	
	private double clampProgress(double progress) {
		return Mth.clamp(
			progress, 
			this.getProgressFromValue(this.lower.get()),
			this.getProgressFromValue(this.upper.get())
		);
	}

	public static class IntSlider extends Slider<Integer> {
		private int multiple;

		public IntSlider(int x, int y, int width, int height, Component title, int initialValue, int min, int max, Slider.Bound<Integer> lower, Slider.Bound<Integer> upper, Optional<Slider.ChangeCallback<Integer>> changeCallback, int multiple, Slider.Formatter<Integer> formatter) {
			super(x, y, width, height, title, 0.0D, min / multiple, max / multiple, lower, upper, changeCallback, formatter);
			
			this.multiple = multiple;
			this.value = this.getProgressFromValue(initialValue);
			this.updateMessage();
		}

		@Override
		protected double getProgressFromValue(Integer value) {
			double d = value / this.multiple;
	    	return (Mth.clamp(d, this.min, this.max) - this.min) / (this.max - this.min);
		}
		
		@Override
		protected Integer getValueFromProgress(double progress) {
	        return NoiseUtil.round(Mth.lerp(progress, this.min, this.max)) * this.multiple;
		}
		
		public static IntSlider make(int x, int y, int width, int height, Component title, int initialValue, int min, int max, Slider.Bound<Integer> lower, Slider.Bound<Integer> upper, int multiple, Slider.Formatter<Integer> formatter) {
			return new IntSlider(x, y, width, height, title, initialValue, min, max, lower, upper, Optional.empty(), multiple, formatter);
		}
		
		public static IntSlider make(int x, int y, int width, int height, Component title, int initialValue, int min, int max, Slider.Bound<Integer> lower, Slider.Bound<Integer> upper, Slider.ChangeCallback<Integer> changeCallback, int multiple, Slider.Formatter<Integer> formatter) {
			return new IntSlider(x, y, width, height, title, initialValue, min, max, lower, upper, Optional.of(changeCallback), multiple, formatter);
		}
		
		public static IntSlider make(int x, int y, int width, int height, Component title, int initialValue, int min, int max, int multiple, Slider.Formatter<Integer> formatter) {
			return make(x, y, width, height, title, initialValue, min, max, () -> min, () -> max, multiple, formatter);
		}
		
		public static IntSlider make(int x, int y, int width, int height, Component title, int initialValue, int min, int max, Slider.ChangeCallback<Integer> changeCallback, int multiple, Slider.Formatter<Integer> formatter) {
			return make(x, y, width, height, title, initialValue, min, max, () -> min, () -> max, changeCallback, multiple, formatter);
		}
	}
	
	public static class FloatSlider extends Slider<Float> {

		public FloatSlider(int x, int y, int width, int height, Component title, float initialValue, float min, float max, Slider.Bound<Float> lower, Slider.Bound<Float> upper, Optional<Slider.ChangeCallback<Float>> changeCallback, Slider.Formatter<Float> formatter) {
			super(x, y, width, height, title, 0.0D, min, max, lower, upper, changeCallback, formatter);
			
			this.value = this.getProgressFromValue(initialValue);
			this.updateMessage();
		}

		@Override
		protected double getProgressFromValue(Float value) {
	    	return (Mth.clamp(value, this.min, this.max) - this.min) / (this.max - this.min);
		}

		@Override
		protected Float getValueFromProgress(double progress) {
			return (float) Mth.lerp(progress, this.min, this.max);
		}
		
		public static FloatSlider make(int x, int y, int width, int height, Component title, float initialValue, float min, float max, Slider.Bound<Float> lower, Slider.Bound<Float> upper, Slider.Formatter<Float> formatter) {
			return new FloatSlider(x, y, width, height, title, initialValue, min, max, lower, upper, Optional.empty(), formatter);
		}
		
		public static FloatSlider make(int x, int y, int width, int height, Component title, float initialValue, float min, float max, Slider.Bound<Float> lower, Slider.Bound<Float> upper, Slider.ChangeCallback<Float> changeCallback, Slider.Formatter<Float> formatter) {
			return new FloatSlider(x, y, width, height, title, initialValue, min, max, lower, upper, Optional.of(changeCallback), formatter);
		}
		
		public static FloatSlider make(int x, int y, int width, int height, Component title, float initialValue, float min, float max, Slider.Formatter<Float> formatter) {
			return make(x, y, width, height, title, initialValue, min, max, () -> min, () -> max, formatter);
		}
		
		public static FloatSlider make(int x, int y, int width, int height, Component title, float initialValue, float min, float max, Slider.ChangeCallback<Float> changeCallback, Slider.Formatter<Float> formatter) {
			return make(x, y, width, height, title, initialValue, min, max, () -> min, () -> max, changeCallback, formatter);
		}
	}

	public static class DoubleSlider extends Slider<Double> {

		public DoubleSlider(int x, int y, int width, int height, Component title, double initialValue, double min, double max, Slider.Bound<Double> lower, Slider.Bound<Double> upper, Optional<Slider.ChangeCallback<Double>> changeCallback, Slider.Formatter<Double> formatter) {
			super(x, y, width, height, title, 0.0D, min, max, lower, upper, changeCallback, formatter);
			
			this.value = this.getProgressFromValue(initialValue);
			this.updateMessage();
		}

		@Override
		protected double getProgressFromValue(Double value) {
	    	return (Mth.clamp(value, this.min, this.max) - this.min) / (this.max - this.min);
		}

		@Override
		protected Double getValueFromProgress(double progress) {
			return Mth.lerp(progress, this.min, this.max);
		}

		public static DoubleSlider make(int x, int y, int width, int height, Component title, double initialValue, double min, double max, Slider.Bound<Double> lower, Slider.Bound<Double> upper, Slider.Formatter<Double> formatter) {
			return new DoubleSlider(x, y, width, height, title, initialValue, min, max, lower, upper, Optional.empty(), formatter);
		}
		
		public static DoubleSlider make(int x, int y, int width, int height, Component title, double initialValue, double min, double max, Slider.Bound<Double> lower, Slider.Bound<Double> upper, Slider.ChangeCallback<Double> changeCallback, Slider.Formatter<Double> formatter) {
			return new DoubleSlider(x, y, width, height, title, initialValue, min, max, lower, upper, Optional.of(changeCallback), formatter);
		}
		
		public static DoubleSlider make(int x, int y, int width, int height, Component title, double initialValue, double min, double max, Slider.Formatter<Double> formatter) {
			return make(x, y, width, height, title, initialValue, min, max, () -> min, () -> max, formatter);
		}
		
		public static DoubleSlider make(int x, int y, int width, int height, Component title, double initialValue, double min, double max, Slider.ChangeCallback<Double> changeCallback, Slider.Formatter<Double> formatter) {
			return make(x, y, width, height, title, initialValue, min, max, () -> min, () -> max, changeCallback, formatter);
		}
	}
	
	public interface Bound<A extends Number> {
		A get();
	}
    
    public interface ChangeCallback<A extends Number> {
    	boolean apply(Slider<A> slider, A value);
    }
    
	public interface Formatter<A> {
		Component format(A value);
		
		public static <A> Formatter<A> literal() {
			return (a) -> Component.literal(a.toString());
		}
	}
}
