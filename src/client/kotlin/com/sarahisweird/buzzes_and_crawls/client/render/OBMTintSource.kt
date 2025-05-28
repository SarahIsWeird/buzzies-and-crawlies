package com.sarahisweird.buzzes_and_crawls.client.render

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.sarahisweird.buzzes_and_crawls.BaCComponents
import com.sarahisweird.buzzes_and_crawls.client.BCClient
import net.minecraft.client.render.item.tint.TintSource
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.dynamic.Codecs
import net.minecraft.util.math.ColorHelper

@JvmRecord
data class OBMTintSource(
    val default: Int,
) : TintSource {
    companion object {
        val CODEC: MapCodec<OBMTintSource> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(Codecs.RGB.fieldOf("default").forGetter(OBMTintSource::default))
                .apply(builder, ::OBMTintSource)
        }
    }

    constructor() : this(16515327)

    override fun getTint(
        stack: ItemStack,
        world: ClientWorld?,
        user: LivingEntity?
    ): Int {
        val entityType = stack.getOrDefault(BaCComponents.CAPTURED_ENTITY_COMPONENT, null)
            ?: return default

        val color = BCClient.obmTints[entityType] ?: default
        return ColorHelper.fullAlpha(color)
    }

    override fun getCodec(): MapCodec<out TintSource> {
        return CODEC
    }
}
