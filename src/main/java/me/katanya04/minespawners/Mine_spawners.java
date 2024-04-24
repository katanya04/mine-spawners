package me.katanya04.minespawners;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;

public class Mine_spawners implements ModInitializer {
    @Override
    public void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register((world, player, blockPos, blockState, blockEntity) -> {
            if (blockEntity == null || !blockEntity.getType().equals(BlockEntityType.MOB_SPAWNER))
                return;
            MobSpawnerBlockEntity spawnerBlock = (MobSpawnerBlockEntity) blockEntity;
            ItemStack itemInHand = player.getStackInHand(player.getActiveHand());
            if (!itemInHand.isIn(ItemTags.PICKAXES) || !EnchantmentHelper.hasSilkTouch(itemInHand))
                return;
            ItemStack spawnerStack = new ItemStack(Items.SPAWNER, 1);
            if (spawnerBlock.getLogic().getRenderedEntity(world, blockPos) != null) {
                NbtCompound nbt = new NbtCompound();
                spawnerBlock.getLogic().writeNbt(nbt);
                nbt.putString("id", "minecraft:mob_spawner");
                nbt.putShort("Delay", (short) -1);
                ComponentMap components = ComponentMap.builder().add(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(nbt)).build();
                spawnerStack.applyComponentsFrom(components);
            }
            ItemEntity spawnerItem = new ItemEntity(world, blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f, spawnerStack);
            world.spawnEntity(spawnerItem);
        });
    }
}
