package com.sarahisweird.buzzes_and_crawls.client.entity.ant

import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.state.LivingEntityRenderState
import software.bernie.geckolib.renderer.GeoEntityRenderer
import software.bernie.geckolib.renderer.base.GeoRenderState

class AntEntityRenderer<T>(
    context: EntityRendererFactory.Context
) : GeoEntityRenderer<AntEntity, T>(
    context,
    AntEntityModel()
) where T : LivingEntityRenderState, T : GeoRenderState {

}
