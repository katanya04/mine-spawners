package me.katanya04.minespawners.mixins;

import me.katanya04.minespawners.config.ConfigValues;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

/**
 * Remove the spawner from the drops if rng allows it
 */
@Mixin(Block.class)
public class DropChanceMixin {
    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)Ljava/util/List;",
            at = @At(value = "TAIL"))
    private static void dropStack1(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (blockEntity != null && (blockEntity.getType().equals(BlockEntityType.MOB_SPAWNER))) {
            float chance = ConfigValues.DROP_CHANCE.getValue() / 100;
            if (new Random().nextFloat() >= chance)
                cir.getReturnValue().removeIf(item -> item.getItem().equals(Items.SPAWNER));
        }
    }
    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
            at = @At(value = "TAIL"))
    private static void dropStack2(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (blockEntity != null && (blockEntity.getType().equals(BlockEntityType.MOB_SPAWNER))) {
            float chance = ConfigValues.DROP_CHANCE.getValue() / 100;
            if (new Random().nextFloat() >= chance)
                cir.getReturnValue().removeIf(item -> item.getItem().equals(Items.SPAWNER));
        }
    }
}
