package me.katanya04.minespawners.mixins;

import net.minecraft.block.Block;
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
public class RemoveNBTMixin {
    @Inject(method = "dropStack(Lnet/minecraft/world/World;Ljava/util/function/Supplier;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injected(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack, CallbackInfo ci) {
        NbtCompound spawnerNBT = stack.getNbt();
        if (spawnerNBT == null)
            return;
        NbtCompound blockEntityTag = spawnerNBT.getCompound("BlockEntityTag");
        if (blockEntityTag == null)
            return;
        blockEntityTag.remove("x");
        blockEntityTag.remove("y");
        blockEntityTag.remove("z");
        blockEntityTag.remove("Delay");
        spawnerNBT.put("BlockEntityTag", blockEntityTag);
        stack.setNbt(spawnerNBT);
    }
}
