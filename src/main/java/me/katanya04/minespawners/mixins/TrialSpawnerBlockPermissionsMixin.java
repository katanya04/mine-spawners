
package me.katanya04.minespawners.mixins;

import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allow non op players/non-creative players to place a trial spawner without it resetting its nbt
 */
@Mixin(TrialSpawnerBlockEntity.class)
public class TrialSpawnerBlockPermissionsMixin {
    @Inject(method = "copyItemDataRequiresOperator", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
