package com.sarahisweird.buzzes_and_crawls.items

import com.sarahisweird.buzzes_and_crawls.BaCComponents
import com.sarahisweird.buzzes_and_crawls.BaCItems
import com.sarahisweird.buzzes_and_crawls.BuzzesAndCrawls
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Direction
import net.minecraft.world.event.GameEvent
import software.bernie.geckolib.animatable.GeoItem
import software.bernie.geckolib.animatable.client.GeoRenderProvider
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animatable.manager.AnimatableManager
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.function.Consumer

class OccupiedBeakerMagnifierItem(settings: Settings) : Item(settings), GeoItem {
    lateinit var renderProvider: GeoRenderProvider
    private val geoCache = GeckoLibUtil.createInstanceCache(this)

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val stack = context.stack

        val capturedEntity = stack.getOrDefault(BaCComponents.CAPTURED_ENTITY_COMPONENT, null)
            ?: return ActionResult.PASS

        val world = context.world
        if (world.isClient) return ActionResult.SUCCESS

        val blockPos = context.blockPos
        val side = context.side
        val blockState = world.getBlockState(blockPos)

        val spawnPos = if (blockState.getCollisionShape(world, blockPos).isEmpty) blockPos
                       else blockPos.offset(side)

        // Taken from SpawnEggItem.
        val spawnedEntity = capturedEntity.spawnFromItemStack(
            world as ServerWorld,
            context.stack,
            context.player,
            spawnPos,
            SpawnReason.SPAWN_ITEM_USE,
            true,
            (blockPos != spawnPos) and (side == Direction.UP)
        )

        if (spawnedEntity != null) {
            applyAdditionalEntityData(spawnedEntity, stack)
            stack.remove(BaCComponents.CAPTURED_ENTITY_COMPONENT)
            world.emitGameEvent(context.player, GameEvent.ENTITY_PLACE, blockPos)
        }

        val inventory = context.player?.inventory
        if (inventory != null) {
            inventory.selectedStack = stack.withItem(BaCItems.BEAKER_MAGNIFIER)
        } else {
            BuzzesAndCrawls.LOGGER.error("${this::class.simpleName} was used with player == null," +
                    " cannot replace stack with ${BeakerMagnifierItem::class.simpleName}." +
                    " Please file a bug report explaining how you caused this, so it can be fixed!")
        }

        return ActionResult.SUCCESS
    }

    private fun applyAdditionalEntityData(entity: Entity, stack: ItemStack) {
        if (entity !is LivingEntity) {
            if (stack.contains(BaCComponents.ADDITIONAL_ENTITY_DATA_COMPONENT)) {
                stack.remove(BaCComponents.ADDITIONAL_ENTITY_DATA_COMPONENT)
            }

            BuzzesAndCrawls.LOGGER.warn("Expected the spawned entity to be a LivingEntity," +
                    " not setting additional entity data. (got ${entity::class.java.simpleName})")
            return
        }

        val additionalEntityData = stack.getOrDefault(BaCComponents.ADDITIONAL_ENTITY_DATA_COMPONENT, null)
        if (additionalEntityData != null) {
            entity.customName = additionalEntityData.customName
            entity.health = additionalEntityData.health
            stack.remove(BaCComponents.ADDITIONAL_ENTITY_DATA_COMPONENT)
        }
    }

    override fun getName(stack: ItemStack): Text {
        val capturedEntity = stack.getOrDefault(BaCComponents.CAPTURED_ENTITY_COMPONENT, null)
            ?: return Text.translatable("item.buzzes_and_crawls.occupied_beaker_magnifier")

        return Text.translatable(
            "item.buzzes_and_crawls.occupied_beaker_magnifier.with_entity",
            capturedEntity.name
        )
    }

    override fun createGeoRenderer(consumer: Consumer<GeoRenderProvider>) {
        consumer.accept(renderProvider)
    }

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {}

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache {
        return geoCache
    }
}
