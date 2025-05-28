package com.sarahisweird.buzzes_and_crawls.components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs

@JvmRecord
data class AdditionalEntityDataComponent(
    val health: Float,
    val customName: Text?,
) {
    companion object {
        val CODEC: Codec<AdditionalEntityDataComponent> = RecordCodecBuilder.create<AdditionalEntityDataComponent> { builder ->
            builder.group(
                Codec.FLOAT.fieldOf("health").forGetter(AdditionalEntityDataComponent::health),
                TextCodecs.CODEC.fieldOf("custom_name").forGetter(AdditionalEntityDataComponent::customName),
            ).apply(builder, ::AdditionalEntityDataComponent)
        }
    }
}
