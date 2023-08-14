package raccoonman.reterraforged.common.util;

import java.awt.Color;

public class ColorUtil {

	public static int rgba(float h, float s, float b) {
        int argb = Color.HSBtoRGB(h, s, b);
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue =  argb & 0xFF;
        return rgba(red, green, blue);
    }

    public static int rgba(int r, int g, int b) {
        return r + (g << 8) + (b << 16) + (255 << 24);
    }
}
