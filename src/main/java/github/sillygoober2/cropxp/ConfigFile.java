package github.sillygoober2.cropxp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigFile {
    public static final Logger LOGGER = LoggerFactory.getLogger("CropXP");

    private static final String CONFIG_FILENAME = "sillys_crop_xp.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static Map<String, int[]> cropXpMap = new HashMap<>();

    private static final Map<String, int[]> DEFAULT_CROP_XP = Map.of(
            "minecraft:wheat", new int[]{1, 3},
            "minecraft:carrots", new int[]{2, 4},
            "minecraft:potatoes", new int[]{1, 2},
            "minecraft:beetroots", new int[]{1, 2},
            "minecraft:nether_wart", new int[]{2, 5}
    );

    public static void ensureConfigExists(MinecraftServer server) {
        Path CONFIG_PATH = server.getSavePath(WorldSavePath.ROOT).resolve(CONFIG_FILENAME);
        if (Files.notExists(CONFIG_PATH)) {
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(DEFAULT_CROP_XP, writer);
            } catch (IOException e) {
                LOGGER.error("Failed to write default XP config", e);
            }
            cropXpMap = new HashMap<>(DEFAULT_CROP_XP);
        }
    }

    public static void loadConfig(MinecraftServer server) {
        Path configPath = server.getSavePath(WorldSavePath.ROOT).resolve(CONFIG_FILENAME);
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                Type type = new TypeToken<Map<String, int[]>>() {}.getType();
                cropXpMap = GSON.fromJson(reader, type);
            } catch (IOException e) {
                LOGGER.error("Failed to load XP config", e);
            }
        } else {
            LOGGER.warn("Config file not found; using defaults");
            cropXpMap = DEFAULT_CROP_XP;
        }
    }

    public static Map<String, int[]> getConfigMap() {
        return cropXpMap;
    }


}
