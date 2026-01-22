package me.katanya04.minespawners.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sets the spawn potential to be the spawn entry one (See <a href="https://github.com/katanya04/mine-spawners/issues/1">Issue #1</a>)
 */
@Mixin(BaseSpawner.class)
public class UpdateSpawnPotentialsMixin {
    @Shadow private WeightedList<SpawnData> spawnPotentials;

    @Shadow @Nullable private SpawnData nextSpawnData;

    @Inject(method = "setEntityId", at = @At("TAIL"))
    private void injected(EntityType<?> type, Level world, RandomSource random, BlockPos pos, CallbackInfo ci) {
        this.spawnPotentials = WeightedList.of(this.nextSpawnData);
    }
}