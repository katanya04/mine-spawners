package me.katanya04.minespawners.config;

public class ConfigValues {
    private ConfigValues(){}
    public static ConfigValue<Float> DROP_CHANCE = new FloatConfigValue("DROP_CHANCE", 100.f, v -> v >= 0.f && v <= 100.f,
            "Chance of dropping the spawner when mined. From 0 (never) to 100 (always)");
    public static void initialize() {
        SimpleConfig.add(DROP_CHANCE);
    }
}
