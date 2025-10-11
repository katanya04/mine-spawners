package me.katanya04.minespawners.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * When adding tooltip, take mob info from "SpawnData" nbt tag if it's a spawner, or from "spawn_data" if it's a trial
 * spawner (See <a href="https://github.com/katanya04/mine-spawners/issues/6#issuecomment-2890670499">Issue #6</a> and
 * <a href="https://report.bugs.mojang.com/servicedesk/customer/portal/2/MC-298744">this issue at Mojira</a>)
 */
@Mixin(ItemStack.class)
public class TrialSpawnerTooltipMixin {
    @ModifyExpressionValue(method="appendTooltip", at = @At(value = "CONSTANT", args = "stringValue=SpawnData"))
    private String injected(String original) {
        ItemStack thisItem = (ItemStack) (Object) this;
        return thisItem.isOf(Items.SPAWNER) ? "SpawnData" : "spawn_data";
    }
}
