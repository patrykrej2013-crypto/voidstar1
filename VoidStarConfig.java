package pl.voidstar.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Wszystkie ustawienia moda. Zapis: config/voidstar_config.json */
public class VoidStarConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("voidstar_config.json");
    public static VoidStarConfig INSTANCE = new VoidStarConfig();

    public boolean modEnabled = true;
    public boolean showOtherPlayers = false;
    public String quality = "MEDIUM"; // LOW / MEDIUM / HIGH
    public String theme = "Classic";
    public int customAccent = 0xFFFFFF;

    public Trail trail = new Trail();
    public Stars stars = new Stars();
    public Totem totem = new Totem();

    public static class Trail {
        public boolean enabled = true;
        public float thickness = 0.6f;   // 0.2 - 1.0 bloku
        public float length = 6f;        // 1 - 10 blokow
        public float fadeSeconds = 2f;   // 0.5 - 8 s
        public float starSize = 3f;      // px
        public int density = 135;        // gwiazdek na 10 blokow
        public int[] colors = {0xFFFFFF, 0xBDBDBD, 0x616161, 0x000000};
        public int[] weights = {35, 25, 20, 20};
    }

    public static class Stars {
        public boolean enabled = true;
        public int count = 24;           // 8 - 40
        public float distance = 5f;      // 3 - 30 blokow
        public float thickness = 1f;     // 1 - 8 px
        public float bobAmount = 0.1f;   // unoszenie, bloki
        public float musicShake = 0.5f;  // 0 - 1 (na pozniej: Spotify)
        public boolean inertia = true;
        public int[] colors = {0xFFFFFF, 0xBDBDBD, 0x616161, 0x000000};
        public int lineColor = 0xFFFFFF;
    }

    public static class Totem {
        public String pattern = "Classic";
        public boolean rotateGalaxy = true;
        public boolean pulse = true;
        public float size = 1.0f;        // 0.5 - 1.5
    }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                VoidStarConfig c = GSON.fromJson(Files.readString(PATH), VoidStarConfig.class);
                if (c != null) INSTANCE = c;
            } else {
                save();
            }
        } catch (Exception e) {
            INSTANCE = new VoidStarConfig(); // uszkodzony plik -> domyslne
        }
    }

    public static void save() {
        try {
            Files.writeString(PATH, GSON.toJson(INSTANCE));
        } catch (IOException ignored) { }
    }

    public static void reset() {
        INSTANCE = new VoidStarConfig();
        save();
    }
}
