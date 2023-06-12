package me.marco2124.minespawners.mixins;

import net.minecraft.block.SpawnerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpawnerBlock.class)
public class SpawnerBlockDropsXpMixin {
    @ModifyVariable(method = "onStacksDropped", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private boolean injected(boolean dropExperience) {
        return false;
    }
}
