package com.sarahisweird.buzzes_and_crawls.entities.ant.goals

import com.sarahisweird.buzzes_and_crawls.BaCBlocks
import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity
import com.sarahisweird.buzzes_and_crawls.util.MoveToTargetBlockGoal
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk

class FindNestGoal(
    ant: AntEntity,
    speed: Double,
    range: Int
) : MoveToTargetBlockGoal(ant, speed, range) {
    override fun isTargetBlock(chunk: Chunk, pos: BlockPos): Boolean {
        return chunk.getBlockState(pos).isOf(BaCBlocks.ANTHILL)
                && chunk.getBlockState(pos.up()).isAir
    }

    override fun getInterval(mob: PathAwareEntity): Int {
        return 0
    }

    override fun getDesiredDistanceToTarget(): Double {
        return 2.0
    }

    override fun getTargetPos(): BlockPos {
        return super.getTargetPos().down()
    }
}
