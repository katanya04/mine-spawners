package me.katanya04.minespawners.mixins;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(Block.class)
public class MoveNBTCompoundMixin {
    @Inject(method = "dropStack(Lnet/minecraft/world/World;Ljava/util/function/Supplier;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injected(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack, CallbackInfo ci) {
        NbtComponent customData = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null)
            return;
        if (!customData.contains("to_block_entity_data"))
            return;
        NbtCompound toCopy = customData.copyNbt().getCompound("to_block_entity_data");
        toCopy.remove("x");
        toCopy.remove("y");
        toCopy.remove("z");
        NbtComponent newComponent = NbtComponent.of(toCopy);
        stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, newComponent);
        stack.remove(DataComponentTypes.CUSTOM_DATA);
    }
}