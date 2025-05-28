package com.sarahisweird.buzzes_and_crawls.entities.ant.goals

import com.sarahisweird.buzzes_and_crawls.BaCBlocks
import com.sarahisweird.buzzes_and_crawls.blocks.AntHillBlock
import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity
import net.minecraft.block.Blocks
import net.minecraft.block.Segmented
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos

class BuildNestGoal(
    private val ant: AntEntity,
) : Goal() {
    companion object {
        const val DEFAULT_CHANCE = 0.1f
        val GATHER_TICKS = toGoalTicks(200)
        val BUILD_TICKS = toGoalTicks(200)
    }

    private enum class State {
        FINDING,
        GATHERING,
        RETURNING,
        BUILDING,
    }

    private val findMaterialGoal = FindMaterialGoal(ant, 1.0, 16)
    private val findNestGoal = FindNestGoal(ant, 1.0, 16)

    var chance = DEFAULT_CHANCE

    private var state = State.FINDING
    private var materialPos: BlockPos = BlockPos.ORIGIN
    private var nestPos: BlockPos = BlockPos.ORIGIN
    private var gatherTime = 0
    private var buildTime = 0
    private var done = false

    override fun canStart(): Boolean {
        if (ant.random.nextFloat() < chance) return false
        if (!findMaterialGoal.canStart()) return false
        if (!findNestGoal.canStart()) return false
        return true
    }

    override fun canStop(): Boolean {
        return state == State.FINDING
    }

    override fun shouldContinue(): Boolean {
        return when (state) {
            State.FINDING -> findMaterialGoal.shouldContinue()
            State.RETURNING -> findNestGoal.shouldContinue()
            else -> true
        }
    }

    override fun start() {
        done = false
        findMaterialGoal.start()
        state = State.FINDING
    }

    override fun stop() {
        ant.navigation.stop()
        findMaterialGoal.stop()
        findNestGoal.stop()
    }

    override fun shouldRunEveryTick(): Boolean {
        return true
    }

    private fun onTickFinding() {
        if (!findMaterialGoal.hasReached()) {
            findMaterialGoal.tick()
            return
        }

        materialPos = findMaterialGoal.targetPos.down()
        findMaterialGoal.stop()

        state = State.GATHERING
        gatherTime = 0
    }

    private fun removeMaterial() {
        if (ant.world.isClient) return

        val world = ant.world as ServerWorld
        val state = world.getBlockState(materialPos)
        if (!state.isOf(Blocks.LEAF_LITTER)) return

        val segments = state[Segmented.SEGMENT_AMOUNT]
        val newState = if (segments > 1) state.with(Segmented.SEGMENT_AMOUNT, segments - 1) else Blocks.AIR.defaultState
        world.setBlockState(materialPos, newState)
    }

    private fun onTickGathering() {
        if (gatherTime < GATHER_TICKS) {
            if (gatherTime % 4 == 0) {
                ant.spawnWorkParticles(Blocks.LEAF_LITTER)
            }

            if (gatherTime % 8 == 0) {
                ant.playWorkSound(SoundEvents.BLOCK_LEAF_LITTER_BREAK)
            }

            gatherTime++
            return
        }

        removeMaterial()

        state = State.RETURNING
        findNestGoal.start()
    }

    private fun onTickReturning() {
        if (!findNestGoal.hasReached()) {
            findNestGoal.tick()
            return
        }

        nestPos = findNestGoal.targetPos
        findNestGoal.stop()

        state = State.BUILDING
        buildTime = 0
    }

    private fun onTickBuilding() {
        if (buildTime < BUILD_TICKS) {
            if (buildTime % 4 == 0) {
                ant.spawnWorkParticles(BaCBlocks.ANTHILL)
            }

            if (buildTime % 8 == 0) {
                ant.playWorkSound(SoundEvents.BLOCK_ROOTED_DIRT_PLACE)
            }

            buildTime++
            return
        }

        if (done) return

        done = true

        val state = ant.world.getBlockState(nestPos)
        if (!state.isOf(BaCBlocks.ANTHILL)) return

        val layers = state[AntHillBlock.LAYERS]
        if (layers < AntHillBlock.MAX_LAYERS) {
            ant.world.setBlockState(nestPos, state.with(AntHillBlock.LAYERS, layers + 1))
            ant.updatePosition(ant.x, ant.y + 2.0 / 16.0, ant.z)
            return
        }

        val posAbove = nestPos.up()
        val stateAbove = ant.world.getBlockState(posAbove)

        if (stateAbove.isAir) {
            ant.world.setBlockState(posAbove, BaCBlocks.ANTHILL.defaultState)
            return
        }

        if (stateAbove.isOf(BaCBlocks.ANTHILL)) {
            val layersAbove = state[AntHillBlock.LAYERS]
            if (layersAbove < AntHillBlock.MAX_LAYERS) {
                ant.world.setBlockState(posAbove, state.with(AntHillBlock.LAYERS, layersAbove + 1))
            }

            return
        }
    }

    override fun tick() {
        when (state) {
            State.FINDING -> onTickFinding()
            State.GATHERING -> onTickGathering()
            State.RETURNING -> onTickReturning()
            State.BUILDING -> onTickBuilding()
        }
    }
}