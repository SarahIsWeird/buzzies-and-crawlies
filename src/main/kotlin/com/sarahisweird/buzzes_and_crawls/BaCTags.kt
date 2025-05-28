package com.sarahisweird.buzzes_and_crawls

import com.sarahisweird.buzzes_and_crawls.BuzzesAndCrawls.MOD_ID
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object BaCTags {
    val BEAKER_INSPECTABLE_TAG: TagKey<EntityType<*>> = TagKey.of(
        RegistryKeys.ENTITY_TYPE,
        Identifier.of(MOD_ID, "beaker_inspectable")
    )
}
