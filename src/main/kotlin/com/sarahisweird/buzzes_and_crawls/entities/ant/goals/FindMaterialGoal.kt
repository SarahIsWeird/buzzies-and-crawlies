package com.sarahisweird.buzzes_and_crawls.entities.ant.goals

import com.sarahisweird.buzzes_and_crawls.entities.ant.AntEntity
import com.sarahisweird.buzzes_and_crawls.util.MoveToTargetBlockGoal
import net.minecraft.block.Blocks
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk

class FindMaterialGoal(
    ant: AntEntity,
    speed: Double,
    range: Int,
) : MoveToTargetBlockGoal(ant, speed, range) {
    override fun isTargetBlock(chunk: Chunk, pos: BlockPos): Boolean {
        // TODO: Make a tag for this
        return chunk.getBlockState(pos).isOf(Blocks.LEAF_LITTER) && chunk.getBlockState(pos.up()).isAir
    }

    override fun getInterval(mob: PathAwareEntity): Int {
        return 0
    }

    override fun getDesiredDistanceToTarget(): Double {
        return 2.0
    }
}
