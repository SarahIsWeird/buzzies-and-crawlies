package com.sarahisweird.buzzes_and_crawls.entities.ant

import com.sarahisweird.buzzes_and_crawls.entities.ant.goals.FindFoodGoal
import com.sarahisweird.buzzes_and_crawls.util.minus
import com.sarahisweird.buzzes_and_crawls.util.plus
import com.sarahisweird.buzzes_and_crawls.util.times
import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.pathing.EntityNavigation
import net.minecraft.entity.ai.pathing.SpiderNavigation
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animatable.manager.AnimatableManager
import software.bernie.geckolib.animatable.processing.AnimationController
import software.bernie.geckolib.animatable.processing.AnimationTest
import software.bernie.geckolib.animation.PlayState
import software.bernie.geckolib.animation.RawAnimation
import software.bernie.geckolib.util.GeckoLibUtil
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class AntEntity(entityType: EntityType<out AntEntity>, world: World) : PathAwareEntity(entityType, world), GeoEntity {
    companion object {
        private val IDLE_ANIMATION = RawAnimation.begin().thenLoop("move.idle")
        private val WALK_ANIMATION = RawAnimation.begin().thenLoop("move.walk")

        private val ANT_FLAGS: TrackedData<Byte> = DataTracker.registerData(AntEntity::class.java,
            TrackedDataHandlerRegistry.BYTE)
        private const val CLIMB_MASK: Byte = 0x01

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
//        goalSelector.add(0, LookAroundGoal(this))
//        goalSelector.add(1, WanderAroundGoal(this, 1.0))
//         goalSelector.add(10, BuildNestGoal(this))
        goalSelector.add(10, FindFoodGoal(this))
    }

    override fun createNavigation(world: World): EntityNavigation {
        return SpiderNavigation(this, world)
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
        builder.add(ANT_FLAGS, 0)
    }

    private var isClimbingWall: Boolean
        get() = (dataTracker.get(ANT_FLAGS) and CLIMB_MASK) != 0.toByte()
        set(isClimbing) {
            var flags = dataTracker.get(ANT_FLAGS)
            flags = if (isClimbing) {
                flags or CLIMB_MASK
            } else {
                flags and (CLIMB_MASK.inv())
            }

            dataTracker.set(ANT_FLAGS, flags)
        }

    override fun tick() {
        super.tick()
        if (world.isClient) return

        isClimbingWall = horizontalCollision
    }

    override fun isClimbing(): Boolean {
        return isClimbingWall
    }

    fun spawnWorkParticles(item: ItemConvertible) {
        if (world.isClient) return

        val world = world as ServerWorld
        val effect = ItemStackParticleEffect(ParticleTypes.ITEM, ItemStack(item))

        val pos = eyePos + rotationVector.normalize() * (currentSize / 2.0) - Vec3d(0.0, 3.0 / 16.0, 0.0)
        val offset = Vec3d(
            (random.nextDouble() - 0.5) * 0.08,
            (random.nextDouble() - 0.5) * 0.08,
            (random.nextDouble() - 0.5) * 0.08,
        )

        world.spawnParticles(
            effect,
            pos.x, pos.y, pos.z,
            2,
            offset.x, offset.y, offset.z,
            0.05
        )
    }

    fun playWorkSound(sound: SoundEvent) {
        if (world.isClient) return

        val world = world as ServerWorld
        world.playSound(this, blockPos, sound, SoundCategory.BLOCKS, 0.5f, 1.5f)
    }
}
