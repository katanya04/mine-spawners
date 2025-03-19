package me.katanya04.minespawners.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Make trial spawners drop only when mined with required tool (pickaxe)
 */
@Mixin(Blocks.class)
public class TrialSpawnerCorrectToolMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "net/minecraft/block/Blocks.register (Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 825), method = "<clinit>", index = 2)
    private static AbstractBlock.Settings injected(AbstractBlock.Settings settings) {
        return settings.requiresTool();
    }
}
