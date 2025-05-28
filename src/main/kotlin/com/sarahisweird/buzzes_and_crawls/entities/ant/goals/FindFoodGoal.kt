package com.sarahisweird.buzzes_and_crawls.entities.ant.goals

import com.sarahisweird.buzzes_and_crawls.BaCEntities
import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity
import com.sarahisweird.buzzes_and_crawls.util.getEntitiesByClass
import com.sarahisweird.buzzes_and_crawls.util.plus
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class FindFoodGoal(
    private val ant: AntEntity
) : Goal() {
    companion object {
        private const val SPAWN_CHANCE = 0.5f

        private const val SEARCH_RADIUS_XZ = 16.0
        private const val SEARCH_RADIUS_Y = 8.0

        private val COOLDOWN = toGoalTicks(20 * 30)
        private val GATHER_TICKS = toGoalTicks(20 * 3)
        private val FEED_TICKS = toGoalTicks(20 * 3)

        private fun isFoodItem(itemEntity: ItemEntity): Boolean {
            // TODO: Add a tag for this
            /* TODO: What items should ants pick up?
                - Chicken
                - Other meats?
                - Berries
                - Other plants?
                - Dropped leaf litter?
                    - Should this rather go into BuildNestGoal?
             */
            return itemEntity.stack.isOf(Items.CHICKEN)
        }
    }

    private enum class State {
        FINDING,
        GATHERING,
        RETURNING,
        FEEDING,
    }

    private val findNestGoal = FindNestGoal(ant, 1.0, 16)
    private var nestPos = BlockPos.ORIGIN

    private var state: State? = null
    private var remainingCooldown = 0
    private var gatherTime = 0
    private var feedingTime = 0
    private var itemEntity: ItemEntity? = null
    private var foodItem: Item = Items.AIR
    private var done = false

    override fun canStart(): Boolean {
        if (remainingCooldown > 0) {
            remainingCooldown--
            return false
        }

        itemEntity = getClosestFoodItem() ?: return false
        itemEntity?.stack?.item?.let { foodItem = it }
        return findNestGoal.canStart()
    }

    override fun start() {
        ant.navigation.startMovingTo(itemEntity, 1.0)
        state = State.FINDING
        itemEntity?.setCovetedItem()
        done = false
    }

    override fun canStop(): Boolean {
        return itemEntity == null || state == State.FINDING
    }

    override fun stop() {
        findNestGoal.stop()
        ant.navigation.stop()
        itemEntity = null
        state = null
        done = true
    }

    override fun shouldRunEveryTick(): Boolean {
        return true
    }

    private fun tickFinding() {
        if (itemEntity?.isRemoved == true) {
            this.stop()
            return
        }

        if ((itemEntity?.distanceTo(ant) ?: Float.MAX_VALUE) > 1f) {
            ant.navigation.recalculatePath()
            ant.navigation.tick()
            return
        }

        ant.navigation.stop()
        state = State.GATHERING
        gatherTime = 0
    }

    private fun tickGathering() {
        if (itemEntity?.isRemoved == true) {
            this.stop()
            return
        }

        if (gatherTime < GATHER_TICKS) {
            if (gatherTime % 4 == 0) {
                ant.spawnWorkParticles(foodItem)
            }

            if (gatherTime % 8 == 0) {
                ant.playWorkSound(SoundEvents.ENTITY_GENERIC_EAT.value())
            }

            gatherTime++
            return
        }

        val stack = itemEntity?.stack ?: ItemStack(Items.AIR)
        if (stack.count == 1) {
            itemEntity?.remove(Entity.RemovalReason.KILLED)
        } else {
            itemEntity?.stack?.decrement(1)
            itemEntity?.resetPickupDelay()
        }

        state = State.RETURNING
        findNestGoal.start()
    }

    private fun tickReturning() {
        if (!findNestGoal.hasReached()) {
            findNestGoal.tick()
            return
        }

        nestPos = findNestGoal.targetPos
        findNestGoal.stop()
        state = State.FEEDING
        feedingTime = 0
    }

    private fun tickFeeding() {
        if (feedingTime < FEED_TICKS) {
            if (feedingTime % 4 == 0) {
                ant.spawnWorkParticles(foodItem)
            }

            if (feedingTime % 8 == 0) {
                ant.playWorkSound(SoundEvents.ENTITY_GENERIC_EAT.value())
            }

            feedingTime++
            return
        }

        if (done) return
        done = true

        if (ant.world.isClient) return
        if (ant.random.nextFloat() > SPAWN_CHANCE) return

        val newAnt = AntEntity(BaCEntities.ANT, ant.world)
        newAnt.setPosition(nestPos.toBottomCenterPos() + Vec3d(0.0, 1.0, 0.0))
        ant.world.spawnEntity(newAnt)

        remainingCooldown = COOLDOWN
    }

    override fun tick() {
        when (state) {
            State.FINDING -> tickFinding()
            State.GATHERING -> tickGathering()
            State.RETURNING -> tickReturning()
            State.FEEDING -> tickFeeding()
            null -> {}
        }
    }

    private fun getClosestFoodItem(): ItemEntity? {
        val searchBox = ant.boundingBox.expand(SEARCH_RADIUS_XZ, SEARCH_RADIUS_Y, SEARCH_RADIUS_XZ)

        var closestItemEntity: ItemEntity? = null
        var closestDistance: Float = Float.MAX_VALUE
        for (itemEntity in ant.world.getEntitiesByClass<ItemEntity>(searchBox, FindFoodGoal::isFoodItem)) {
            val distance = ant.distanceTo(itemEntity)

            if (closestDistance < distance) continue
            closestItemEntity = itemEntity
            closestDistance = distance
        }

        return closestItemEntity
    }
}
