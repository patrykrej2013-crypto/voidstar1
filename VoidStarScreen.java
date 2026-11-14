package pl.voidstar.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import pl.voidstar.config.ColorTheme;
import pl.voidstar.config.VoidStarConfig;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class VoidStarScreen extends Screen {
    private static final String[] TABS = {"TRAIL", "TOTEM", "GWIAZDKI", "MOTYWY", "INNE"};
    private static int currentTab = 0;

    private final Screen parent;
    private int px, py, pw, ph, rowY, cx, cw;

    public VoidStarScreen(Screen parent) {
        super(Text.literal("VoidStar"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        pw = Math.min(600, width - 10);
        ph = Math.min(400, height - 10);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        cw = Math.min(300, pw - 20);
        cx = px + pw - cw - 10;          // lewa strona = podglad, prawa = ustawienia
        rowY = py + 70;

        int tw = (pw - 20) / TABS.length;
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            addDrawableChild(ButtonWidget.builder(Text.literal((i == currentTab ? "> " : "") + TABS[i]),
                    b -> { currentTab = idx; clearAndInit(); })
                    .dimensions(px + 10 + i * tw, py + 36, tw - 2, 20).build());
        }

        VoidStarConfig c = VoidStarConfig.INSTANCE;
        switch (currentTab) {
            case 0 -> {
                toggle("Trail", () -> c.trail.enabled, v -> c.trail.enabled = v);
                slider("Grubosc (bloki)", 0.2, 1.0, 0.1, c.trail.thickness, v -> c.trail.thickness = (float) v);
                slider("Dlugosc (bloki)", 1, 10, 1, c.trail.length, v -> c.trail.length = (float) v);
                slider("Zanikanie (s)", 0.5, 8, 0.5, c.trail.fadeSeconds, v -> c.trail.fadeSeconds = (float) v);
                slider("Rozmiar gwiazdek (px)", 2, 8, 1, c.trail.starSize, v -> c.trail.starSize = (float) v);
                slider("Gestosc (na 10 blokow)", 30, 150, 10, c.trail.density, v -> c.trail.density = (int) v);
            }
            case 1 -> {
                slider("Rozmiar (%)", 50, 150, 10, c.totem.size * 100, v -> c.totem.size = (float) (v / 100));
                toggle("Obrot galaktyki", () -> c.totem.rotateGalaxy, v -> c.totem.rotateGalaxy = v);
                toggle("Pulsowanie", () -> c.totem.pulse, v -> c.totem.pulse = v);
                row(Text.literal("50 wzorow + Custom: etap 4"), b -> {});
            }
            case 2 -> {
                toggle("Gwiazdki", () -> c.stars.enabled, v -> c.stars.enabled = v);
                slider("Ilosc", 8, 40, 1, c.stars.count, v -> c.stars.count = (int) v);
                slider("Odleglosc (bloki)", 3, 30, 1, c.stars.distance, v -> c.stars.distance = (float) v);
                slider("Grubosc (px)", 1, 8, 1, c.stars.thickness, v -> c.stars.thickness = (float) v);
                slider("Unoszenie (bloki)", 0, 0.5, 0.1, c.stars.bobAmount, v -> c.stars.bobAmount = (float) v);
                slider("Sila drgan (muzyka)", 0, 1, 0.1, c.stars.musicShake, v -> c.stars.musicShake = (float) v);
                toggle("Bezwladnosc", () -> c.stars.inertia, v -> c.stars.inertia = v);
            }
            case 3 -> {
                int half = cw / 2 - 2;
                int i = 0;
                for (ColorTheme t : ColorTheme.ALL) {
                    int x = cx + (i % 2) * (half + 4);
                    int y = rowY + (i / 2) * 24;
                    addDrawableChild(ButtonWidget.builder(Text.literal((t.name().equals(c.theme) ? "* " : "") + t.name()),
                            b -> { t.applyTo(c); clearAndInit(); }).dimensions(x, y, half, 20).build());
                    i++;
                }
                int cy = rowY + 5 * 24 + 4;
                final int[] rgb = {(c.customAccent >> 16) & 255, (c.customAccent >> 8) & 255, c.customAccent & 255};
                int third = cw / 3 - 2;
                String[] n = {"R", "G", "B"};
                for (int k = 0; k < 3; k++) {
                    final int kk = k;
                    addDrawableChild(new OptionSlider(cx + k * (third + 3), cy, third, 20, n[k], 0, 255, 1, rgb[k], v -> {
                        rgb[kk] = (int) v;
                        ColorTheme.applyCustom(c, (rgb[0] << 16) | (rgb[1] << 8) | rgb[2]);
                    }));
                }
            }
            case 4 -> {
                toggle("Caly mod", () -> c.modEnabled, v -> c.modEnabled = v);
                toggle("Efekty innych graczy", () -> c.showOtherPlayers, v -> c.showOtherPlayers = v);
                row(Text.literal("Jakosc: " + c.quality), b -> {
                    c.quality = switch (c.quality) { case "LOW" -> "MEDIUM"; case "MEDIUM" -> "HIGH"; default -> "LOW"; };
                    clearAndInit();
                });
                row(Text.literal("Zresetuj wszystko"), b -> { VoidStarConfig.reset(); clearAndInit(); });
            }
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Zamknij"), b -> close())
                .dimensions(px + pw - 90, py + ph - 28, 80, 20).build());
    }

    private void row(Text label, ButtonWidget.PressAction a) {
        addDrawableChild(ButtonWidget.builder(label, a).dimensions(cx, rowY, cw, 20).build());
        rowY += 24;
    }

    private void toggle(String name, BooleanSupplier get, Consumer<Boolean> set) {
        addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + (get.getAsBoolean() ? "WL" : "WYL")), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(Text.literal(name + ": " + (get.getAsBoolean() ? "WL" : "WYL")));
        }).dimensions(cx, rowY, cw, 20).build());
        rowY += 24;
    }

    private void slider(String name, double min, double max, double step, double cur, DoubleConsumer set) {
        addDrawableChild(new OptionSlider(cx, rowY, cw, 20, name, min, max, step, cur, set));
        rowY += 24;
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        super.renderBackground(ctx, mx, my, delta);
        ctx.fill(px, py, px + pw, py + ph, 0xE60A0A0F);      // tlo #0A0A0F, ~90%
        ctx.drawBorder(px, py, pw, ph, 0xFFFFFFFF);          // obramowanie 1px
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("VOIDSTAR"), px + pw / 2, py + 12, 0xFFFFFF);

        // lewa strona: podglad aktualnych kolorow (podglad postaci: etap 2)
        VoidStarConfig c = VoidStarConfig.INSTANCE;
        int lx = px + 12, ly = py + 70;
        ctx.drawTextWithShadow(textRenderer, Text.literal("Motyw: " + c.theme), lx, ly, 0xFFFFFF);
        for (int i = 0; i < 4; i++) {
            ctx.fill(lx + i * 30, ly + 16, lx + i * 30 + 26, ly + 42, 0xFF000000 | c.stars.colors[i]);
            ctx.drawBorder(lx + i * 30, ly + 16, 26, 26, 0xFF888888);
        }
    }

    @Override
    public void removed() {
        VoidStarConfig.save(); // autozapis
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
