package com.sarahisweird.buzzes_and_crawls.util

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.WorldView
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkStatus

abstract class OpenedMoveToTargetPosGoal(
    entity: PathAwareEntity,
    speed: Double,
    range: Int,
    maxYDifference: Int = 1,
) : MoveToTargetPosGoal(entity, speed, range, maxYDifference) {
    public override fun findTargetPos(): Boolean {
        return super.findTargetPos()
    }

    public override fun hasReached(): Boolean {
        return super.hasReached()
    }

    public override fun getTargetPos(): BlockPos {
        return super.getTargetPos()
    }
}

abstract class MoveToTargetBlockGoal(
    entity: PathAwareEntity,
    speed: Double,
    range: Int,
    maxYDifference: Int = 1,
) : OpenedMoveToTargetPosGoal(entity, speed, range, maxYDifference) {
    abstract fun isTargetBlock(chunk: Chunk, pos: BlockPos): Boolean

    override fun isTargetPos(world: WorldView, pos: BlockPos): Boolean {
        val chunkX = ChunkSectionPos.getSectionCoord(pos.x)
        val chunkZ = ChunkSectionPos.getSectionCoord(pos.z)
        val chunk = world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false)
            ?: return false

        return isTargetBlock(chunk, pos)
    }
}
