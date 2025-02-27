package me.katanya04.minespawners.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds all the config values, and manages the creation of the config file, saving and reading from it
 */
public class SimpleConfig {
    public static final Map<String, ConfigValue<?>> values = new HashMap<>();
    private static final File configFile = new File("config" + File.separator + "minespawners-config.json");
    private static boolean checkIfConfigExists() {
        return configFile.isFile() && configFile.exists() && configFile.canRead() && configFile.canWrite();
    }
    private static boolean createFile(boolean override) throws IOException {
        if (checkIfConfigExists() && !override)
            return false;
        configFile.delete();
        configFile.getParentFile().mkdirs();
        return configFile.createNewFile();
    }
    private static void readValuesFromFile() throws IOException {
        String jsonString = new String(Files.readAllBytes(configFile.toPath()));
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        values.forEach((k, v) -> v.setValueFromJson(json));
    }
    public static void saveToFile() throws IOException {
        String jsonString = new String(Files.readAllBytes(configFile.toPath()));
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        JsonObject json = jsonElement.isJsonNull() ? new JsonObject() : jsonElement.getAsJsonObject();
        values.forEach((k, v) -> v.setValueToJson(json));
        configFile.delete();
        Files.write(configFile.toPath(), new Gson().toJson(json).getBytes(StandardCharsets.UTF_8));
    }

    public static FloatConfigValue DROP_CHANCE = new FloatConfigValue("DROP_CHANCE", 1.f,
            "Chance of dropping the spawner when mined. From 0 (never) to 100 (always)", 0.f, 1.f);

    public static void initializeConfig() throws IOException {
        SimpleConfig.values.put(DROP_CHANCE.getKey(), DROP_CHANCE);
        if (!createFile(false))
            readValuesFromFile();
        saveToFile();
    }
}