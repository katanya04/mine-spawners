package me.katanya04.minespawners.mixins;

import net.minecraft.block.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Make trial spawners drop only when mined with required tool (pickaxe)
 */
@Mixin(Blocks.class)
public class TrialSpawnerCorrectToolMixin {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;"
    ), slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=trial_spawner"),
            to = @At(value = "CONSTANT", args = "stringValue=vault")
    ), index = 2
    )
    private static AbstractBlock.Settings injected(AbstractBlock.Settings settings) {
        return settings.requiresTool();
    }
}
