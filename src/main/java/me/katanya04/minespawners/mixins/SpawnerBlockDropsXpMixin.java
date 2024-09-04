package me.katanya04.minespawners.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * If spawner was mined with a silk touch pickaxe, don't drop xp
 */
@Mixin(SpawnerBlock.class)
public class SpawnerBlockDropsXpMixin {
    @Inject(method = "onStacksDropped", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void injected(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        if (tool.getEnchantments().getEnchantments().contains(world.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT).getEntry(Identifier.ofVanilla("silk_touch")).get()))
            ci.cancel();
    }
}
