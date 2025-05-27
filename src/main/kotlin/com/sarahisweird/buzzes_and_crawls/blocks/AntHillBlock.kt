package com.sarahisweird.buzzes_and_crawls.blocks

import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity
import net.minecraft.block.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView
import kotlin.math.min

/**
 * The anthill block. Its layer behavior is more or less copied straight from
 * [SnowBlock][net.minecraft.block.SnowBlock].
 *
 * Simply extending `SnowBlock` is infeasible for a few reasons, namely the inability to meaningfully change layer
 * logic, possibly breaking mod compatibility, and lack of TileEntity support.
 */
class AntHillBlock(settings: Settings) : Block(settings) {
    companion object {
        const val MAX_LAYERS = 8
        val LAYERS: IntProperty = IntProperty.of("hill_layers", 1, MAX_LAYERS)
        val SHAPES: Array<VoxelShape> = createShapeArray(MAX_LAYERS) { layerHeight ->
            createColumnShape(16.0, 0.0, layerHeight.toDouble() * 16.0 / MAX_LAYERS)
        }
    }

    init {
        defaultState = stateManager.defaultState.with(LAYERS, 1)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(LAYERS)
    }

    override fun canPathfindThrough(state: BlockState, type: NavigationType): Boolean {
        // Mobs should avoid pathfinding through the block entirely, unlike vanilla snow layers, where it's possible
        // if it's at max layers.
        return false
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return SHAPES[state[LAYERS]]
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val context = context as EntityShapeContext
        if (context.entity is AntEntity) return SHAPES[state[LAYERS]]
        return SHAPES[state[LAYERS] - 1]
    }

    override fun getSidesShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
        return SHAPES[state[LAYERS]]
    }

    override fun getCameraCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return SHAPES[state[LAYERS]]
    }

    override fun hasSidedTransparency(state: BlockState): Boolean {
        return true
    }

    override fun getAmbientOcclusionLightLevel(state: BlockState, world: BlockView, pos: BlockPos): Float {
        return if (state.get(LAYERS) == MAX_LAYERS) 0.2f else 1f
    }

    override fun canReplace(state: BlockState, context: ItemPlacementContext): Boolean {
        if (!context.stack.isOf(this.asItem())) return false
        if (state[LAYERS] >= MAX_LAYERS) return false

        // Not entirely sure what this call is for, but it's in the SnowBlock impl, so eh
        if (!context.canReplaceExisting()) return true

        // This is from the SnowBlock impl as well:
        // A layer can only be added if the clicked face is the UP face.
        // TODO: Is this what we want? Should we allow building up anthill piles from the side as well?
        return context.side == Direction.UP
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val existingState = ctx.world.getBlockState(ctx.blockPos)
        if (!existingState.isOf(this)) return super.getPlacementState(ctx)

        val newLayers = min(existingState[LAYERS] + 1, MAX_LAYERS)
        return existingState.with(LAYERS, newLayers)
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val stateBelow = world.getBlockState(pos.down())
        if (stateBelow.isOf(this) && stateBelow[LAYERS] == MAX_LAYERS) return true

        // Copied from SnowBlock. This call is weird! Looking at the name and reading through the impl,
        // I would have assumed that it only checks the upper face to be a full square, but ostensibly it
        // must also check somewhere that it's actually on the block boundary? That's the behavior we want,
        // so it's fine™️?
        return isFaceFullSquare(stateBelow.getCollisionShape(world, pos.down()), Direction.UP)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random
    ): BlockState {
        return if (canPlaceAt(state, world, pos)) {
            super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random)
        } else {
            Blocks.AIR.defaultState
        }
    }
}
