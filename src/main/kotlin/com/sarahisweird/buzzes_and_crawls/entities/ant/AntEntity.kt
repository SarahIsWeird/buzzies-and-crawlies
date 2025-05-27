package com.sarahisweird.buzzes_and_crawls.entities.ant

import com.sarahisweird.buzzes_and_crawls.entities.ant.goals.BuildNestGoal
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animatable.manager.AnimatableManager
import software.bernie.geckolib.animatable.processing.AnimationController
import software.bernie.geckolib.animatable.processing.AnimationTest
import software.bernie.geckolib.animation.PlayState
import software.bernie.geckolib.animation.RawAnimation
import software.bernie.geckolib.util.GeckoLibUtil

class AntEntity(entityType: EntityType<out AntEntity>, world: World) : PathAwareEntity(entityType, world), GeoEntity {
    companion object {
        private val IDLE_ANIMATION = RawAnimation.begin().thenLoop("move.idle")
        private val WALK_ANIMATION = RawAnimation.begin().thenLoop("move.walk")

        fun createAntAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 2.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.SCALE, 0.8)
                .add(EntityAttributes.FALL_DAMAGE_MULTIPLIER, 0.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 1.0)
        }
    }

    val currentSize: Double
        get() = width * attributes.getValue(EntityAttributes.SCALE)

    private val geoCache = GeckoLibUtil.createInstanceCache(this)

    private fun movementAnimController(animationTest: AnimationTest<AntEntity>): PlayState {
        if (animationTest.isMoving) return animationTest.setAndContinue(WALK_ANIMATION)
        return animationTest.setAndContinue(IDLE_ANIMATION)
    }

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {
        controllers.add(
            AnimationController("Movement", 5, ::movementAnimController)
        )
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache {
        return geoCache
    }

    override fun initGoals() {
        goalSelector.add(10, BuildNestGoal(this))
        // goalSelector.add(10, WanderAroundGoal(this, 1.0))
        // goalSelector.add(0, LookAroundGoal(this))
    }
}
