package com.sarahisweird.buzzes_and_crawls.mixin;

import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PathNodeMaker.class)
public interface PathNodeMakerAccessor {
    @Accessor
    MobEntity getEntity();
}
