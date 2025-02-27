package me.katanya04.minespawners.loot;

import me.katanya04.minespawners.Main;
import me.katanya04.minespawners.loot.functions.CopyDataComponentFunction;
import me.katanya04.minespawners.loot.functions.SetDataComponentFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registration of the {@link SetDataComponentFunction} loot function
 */
public class LootFunctions {
    public static LootFunctionType<SetDataComponentFunction> setDataComponentFunctionType;
    public static LootFunctionType<CopyDataComponentFunction> copyDataComponentFunctionType;
    public static void register() {
        setDataComponentFunctionType = Registry.register(Registries.LOOT_FUNCTION_TYPE,
                Identifier.of(Main.MOD_ID, "set_data_component"), new LootFunctionType<>(SetDataComponentFunction.CODEC));
        copyDataComponentFunctionType = Registry.register(Registries.LOOT_FUNCTION_TYPE,
                Identifier.of(Main.MOD_ID, "copy_data_component"), new LootFunctionType<>(CopyDataComponentFunction.CODEC));
    }
}
