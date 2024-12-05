package me.katanya04.minespawners.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Allow non op players/non-creative players to place a spawner without it resetting its nbt
 */
@Mixin(BlockItem.class)
public class SpawnerBlockPermissionsMixin {
    @Inject(method = "writeNbtToBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;getType()Lnet/minecraft/block/entity/BlockEntityType;",
            shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injected(World world, PlayerEntity player, BlockPos pos, ItemStack stack, CallbackInfoReturnable<Boolean> cir,
                                 NbtComponent nbtComponent, BlockEntityType<?> blockEntityType, BlockEntity blockEntity,
                                 @Local(ordinal = 0) BlockEntityType<?> blockEntityType2) {
        if (blockEntityType2.equals(BlockEntityType.MOB_SPAWNER) || blockEntityType2.equals(BlockEntityType.TRIAL_SPAWNER))
            cir.setReturnValue(nbtComponent.applyToBlockEntity(blockEntity, world.getRegistryManager()));
    }
}
