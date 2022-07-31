package com.possible_triangle.bigsip.mixin;

import com.possible_triangle.bigsip.modules.MaturingModule;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.contraptions.components.structureMovement.BlockMovementChecks;
import com.simibubi.create.content.logistics.block.vault.ItemVaultBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockMovementChecks.class, remap = false)
public class BlockMovementChecksMixin {

    @Inject(at = @At("HEAD"), cancellable = true, method = "isBlockAttachedTowardsFallback(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z")
    private static void keepBarrelConnected(BlockState state, Level world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(MaturingModule.INSTANCE.getBARREL())) {
            var connected = ConnectivityHandler.isConnected(world, pos, pos.relative(direction));
            cir.setReturnValue(connected);
        }
    }

}
