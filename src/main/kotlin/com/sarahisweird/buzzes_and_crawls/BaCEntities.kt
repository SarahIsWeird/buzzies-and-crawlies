package com.sarahisweird.buzzes_and_crawls

import com.sarahisweird.buzzes_and_crawls.BuzzesAndCrawls.MOD_ID
import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object BaCEntities {
    @JvmField
    val ANT: EntityType<AntEntity> = register("ant",
        EntityType.Builder.create(::AntEntity, SpawnGroup.CREATURE)
            .dimensions(11f / 16f, 4f / 16f))

    private fun <T : Entity> register(name: String, typeBuilder: EntityType.Builder<T>): EntityType<T> {
        val id = Identifier.of(MOD_ID, name)
        val key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)

        return Registry.register(
            Registries.ENTITY_TYPE,
            id,
            typeBuilder.build(key)
        )
    }

    fun init() {
        FabricDefaultAttributeRegistry.register(ANT, AntEntity.createAntAttributes())
    }
}
