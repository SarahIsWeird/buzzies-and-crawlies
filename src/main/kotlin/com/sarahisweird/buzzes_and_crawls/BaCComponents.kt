package com.sarahisweird.buzzes_and_crawls

import com.sarahisweird.buzzes_and_crawls.BuzzesAndCrawls.MOD_ID
import com.sarahisweird.buzzes_and_crawls.components.AdditionalEntityDataComponent
import net.minecraft.component.ComponentType
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BaCComponents {
    val CAPTURED_ENTITY_COMPONENT: ComponentType<EntityType<*>> = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of(MOD_ID, "captured_entity"),
        ComponentType.builder<EntityType<*>>()
            .codec(EntityType.CODEC)
            .build()
    )

    val ADDITIONAL_ENTITY_DATA_COMPONENT: ComponentType<AdditionalEntityDataComponent> = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of(MOD_ID, "additional_entity_data"),
        ComponentType.builder<AdditionalEntityDataComponent>()
            .codec(AdditionalEntityDataComponent.CODEC)
            .build()
    )

    fun init() {}
}
