package me.marco2124.minespawners.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MobSpawnerLogic.class)
public class UpdateSpawnPotentials {
    @Shadow private DataPool<MobSpawnerEntry> spawnPotentials;

    @Shadow @Nullable private MobSpawnerEntry spawnEntry;

    @Inject(method = "setEntityId", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected(EntityType<?> type, World world, Random random, BlockPos pos, CallbackInfo ci) {
        this.spawnPotentials =  DataPool.of(this.spawnEntry != null ? this.spawnEntry : new MobSpawnerEntry());
    }
}
