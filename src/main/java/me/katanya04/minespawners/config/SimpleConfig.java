package me.katanya04.minespawners.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import me.katanya04.minespawners.Main;
import me.katanya04.minespawners.config.valuetypes.ConfigValue;
import me.katanya04.minespawners.config.valuetypes.FloatConfigValue;
import me.katanya04.minespawners.config.valuetypes.ListConfigValue;
import net.fabricmc.loader.impl.util.log.Log;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that holds all the config values, and manages the creation of the config file, saving and reading from it
 */
public class SimpleConfig {
    public static final Map<String, ConfigValue<?>> values = new HashMap<>();
    private static final File configFile = new File("config" + File.separator + "minespawners-config.json");
    private static boolean checkIfConfigExists() {
        return configFile.isFile() && configFile.exists() && configFile.canRead() && configFile.canWrite();
    }
    private static boolean createFile(boolean override) {
        try {
            if (checkIfConfigExists() && !override)
                return false;
            configFile.delete();
            configFile.getParentFile().mkdirs();
            return configFile.createNewFile();
        } catch (IOException ioException) {
            throw new RuntimeException("IO exception while creating config file for minespawners mod: " + ioException);
        }
    }
    private static void readValuesFromFile() {
        try {
            String jsonString = new String(Files.readAllBytes(configFile.toPath()));
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            values.forEach((k, v) -> {
                try {
                    v.setValueFromJson(json);
                } catch (Exception exception) {
                    Log.warn(Main.logCategory, "Malformed entry for " + v.getKey() + " configuration value. Correcting");
                }
            });
            configFile.delete();
            Files.writeString(configFile.toPath(), new Gson().toJson(json));
        } catch (IOException ioException) {
            throw new RuntimeException("IO exception while reading config file for minespawners mod: " + ioException);
        }
    }
    public static void saveToFile() {
        try {
            String jsonString = new String(Files.readAllBytes(configFile.toPath()));
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            JsonObject json = jsonElement.isJsonNull() ? new JsonObject() : jsonElement.getAsJsonObject();
            values.forEach((k, v) -> v.setValueToJson(json));
            configFile.delete();
            Files.writeString(configFile.toPath(), new Gson().toJson(json));
        } catch (IOException ioException) {
            throw new RuntimeException("IO exception while saving config file for minespawners mod: " + ioException);
        }
    }

    public static FloatConfigValue DROP_CHANCE = new FloatConfigValue("DROP_CHANCE", 1.f, 0.f, 1.f);

    public static ListConfigValue<String> BLACKLISTED_PICKAXES = new ListConfigValue<>("BLACKLISTED_PICKAXES", new ArrayList<>(), SimpleConfig::isPickaxe) {
        @Override
        public Codec<List<String>> getCodec() {
            return Codec.STRING.listOf().xmap(ArrayList::new, Collections::unmodifiableList);
        }
    };

    static Set<Item> getAllPickaxes() {
        return BuiltInRegistries.ITEM.stream().filter(item -> isPickaxe(item.getDefaultInstance())).collect(Collectors.toSet());
    }

    private static boolean isPickaxe(String name) {
        return isPickaxe(BuiltInRegistries.ITEM.getValue(Identifier.tryParse(name)).getDefaultInstance());
    }

    private static boolean isPickaxe(ItemStack stack) {
        return  stack.is(ItemTags.PICKAXES) ||
                stack.isCorrectToolForDrops(Blocks.SPAWNER.defaultBlockState()) ||
                (stack.get(DataComponents.TOOL) != null && stack.get(DataComponents.TOOL).rules().stream()
                        .anyMatch(r -> (r.blocks() instanceof HolderSet.Named<Block> blocks) &&
                                blocks.key().location().equals(BlockTags.MINEABLE_WITH_PICKAXE.location()))
                );
    }

    public static void initializeConfig() {
        SimpleConfig.values.put(DROP_CHANCE.getKey(), DROP_CHANCE);
        SimpleConfig.values.put(BLACKLISTED_PICKAXES.getKey(), BLACKLISTED_PICKAXES);
        if (!createFile(false))
            readValuesFromFile();
        saveToFile();
    }
}