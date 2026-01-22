package me.katanya04.minespawners.mixins;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
            target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;"
    ), slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=trial_spawner"),
            to = @At(value = "CONSTANT", args = "stringValue=vault")
    ), index = 2
    )
    private static BlockBehaviour.Properties injected(BlockBehaviour.Properties settings) {
        return settings.requiresCorrectToolForDrops();
    }
}
