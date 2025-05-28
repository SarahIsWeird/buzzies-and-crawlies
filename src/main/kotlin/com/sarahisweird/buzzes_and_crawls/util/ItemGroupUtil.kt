package com.sarahisweird.buzzes_and_crawls.util

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.minecraft.item.ItemConvertible

fun FabricItemGroupEntries.addAll(vararg items: ItemConvertible) {
    for (item in items) {
        add(item)
    }
}
