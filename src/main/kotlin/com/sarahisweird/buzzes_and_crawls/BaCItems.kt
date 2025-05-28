package com.sarahisweird.buzzes_and_crawls

import com.sarahisweird.buzzes_and_crawls.BuzzesAndCrawls.MOD_ID
import com.sarahisweird.buzzes_and_crawls.items.BeakerMagnifierItem
import com.sarahisweird.buzzes_and_crawls.items.OccupiedBeakerMagnifierItem
import com.sarahisweird.buzzes_and_crawls.util.addAll
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.MobEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.SpawnEggItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object BaCItems {
    val BEAKER_MAGNIFIER = register(
        "beaker_magnifier",
        ::BeakerMagnifierItem,
        Item.Settings().maxCount(1))
    val OCCUPIED_BEAKER_MAGNIFIER = register(
        "occupied_beaker_magnifier",
        ::OccupiedBeakerMagnifierItem,
        Item.Settings().maxCount(1))

    val ANT_SPAWN_EGG = registerSpawnEgg("ant", BaCEntities.ANT)

    private fun <T : Item> register(name: String, factory: (Item.Settings) -> T, settings: Item.Settings = Item.Settings()): T {
        val id = Identifier.of(MOD_ID, name)
        val key = RegistryKey.of(RegistryKeys.ITEM, id)

        val item = factory.invoke(settings.registryKey(key))
        return Registry.register(Registries.ITEM, key, item)
    }

    private fun registerSpawnEgg(
        entityName: String,
        entityType: EntityType<out MobEntity>,
        settings: Item.Settings = Item.Settings()
    ): SpawnEggItem {
        return register("${entityName}_spawn_egg", { settings -> SpawnEggItem(entityType, settings) }, settings)
    }

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(BuzzesAndCrawls.ITEM_GROUP_KEY).register { group ->
            group.addAll(
                BEAKER_MAGNIFIER,
                ANT_SPAWN_EGG,
            )
        }

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register { group ->
            group.addAll(
                BEAKER_MAGNIFIER,
            )
        }

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register { group ->
            group.addAll(
                ANT_SPAWN_EGG,
            )
        }
    }
}
