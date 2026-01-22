package me.katanya04.minespawners.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Mixin that allows non-creative players to place a spawner without it's NBT resetting
 */
@Mixin(BlockEntityType.class)
public abstract class SpawnerBlockPermissionsMixin {
    @Mutable
    @Final
    @Shadow
    private static Set<BlockEntityType<?>> OP_ONLY_CUSTOM_DATA;

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/Set;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;"), method = "<clinit>", cancellable = true)
    private static void injected(CallbackInfo ci) {
        OP_ONLY_CUSTOM_DATA = Set.of(BlockEntityType.COMMAND_BLOCK, BlockEntityType.LECTERN, BlockEntityType.SIGN, BlockEntityType.HANGING_SIGN);
        ci.cancel();
    }
}
