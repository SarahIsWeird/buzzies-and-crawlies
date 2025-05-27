package com.sarahisweird.buzzes_and_crawls

import com.sarahisweird.buzzes_and_crawls.BuzzesAndCrawls.MOD_ID
import com.sarahisweird.buzzes_and_crawls.blocks.AntHillBlock
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

object BaCBlocks {
    @JvmField
    val ANTHILL = register("anthill", ::AntHillBlock, AbstractBlock.Settings.create()
        .pistonBehavior(PistonBehavior.DESTROY)
        .sounds(BlockSoundGroup.ROOTED_DIRT)
        .allowsSpawning { _, _, _, _ -> false }
        .dynamicBounds()
        .notSolid() // SnowBlock uses this, so you know what, I don't care.
        .strength(0.25f)
        .blockVision { state, _, _ -> state.get(AntHillBlock.LAYERS, 8) >= AntHillBlock.MAX_LAYERS }
        .mapColor(MapColor.DIRT_BROWN))

    private fun <T : Block> register(name: String, factory: (AbstractBlock.Settings) -> T, settings: AbstractBlock.Settings, registerBlockItem: Boolean = true): T {
        val id = Identifier.of(MOD_ID, name)
        val blockKey = RegistryKey.of(RegistryKeys.BLOCK, id)
        val block = factory.invoke(settings.registryKey(blockKey))

        if (registerBlockItem) {
            val itemKey = RegistryKey.of(RegistryKeys.ITEM, id)
            val item = BlockItem(block, Item.Settings()
                .registryKey(itemKey)
                .useBlockPrefixedTranslationKey())

            Registry.register(Registries.ITEM, itemKey, item)
        }

        return Registry.register(Registries.BLOCK, blockKey, block)
    }

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register { group ->
            group.add(ANTHILL.asItem())
        }
    }
}
