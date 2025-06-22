package me.katanya04.minespawners.mixins;

import net.minecraft.block.entity.MobSpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin that allows non-creative players to place a spawner without it's NBT resetting
 */
@Mixin(MobSpawnerBlockEntity.class)
public abstract class SpawnerBlockPermissionsMixin {

    @Inject(at = @At(value = "HEAD"), method = "copyItemDataRequiresOperator", cancellable = true)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
