package pl.voidstar.gui;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.function.DoubleConsumer;

public class OptionSlider extends SliderWidget {
    private final String label;
    private final double min, max, step;
    private final DoubleConsumer setter;

    public OptionSlider(int x, int y, int w, int h, String label, double min, double max, double step,
                        double current, DoubleConsumer setter) {
        super(x, y, w, h, Text.empty(), (Math.max(min, Math.min(max, current)) - min) / (max - min));
        this.label = label; this.min = min; this.max = max; this.step = step; this.setter = setter;
        updateMessage();
    }

    private double real() {
        double v = min + value * (max - min);
        v = Math.round(v / step) * step;
        return Math.max(min, Math.min(max, v));
    }

    @Override
    protected void updateMessage() {
        double v = real();
        String s = step >= 1 ? String.valueOf((int) v) : String.format(Locale.ROOT, "%.1f", v);
        setMessage(Text.literal(label + ": " + s));
    }

    @Override
    protected void applyValue() {
        setter.accept(real());
    }
}
