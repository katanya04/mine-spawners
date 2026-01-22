package me.katanya04.minespawners.mixins;

import me.katanya04.minespawners.tags.DynamicTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * If spawner was mined with a silk touch pickaxe, don't drop xp
 */
@Mixin(SpawnerBlock.class)
public class SpawnerBlockDropsXpMixin {
    @Inject(method = "spawnAfterBreak", at = @At("HEAD"), cancellable = true)
    private void injected(BlockState state, ServerLevel world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        Optional<Registry<Enchantment>> enchantmentRegistry = world.registryAccess().lookup(Registries.ENCHANTMENT);
        enchantmentRegistry.flatMap(enchantments -> enchantments.get(Identifier.withDefaultNamespace("silk_touch")))
                .ifPresent(silkTouch -> {
                    if (tool.getEnchantments().keySet().contains(silkTouch) && tool.isCorrectToolForDrops(state) && !DynamicTags.isInTag(tool, DynamicTags.BLACKLISTED))
                        ci.cancel();
                });
    }
}
