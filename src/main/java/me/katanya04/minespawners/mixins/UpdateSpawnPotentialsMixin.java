package me.katanya04.minespawners.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sets the spawn potential to be the spawn entry one (See <a href="https://github.com/katanya04/mine-spawners/issues/1">Issue #1</a>)
 */
@Mixin(MobSpawnerLogic.class)
public class UpdateSpawnPotentialsMixin {
    @Shadow private DataPool<MobSpawnerEntry> spawnPotentials;

    @Shadow @Nullable private MobSpawnerEntry spawnEntry;

    @Inject(method = "setEntityId", at = @At("HEAD"))
    private void injected(EntityType<?> type, World world, Random random, BlockPos pos, CallbackInfo ci) {
        this.spawnEntry = new MobSpawnerEntry();
    }

    @Inject(method = "setEntityId", at = @At("TAIL"))
    private void injected_2(EntityType<?> type, World world, Random random, BlockPos pos, CallbackInfo ci) {
        if (this.spawnEntry != null)
            this.spawnPotentials = DataPool.of(this.spawnEntry);
    }
}