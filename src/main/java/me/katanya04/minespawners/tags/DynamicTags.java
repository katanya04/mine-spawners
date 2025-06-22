package me.katanya04.minespawners.tags;

import me.katanya04.minespawners.Main;
import me.katanya04.minespawners.config.SimpleConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A dynamic tag, is not registered anywhere, it is just a map tag-supplier of elements
 */
public class DynamicTags {
    private static final Map<TagKey<Item>, Supplier<Set<Item>>> DYNAMIC_TAGS = new HashMap<>();
    public static final TagKey<Item> BLACKLISTED = TagKey.of(Registries.ITEM.getKey(), Identifier.of(Main.MOD_ID, "blacklisted"));
    static {
        DYNAMIC_TAGS.put(BLACKLISTED, () -> SimpleConfig.BLACKLISTED_PICKAXES.getValue().stream()
                .map(p -> Registries.ITEM.get(Identifier.tryParse(p))).collect(Collectors.toSet()));
    }

    public static boolean isInTag(ItemStack stack, TagKey<Item> tag) {
        return stack.isIn(tag) || DYNAMIC_TAGS.getOrDefault(tag, Collections::emptySet).get().contains(stack.getItem());
    }
}
