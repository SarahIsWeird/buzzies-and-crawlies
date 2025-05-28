package com.sarahisweird.buzzes_and_crawls.items

import com.sarahisweird.buzzes_and_crawls.BaCComponents
import com.sarahisweird.buzzes_and_crawls.BaCItems
import com.sarahisweird.buzzes_and_crawls.BaCTags
import com.sarahisweird.buzzes_and_crawls.components.AdditionalEntityDataComponent
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand

class BeakerMagnifierItem(settings: Settings) : Item(settings) {
    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        if (!entity.type.isIn(BaCTags.BEAKER_INSPECTABLE_TAG)) {
            return ActionResult.PASS
        }

        if (stack.contains(DataComponentTypes.ENTITY_DATA)) return ActionResult.FAIL
        if (!entity.isAlive) return ActionResult.PASS

        if (user.world.isClient) return ActionResult.SUCCESS

        stack.set(BaCComponents.CAPTURED_ENTITY_COMPONENT, entity.type)

        if (entity.health != entity.maxHealth || entity.hasCustomName()) {
            val additionalEntityData = AdditionalEntityDataComponent(entity.health, entity.customName)
            stack.set(BaCComponents.ADDITIONAL_ENTITY_DATA_COMPONENT, additionalEntityData)
        }

        user.inventory.selectedStack = stack.withItem(BaCItems.OCCUPIED_BEAKER_MAGNIFIER)
        entity.remove(Entity.RemovalReason.DISCARDED)

        return ActionResult.SUCCESS
    }
}
