package me.katanya04.minespawners.mixins;

import me.katanya04.minespawners.tags.DynamicTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * If spawner was mined with a silk touch pickaxe, don't drop xp
 */
@Mixin(SpawnerBlock.class)
public class SpawnerBlockDropsXpMixin {
    @Inject(method = "onStacksDropped", at = @At("HEAD"), cancellable = true)
    private void injected(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, CallbackInfo ci) {
        if (EnchantmentHelper.get(tool).containsKey(Enchantments.SILK_TOUCH) && tool.isSuitableFor(state) &&
                !DynamicTags.isInTag(tool, DynamicTags.BLACKLISTED))
            ci.cancel();
    }
}
