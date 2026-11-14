package pl.voidstar.config;

import java.util.List;

/** Gotowe motywy kolorow. colors = 4 kolory gwiazdek, accent = kolor glowny menu. */
public record ColorTheme(String name, int accent, int[] colors) {

    public static final List<ColorTheme> ALL = List.of(
            new ColorTheme("Classic", 0xFFFFFF, new int[]{0xFFFFFF, 0xBDBDBD, 0x616161, 0x000000}),
            new ColorTheme("Blood", 0xFF2D4A, new int[]{0xFF2D4A, 0xC4122B, 0x6B0A18, 0x1A0005}),
            new ColorTheme("Plasma", 0xFF3DF2, new int[]{0xFF3DF2, 0xC02BD0, 0x6A1B7A, 0x1A0620}),
            new ColorTheme("Void", 0x6C5CFF, new int[]{0x6C5CFF, 0x4330D8, 0x241A80, 0x06041A}),
            new ColorTheme("Amethyst", 0xC770FF, new int[]{0xC770FF, 0x9B3FE0, 0x5A2490, 0x140826}),
            new ColorTheme("Arctic", 0x6FD8FF, new int[]{0x6FD8FF, 0x3A9CC8, 0x1E5F80, 0x051820}),
            new ColorTheme("Aqua", 0x2CF0C8, new int[]{0x2CF0C8, 0x1DB090, 0x0F6B58, 0x03201B}),
            new ColorTheme("Lime", 0x9BEA2E, new int[]{0x9BEA2E, 0x6FB81C, 0x3E6E0E, 0x0E1C03}),
            new ColorTheme("Gold", 0xFFD21F, new int[]{0xFFD21F, 0xD3A310, 0x7C5F08, 0x1F1602}),
            new ColorTheme("Mono", 0xBDBDBD, new int[]{0xE0E0E0, 0xA0A0A0, 0x606060, 0x202020})
    );

    public static ColorTheme byName(String n) {
        for (ColorTheme t : ALL) if (t.name.equals(n)) return t;
        return ALL.get(0);
    }

    /** Nakladamy motyw na wszystkie kolory w configu. */
    public void applyTo(VoidStarConfig cfg) {
        cfg.theme = name;
        System.arraycopy(colors, 0, cfg.trail.colors, 0, 4);
        System.arraycopy(colors, 0, cfg.stars.colors, 0, 4);
        cfg.stars.lineColor = colors[0];
    }

    /** Wlasny motyw z jednego koloru: 4 odcienie. */
    public static void applyCustom(VoidStarConfig cfg, int rgb) {
        int[] c = {rgb, shade(rgb, 0.7f), shade(rgb, 0.4f), shade(rgb, 0.08f)};
        cfg.theme = "Custom";
        cfg.customAccent = rgb;
        System.arraycopy(c, 0, cfg.trail.colors, 0, 4);
        System.arraycopy(c, 0, cfg.stars.colors, 0, 4);
        cfg.stars.lineColor = rgb;
    }

    private static int shade(int rgb, float f) {
        int r = (int) (((rgb >> 16) & 255) * f), g = (int) (((rgb >> 8) & 255) * f), b = (int) ((rgb & 255) * f);
        return (r << 16) | (g << 8) | b;
    }
}
