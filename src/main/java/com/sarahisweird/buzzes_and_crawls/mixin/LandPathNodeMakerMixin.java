package com.sarahisweird.buzzes_and_crawls.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sarahisweird.buzzes_and_crawls.BaCBlocks;
import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.NavigationType;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(LandPathNodeMaker.class)
public abstract class LandPathNodeMakerMixin {
    @WrapOperation(
            method = "getStart()Lnet/minecraft/entity/ai/pathing/PathNode;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;canPathfindThrough(Lnet/minecraft/entity/ai/pathing/NavigationType;)Z"
            )
    )
    private boolean letAntsPathfindThroughAnthills(BlockState state, NavigationType type, Operation<Boolean> op) {
        PathNodeMakerAccessor accessor = (PathNodeMakerAccessor) this;
        if (state.isOf(BaCBlocks.ANTHILL) && accessor.getEntity() instanceof AntEntity) {
            return true;
        }

        return op.call(state, type);
    }
}
